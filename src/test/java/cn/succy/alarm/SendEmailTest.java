package cn.succy.alarm;

import cn.hutool.http.HttpUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by succy on 18-2-26.
 */
public class SendEmailTest {


    public static void main(String[] args) {
       // Alarm.debug("lg", "炼钢测试");
        for (int i = 0; i < 150; i++) {
            new Thread(new AlarmTask(), "test-thread" + i).start();
        }
    }

    static class AlarmTask implements Runnable {
        @Override
        public void run() {
            Map<String, Object> param = new HashMap<>();
            param.put("level", "deBug");
            param.put("modelName", "lg");
            param.put("content", "联系人热加载测试");
            String result = HttpUtil.post("http://127.0.0.1:9000/alarm/send", param);
            System.out.println(result);
        }
    }
}
