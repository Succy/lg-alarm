package cn.succy.alarm;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Console;
import cn.hutool.json.JSONUtil;
import cn.succy.alarm.model.Contact;
import cn.succy.alarm.util.ContactsParser;
import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Succy
 * @date 2018-02-24 21:58
 **/

public class ContactsParserTest {
    @Test
    public void testParse() {
        String json = "{\"contact_id\": \"123\"}";
        Console.log("{}", json);
        Contact contact = BeanUtil.fillBeanWithMap(JSONUtil.parseObj(json), new Contact(), true,true);
        Console.log(contact);
    }

    public static void main(String[] args) throws Exception {
        while (true) {
            Map<String, List<Contact>> recvMap = ContactsParser.getRecvMap();
            System.out.println(recvMap);
            Thread.sleep(1000);
        }

    }

    @Test
    public void testGetWxId() {
        Map<String, List<Contact>> recvMap = ContactsParser.getRecvMap();
        String modelAppName = "lg";
        List<Contact> contacts = recvMap.get(modelAppName);
        Set<String> weChatSet = contacts.stream().map(Contact::getWxId).collect(Collectors.toSet());
        System.out.println(weChatSet);

    }

    @Test
    public void testGetRecvMap() {
        Map<String, List<Contact>> recvMap = ContactsParser.getRecvMap();
        Console.log(recvMap);
    }
}
