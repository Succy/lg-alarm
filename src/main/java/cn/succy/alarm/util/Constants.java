package cn.succy.alarm.util;

/**
 * Created by succy on 18-2-26.
 */
public interface Constants {
    /**
     * email 相关配置常量
     */
     interface Email{
        String USERNAME = "username";
        String PASSWORD = "password";
        String HOSTNAME = "hostname";
        String PORT = "port";
        String CHARSET = "charset";
        String SSL = "ssl";
        String SSL_POEMAIL_PORT = "ssl.port";
    }


    /**
     * 微信相关配置常量
     */
    interface WeChat{
        String PARTY_ID = "partyid";
        String CORP_ID = "corpid";
        String CORP_SECRET = "corpsecret";
        String ACCESS_TOKEN = "access_token";
        String EXPIRES_IN = "expires_in";
        String AGENT_ID = "agentid";
        String MESSAGE_TYPE = "text";

        String URL_GET_TOKEN = "https://qyapi.weixin.qq.com/cgi-bin/gettoken";
        String URL_SEND_MSG = "https://qyapi.weixin.qq.com/cgi-bin/message/send";

        String ERR_CODE = "errcode";
        int SUCCESS_CODE = 0;
        int INVALID_TOKEN_CODE = 40014;
    }

    /**
     * 分组名常量
     */
    String SETTING_GROUP_EMAIL = "email";
    String SETTING_GROUP_WECHAT = "wechat";
    /**
     * 全局setting常量
     */
    String SETTING_SENDER = "sender";
    String SETTING_THREAD_POOL_SIZE = "thread.pool.size";
    String SETTING_MESSAGE_QUEUE_SIZE = "message.queue.size";

    String ALARM_SYS_NAME = "质量数据采集系统告警";
}
