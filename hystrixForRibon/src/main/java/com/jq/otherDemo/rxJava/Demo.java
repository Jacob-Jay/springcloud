package com.jq.otherDemo.rxJava;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Jiangqing
 * @version 1.0
 * @since 2019-10-22 18:44
 */
public class Demo {
    public static void main(String[] args) {
        Map<String,Object> m = new HashMap<>();
        m.put("aa", "sd");
        List<String> stringList = new ArrayList<>();
        stringList.add("sds");
        m.put("sss", stringList);
        System.out.println(m.toString());
    }

    public static void dodo() {
//        Observable.create()
    }
}
