package cn.succy.alarm.model;

import java.io.Serializable;

/**
 * 联系人实体类
 *
 * @author Succy
 * @date 2018-02-24 21:33
 **/

public class Contact implements Serializable {
    /**
     * 联系人Id
     */
    private String contactId;
    /**
     * 接收联系人姓名
     */
    private String name;
    /**
     * 接收联系人邮箱号
     */
    private String email;
    /**
     * 接收联系人微信id
     */
    private String wxId;

    public String getContactId() {
        return contactId;
    }

    public void setContactId(String contactId) {
        this.contactId = contactId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getWxId() {
        return wxId;
    }

    public void setWxId(String wxId) {
        this.wxId = wxId;
    }

    @Override
    public String toString() {
        return "Contact{" +
                "contactId='" + contactId + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", wxId='" + wxId + '\'' +
                '}';
    }
}
