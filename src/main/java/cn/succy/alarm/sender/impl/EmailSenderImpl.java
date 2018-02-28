package cn.succy.alarm.sender.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.setting.Setting;
import cn.succy.alarm.model.Contact;
import cn.succy.alarm.sender.Sender;
import cn.succy.alarm.template.TemplateManager;
import cn.succy.alarm.template.TemplateModel;
import cn.succy.alarm.util.Constants;
import cn.succy.alarm.util.ContactsParser;
import cn.succy.alarm.util.SettingManager;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 邮件发送器实现类
 *
 * @author Succy
 * @date 2017-10-13 11:36
 **/

public class EmailSenderImpl implements Sender {
    private static final Logger logger = LoggerFactory.getLogger(EmailSenderImpl.class);
    private Setting emailSetting = SettingManager.getSetting(Constants.SETTING_GROUP_EMAIL);
    private Map<String, List<Contact>> recvMap = ContactsParser.getRecvMap();



    @Override
    public void send(TemplateModel model) {
        String appName = model.getAppName();
        if (!recvMap.containsKey(appName)) {
            logger.warn("could not found modelName:{} in recvMap", appName);
            return;
        }

        HtmlEmail email = new HtmlEmail();
        // 邮件服务器域名
        email.setHostName(emailSetting.getStr(Constants.Email.HOSTNAME));
        // 邮件服务器smtp协议端口
        email.setSmtpPort(emailSetting.getInt(Constants.Email.PORT));
        // 邮箱账户
        email.setAuthentication(emailSetting.getStr(Constants.Email.USERNAME), emailSetting.getStr(Constants.Email.PASSWORD));
        // 邮件的字符集
        email.setCharset(emailSetting.getStr(Constants.Email.CHARSET));
        boolean useSSL = emailSetting.getBool(Constants.Email.SSL);
        // 是否开启ssl
        email.setSSLOnConnect(useSSL);
        if (useSSL) {
            email.setSslSmtpPort(emailSetting.getStr(Constants.Email.SSL_POEMAIL_PORT));
        }
        Set<String> emailSet = recvMap.get(appName).stream().map(Contact::getEmail).collect(Collectors.toSet());

        try {
            email.setFrom(emailSetting.getStr(Constants.Email.USERNAME), model.getAlarmName());
            for (String to : emailSet) {
                email.addTo(to);
            }
            String template = TemplateManager.getTemplateMsg(model);
            template = template.replaceAll(StrUtil.LF, "<br/>").replaceAll("\\t", "&nbsp;&nbsp;");
            email.setSubject(model.getAlarmName());
            email.setHtmlMsg(template);
            email.send();
            logger.debug("EmailSender has send a email");
        } catch (EmailException e) {
            logger.error("send email failure", e);
            throw new RuntimeException(e);
        }
    }
}
