package cn.succy.alarm;

import cn.succy.alarm.mq.AlarmMqConsumerStarter;
import com.blade.Blade;
import com.blade.event.EventType;

/**
 * 报警服务启动类
 *
 * @author Succy
 * @date 2018-02-24 20:34
 **/

public class AlarmApplication {

    public static void main(String[] args) {

        Blade.me()
                .threadName("main")
                .event(EventType.SERVER_STARTED, e -> {
                    // 当Blade服务器启动成功之后，开启警报消息队列的消费者线程
                    new AlarmMqConsumerStarter().start();
                })
                .start(AlarmApplication.class, args);

    }
}
