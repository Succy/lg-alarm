package cn.succy.alarm;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.NetUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import cn.hutool.setting.Setting;
import cn.succy.alarm.sender.Sender;
import cn.succy.alarm.sender.SenderFactory;
import cn.succy.alarm.template.TemplateModel;
import cn.succy.alarm.util.Constants;
import cn.succy.alarm.util.SettingManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * 警报
 *
 * @author Succy
 * @date 2017-10-13 17:16
 **/

public class Alarm {
    private static final Log logger = LogFactory.get();
    private static Setting setting = SettingManager.get();
    private static final List<Sender> SENDER_LIST = new ArrayList<>();

    static {
        String senderKeyStr = setting.getStr(Constants.SETTING_SENDER);

        List<String> senderKeys = StrUtil.split(senderKeyStr, StrUtil.C_COMMA);
        logger.info("sender: {}", senderKeys);
        for (String key : senderKeys) {
            Sender sender = SenderFactory.getSender(key);
            SENDER_LIST.add(sender);
        }

    }

    public static void info(String modelName, String content) {
        info(modelName, content, null);
    }

    /**
     * info级别警报
     *
     * @param modelName 告警模块
     * @param content   警报内容
     */
    public static void info(String modelName, String content, Throwable e) {
        send(Level.INFO, modelName, content, e);
    }

    public static void debug(String modelName, String content) {
        debug(modelName, content, null);
    }

    /**
     * debug级别警报
     *
     * @param modelName 告警模块
     * @param content   警报内容
     */
    public static void debug(String modelName, String content, Throwable e) {
        send(Level.DEBUG, modelName, content, e);
    }

    public static void warn(String modelName, String content) {
        warn(modelName, content, null);
    }

    /**
     * warn级别警报
     *
     * @param modelName 告警模块
     * @param content   警报内容
     */
    public static void warn(String modelName, String content, Throwable e) {
        send(Level.WARN, modelName, content, e);
    }

    public static void error(String modelName, String content) {
        error(modelName, content, null);
    }

    /**
     * error级别警报
     *
     * @param modelName 告警模块
     * @param content   警报内容
     */
    public static void error(String modelName, String content, Throwable e) {
        send(Level.ERROR, modelName, content, e);
    }

    private static void send(Level level, String modelName, String content, Throwable e) {
        TemplateModel model = new TemplateModel();
        model.setAlarmName(Constants.ALARM_SYS_NAME);
        model.setAppName(modelName);
        model.setContent(content);
        model.setDateTime(DateTime.now().toString());
        model.setLevel(level);
        model.setException(e);

        // 获取当前机器的Ip
        String localhostStr = NetUtil.getLocalhostStr();
        model.setHost(localhostStr);

        String traceStack = (e == null) ? ThreadUtil.getStackTraceElement(6).toString()
                : e.getStackTrace()[0].toString();
        model.setTraceStack(traceStack);
        send(model);
    }

    public static void send(TemplateModel model) {
        // 测试使用同步发送，使用线程池发送的话，消息队列的堆积就没有什么用了，一下子发送太多邮箱会爆
        for (Sender sender : SENDER_LIST) {
            sender.send(model);
        }
    }

}
