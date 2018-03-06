package cn.succy.alarm;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.StrUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import cn.succy.alarm.model.Contact;
import cn.succy.alarm.model.ParamModel;
import cn.succy.alarm.mq.AlarmMessageQueue;
import cn.succy.alarm.template.TemplateModel;
import cn.succy.alarm.util.AlarmUtil;
import cn.succy.alarm.util.Constants;
import cn.succy.alarm.util.ContactsParser;
import cn.succy.alarm.vo.Result;
import com.blade.mvc.annotation.JSON;
import com.blade.mvc.annotation.Param;
import com.blade.mvc.annotation.Path;
import com.blade.mvc.annotation.PostRoute;
import com.blade.mvc.http.Request;

import java.util.List;
import java.util.Map;

/**
 * Created by succy on 18-2-26.
 */
@Path
public class AlarmController {
    private static final Log logger = LogFactory.get();

    @PostRoute("/alarm/send")
    @JSON
    public Result sendAlarm(@Param ParamModel model, Request request) {
        Result result = new Result();
        // 获取模块名称，并且忽略大小写
        String modelName = model.getModelName().toLowerCase();

        // 找不到对应的modelName情况
        if (StrUtil.isBlank(AlarmUtil.getModelName(modelName))) {
            result.setCode(-1);
            result.setMsg(String.format("警报发送失败！原因：无效modelName=%s,请正确填写(lg|zb|bx|rz|lz)其中一个。", modelName));
            return result;
        }

        // 找不到对应level情况
//        Level level = AlarmUtil.getLevel(model.getLevel());
//        if (level == null) {
//            result.setCode(-2);
//            result.setMsg(String.format("警报发送失败！原因：无效警报级别level=%s,请正确填写(error|info|warn|debug)其中一个。", model.getLevel()));
//            return result;
//        }

        // 模块对应联系人未配置情况
        Map<String, List<Contact>> recvMap = ContactsParser.getRecvMap();
        List<Contact> contacts = recvMap.get(modelName);
        if (contacts.size() == 0) {
            result.setCode(-3);
            result.setMsg(String.format("警报发送失败！原因：告警模块:%s 对应接收联系人列表未配置,请联系管理人员进行配置。", AlarmUtil.getModelName(modelName)));
            return result;
        }

        TemplateModel templateModel = new TemplateModel();
        String remoteAddress = request.host();
        templateModel.setHost(remoteAddress);
        templateModel.setLevel(model.getLevel());
        templateModel.setAlarmName(model.getAlarmName());
        templateModel.setContent(model.getContent());
        templateModel.setAppName(modelName);
        templateModel.setDateTime(DateTime.now().toString());

        //Alarm.send(templateModel);
        AlarmMessageQueue.me().push(templateModel);
        int size = AlarmMessageQueue.me().size();
        logger.debug("message queue size: {}", size);
        result.setCode(0);
        result.setMsg("警报发送成功，请注意查收。");
        return result;
    }
}
