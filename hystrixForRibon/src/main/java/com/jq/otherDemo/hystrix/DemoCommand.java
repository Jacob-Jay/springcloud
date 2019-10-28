package com.jq.otherDemo.hystrix;

import com.netflix.hystrix.*;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * @author Jiangqing
 * @version 1.0
 * @since 2019-10-24 17:21
 */
public class DemoCommand extends HystrixCommand<String> {
    protected DemoCommand(Setter setter) {
        super(setter);
    }

    @Override
    protected String run() throws Exception {
        TimeUnit.MILLISECONDS.sleep(5000);
        return "ssss";
    }

    @Override
    protected String getFallback() {
        return "null";
    }

    public static void main(String[] args) {

syn();
asyn();

    }

    //构建命令是设置相关的属性
    static Setter setter = Setter.withGroupKey(new HystrixCommandGroupKey() {
        @Override
        public String name() {
            return "jq";
        }
    }).andCommandPropertiesDefaults( HystrixCommandProperties.Setter().withExecutionTimeoutInMilliseconds(6000))
            .andCommandKey(HystrixCommandKey.Factory.asKey("jq"))
            .andThreadPoolKey(HystrixThreadPoolKey.Factory.asKey("jq"))
            .andThreadPoolPropertiesDefaults(HystrixThreadPoolProperties.Setter().withCoreSize(5));

    /**
     * 同步执行命令
     */
    public static void syn() {


        DemoCommand command   = new DemoCommand(setter);
        System.out.println("before  execute==================");
        String execute = command.execute();
        System.out.println(execute);
        System.out.println("finish  execute==================");
    }

    /**
     * 异步执行
     */
    public static void asyn() {

        DemoCommand command2  = new DemoCommand(setter);
        System.out.println("before  queue==================");
        Future<String> queue = command2.queue();

        System.out.println("finish  queue==================");
        try {
            System.out.println(queue.get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
}
