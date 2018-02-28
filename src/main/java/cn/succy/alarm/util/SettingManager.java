package cn.succy.alarm.util;

import cn.hutool.setting.Setting;

/**
 * Created by succy on 18-2-26.
 */
public class SettingManager {
    private static Setting SETTING;
    private static final String ALARM_SETTING = "alarm.setting";

    static {
        SETTING = new Setting(ALARM_SETTING);
        SETTING.autoLoad(true);
    }

    /**
     * 获取整个Setting
     */
    public static Setting get() {
        return SETTING;
    }

    /**
     * 根据分组获取分组的Setting
     * @param group 分组
     */
    public static Setting getSetting(String group) {
        return SETTING.getSetting(group);
    }
}
