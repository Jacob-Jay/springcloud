package com.jq.busbyrabbit.rabbit;

/**
 * @author Jiangqing
 * @version 1.0
 * @since 2019-09-28 11:10
 */
public enum ChannelEnum {




    HELLO("hello","测试channel");

    private String name;
    private String desc;

    ChannelEnum(String name, String desc) {
        this.name = name;
        this.desc = desc;
    }

    ChannelEnum() {
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }
}
