package com.jq.commod;

/**
 * @author Jiangqing
 * @version 1.0
 * @since 2019-09-17 11:30
 */
public class TestCommond {
    public static void main(String[] args) {
        Receiver receiver = new Receiver();
        Commond commond = new JumpCommond(receiver);
        Invoker invoker = new Invoker(commond);
        invoker.action();
    }
}
