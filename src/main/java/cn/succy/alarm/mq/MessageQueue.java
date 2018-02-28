package cn.succy.alarm.mq;

/**
 * 定义内存消息队列接口
 * 注：该消息队列适用场景仅限于堆积量少，无HA，消息无持久化的内存消息队列场景
 * 该设计参照生产者消费者模式
 * @author  Succy
 * create on 2018/02/28
 */
public interface MessageQueue<T> {
    /**
     * 将一个消息发布到消息队列
     * @param msg 要发布的消息
     */
    boolean push(T msg);

    /**
     * 从消息队列中拉取一个消息进行消费
     * @return 消息对象
     */
    T pull();

    /**
     * 获取消息队列的长度
     */
    int size();

}
