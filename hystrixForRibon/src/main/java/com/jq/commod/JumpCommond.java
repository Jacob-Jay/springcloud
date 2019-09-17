package com.jq.commod;

/**
 * @author Jiangqing
 * @version 1.0
 * @since 2019-09-17 11:29
 */
public class JumpCommond implements Commond {
    private Receiver receiver;

    public JumpCommond(Receiver receiver) {
        this.receiver = receiver;
    }

    @Override
    public void excute() {
        this.receiver.action();
    }
}
