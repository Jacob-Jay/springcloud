package com.jq.commod;

/**
 * @author Jiangqing
 * @version 1.0
 * @since 2019-09-17 11:30
 *
 * 调用者，只有一个命令
 */
public class Invoker {
    private Commond commond;

    public Invoker(Commond commond) {
        this.commond = commond;
    }

    public void action() {
        this.commond.excute();
    }
}
