package cn.succy.alarm.sender.impl.wechat;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.hutool.setting.Setting;
import cn.succy.alarm.util.Constants;
import cn.succy.alarm.util.ContactsParser;
import cn.succy.alarm.util.SettingManager;
import cn.succy.alarm.model.Contact;
import cn.succy.alarm.sender.Sender;
import cn.succy.alarm.sender.impl.wechat.util.TokenUtil;
import cn.succy.alarm.template.TemplateManager;
import cn.succy.alarm.template.TemplateModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 微信发送器实现类
 *
 * @author Succy
 * @date 2017-10-13 11:37
 **/

public class WeChatSenderImpl implements Sender {
    private static final Logger logger = LoggerFactory.getLogger(WeChatSenderImpl.class);
    private Setting wxSetting = SettingManager.getSetting(Constants.SETTING_GROUP_WECHAT);
    private Map<String, List<Contact>> recvMap = ContactsParser.getRecvMap();

    @Override
    public void send(TemplateModel model) {
        String modelAppName = model.getAppName();
        if (!recvMap.containsKey(modelAppName)) {
            return;
        }

        List<Contact> contacts = recvMap.get(modelAppName);
        Set<String> weChatSet = contacts.stream().map(Contact::getWxId).collect(Collectors.toSet());
        CollUtil.removeBlank(weChatSet);
        String touser = CollUtil.join(weChatSet, "|");

        TextMessage message = new TextMessage();
        message.setAgentid(wxSetting.getInt(Constants.WeChat.AGENT_ID));
        message.setTouser(touser);
        // 设置整个部门发送
        String partyId = wxSetting.getStr(Constants.WeChat.PARTY_ID, "");
        if (StrUtil.isNotBlank(partyId)) {
            message.setToparty(partyId);
        }
        TextMessage.Text text = new TextMessage.Text();
        text.setContent(TemplateManager.getTemplateMsg(model));
        message.setText(text);

        JSON msgJson = JSONUtil.parse(message);
        String response = HttpRequest.post(Constants.WeChat.URL_SEND_MSG + "?access_token=" + TokenUtil.getAccessToken())
                .body(msgJson)
                .execute()
                .body();
        JSONObject respJson = JSONUtil.parseObj(response);
        // 如果不成功，则重试
        int errcode = respJson.getInt(Constants.WeChat.ERR_CODE);
        if (errcode != Constants.WeChat.SUCCESS_CODE) {
            // 如果是token失效，则刷新token
            if (errcode == Constants.WeChat.INVALID_TOKEN_CODE) {
                TokenUtil.initToken();
            }
            // 如果再一次重试不成功，直接抛出运行时异常
            retry(msgJson);
        } else {
            logger.debug("send wechat message success; resp={}", respJson);
        }

    }

    /**
     * 重试机制，如果遇到token失效，则刷新token，重试一次。或者其他异常，也重试一次
     * 如果重试一次之后，还是不成功，直接抛异常了。
     *
     * @param request 请求体json
     */
    private void retry(JSON request) {
        String response = HttpRequest.post(Constants.WeChat.URL_SEND_MSG)
                .body(request)
                .execute()
                .body();
        JSONObject respJson = JSONUtil.parseObj(response);

        if (respJson.getInt(Constants.WeChat.ERR_CODE) == Constants.WeChat.SUCCESS_CODE) {
            logger.debug("send wechat message success; resp=>{}", respJson);
        } else {
            logger.error("send wechat message failure; resp={}", respJson);
            throw new RuntimeException("send wechat message failure");
        }
    }
}
