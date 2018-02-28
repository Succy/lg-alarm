package cn.succy.alarm.mq;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import cn.succy.alarm.Alarm;
import cn.succy.alarm.template.TemplateModel;

public class AlarmMqConsumerStarter extends Thread {
    private static final Log logger = LogFactory.get();

    public AlarmMqConsumerStarter() {
        super("alarm-consumer-thread");
    }

    @Override
    public void run() {
        logger.info("alarm mq consumer startup...");

        while (true) {
            try {
                TemplateModel model = AlarmMessageQueue.me().pull();
                if (model != null) {
                    Alarm.send(model);
                }
            }catch (Exception e) {
                // 该出捕获异常是因为：当邮件发送失败时会抛出异常，如果不捕获，这里将跳出循环
                // 队列中其他等待发送的消息就不会继续发送了。
                logger.warn(e.getMessage());
            }
        }
    }
}
