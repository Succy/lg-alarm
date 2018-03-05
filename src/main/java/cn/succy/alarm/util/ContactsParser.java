package cn.succy.alarm.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.watch.SimpleWatcher;
import cn.hutool.core.io.watch.WatchMonitor;
import cn.hutool.core.io.watch.watchers.DelayWatcher;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import cn.succy.alarm.model.Contact;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Succy
 * @date 2018-02-24 21:30
 **/

public class ContactsParser {
    private static final Log logger = LogFactory.get();

    private static Map<String, List<Contact>> recvMap = new HashMap<>(16);
    private static final String CONTACTS_FILE_PATH = "contacts.json";
    private static final String KEY_PROD_LINES = "prodLines";
    private static final String KEY_CONTACTS = "contacts";


    public static Map<String, List<Contact>> getRecvMap() {
        if (recvMap != null && recvMap.size() > 0) {
            return recvMap;
        }
        return load();
    }

    private static Map<String, List<Contact>> load() {
        logger.debug("load contacts json file");
        File jsonFile = FileUtil.file(CONTACTS_FILE_PATH);
        // 使用延迟处理监听事件，避免多次触发modify事件
        WatchMonitor.createAll(jsonFile, new DelayWatcher(new SimpleWatcher() {
            @Override
            public void onModify(WatchEvent<?> event, Path currentPath) {
                logger.debug("{} has been modified, reload it", jsonFile.getName());
                loadFromJson();
                logger.debug("recv map => {}", recvMap);
            }
        }, 500)).start();

        if (recvMap == null || recvMap.size() <= 0) {
            loadFromJson();
        }

        return recvMap;
    }

    @SuppressWarnings("unchecked")
    private static void loadFromJson() {
       // JSONObject
        JSONObject root = JSON.parseObject(FileUtil.readString(FileUtil.file(CONTACTS_FILE_PATH), CharsetUtil.CHARSET_UTF_8));
        JSONObject prodLinesJson = root.getJSONObject(KEY_PROD_LINES);
        JSONObject contactsJson = root.getJSONObject(KEY_CONTACTS);
        // 清空Map，防止出现意外情况
        recvMap.clear();
        for (Map.Entry<String, Object> entry : prodLinesJson.entrySet()) {
            String prodLine = entry.getKey();
            List<String> values = CollUtil.distinct((List<String>) entry.getValue());
            List<Contact> contacts = new ArrayList<>();
            for (String value : values) {
                Contact contact = contactsJson.getObject(value, Contact.class);
                if (contact != null) {
                    contacts.add(contact);
                }
            }
            recvMap.put(prodLine, contacts);
        }
    }
}
