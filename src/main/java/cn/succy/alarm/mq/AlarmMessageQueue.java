package cn.succy.alarm.mq;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import cn.hutool.setting.Setting;
import cn.succy.alarm.template.TemplateModel;
import cn.succy.alarm.util.Constants;
import cn.succy.alarm.util.SettingManager;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 警报消息队列，实现自MessageQueue接口
 * 内部通过BlockingQueue实现
 */
public class AlarmMessageQueue<T> implements MessageQueue<T> {
    private static final Log logger = LogFactory.get();
    private static AlarmMessageQueue<TemplateModel> alarmMessageQueue = null;

    private BlockingQueue<T> blockingQueue;

    public AlarmMessageQueue() {
        Setting alarmSetting = SettingManager.get();
        Integer size = alarmSetting.getInt(Constants.SETTING_MESSAGE_QUEUE_SIZE, 0);
        int defaultSize = 100;
        if (size != 0) {
            defaultSize = size;
        }
        logger.info("Max message queue size: {}", defaultSize);
        this.blockingQueue = new LinkedBlockingQueue<>(defaultSize);
    }

    public static AlarmMessageQueue<TemplateModel> me() {
        if (null == alarmMessageQueue) {
            alarmMessageQueue = new AlarmMessageQueue<>();
        }

        return alarmMessageQueue;
    }

    @Override
    public boolean push(T msg) {
        try {
            // 当可用空间为0时，阻塞等待有空间再插入
            this.blockingQueue.put(msg);
        } catch (InterruptedException e) {
            logger.error("AlarmMessageQueue push element failure");
            return false;
        }
        return true;
    }

    @Override
    public T pull() {
        try {
            // 当队列中没有可读消息时，阻塞等待
            return this.blockingQueue.take();
        } catch (InterruptedException e) {
            logger.error("AlarmMessageQueue pull element failure");
        }
        return null;
    }

    @Override
    public int size() {
        return this.blockingQueue.size();
    }
}
