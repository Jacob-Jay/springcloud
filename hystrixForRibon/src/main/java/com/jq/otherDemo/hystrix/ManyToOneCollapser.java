package com.jq.otherDemo.hystrix;

import com.netflix.hystrix.*;
import com.netflix.hystrix.strategy.concurrency.HystrixRequestContext;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Future;

import static org.junit.Assert.assertTrue;

/**
 * @author Jiangqing
 * @version 1.0
 * @since 2019-10-25 10:00
 */
public class ManyToOneCollapser extends HystrixCollapser<List<User>,User,String> {


    public static void main(String[] args) throws Exception{
        HystrixRequestContext context = HystrixRequestContext.initializeContext();
        Future<User> f1 = new ManyToOneCollapser("1").queue();
        Future<User> f2 = new ManyToOneCollapser("2").queue();
        Future<User> f3 = new ManyToOneCollapser("3").queue();
        Future<User> f4 = new ManyToOneCollapser("4").queue();
        User user = f1.get();
        System.out.println(user);
        User user1 = f2.get();
        System.out.println(user1);
        User user2 = f3.get();
        System.out.println(user2);
        User user3 = f4.get();
        System.out.println(user3);
        System.out.println(HystrixRequestLog.getCurrentRequest().getAllExecutedCommands().size());
        HystrixCommand<?> command = HystrixRequestLog.getCurrentRequest().getAllExecutedCommands()
                .toArray(new HystrixCommand<?>[1])[0];
//        assertEquals("sdsd", command.getCommandKey().name());
        assertTrue(command.getExecutionEvents().contains(HystrixEventType.COLLAPSED));
        assertTrue(command.getExecutionEvents().contains(HystrixEventType.SUCCESS));
        context.shutdown();

    }

    private final  String key;

    public ManyToOneCollapser(String key) {
        this.key = key;
    }


    @Override
    public String getRequestArgument() {
        return key;
    }

    /**
     * 将时间窗口内的HystrixCollapser 封装到新的command中
     * @param collapsedRequests  时间窗口内的所有HystrixCollapser
     * @return  新的command
     */
    @Override
    protected HystrixCommand<List<User>> createCommand(Collection<CollapsedRequest<User, String>> collapsedRequests) {
        List<String>params = new ArrayList<>();
        for (CollapsedRequest<User, String> collapsedRequest : collapsedRequests) {
            String argument = collapsedRequest.getArgument();
            params.add(argument);
        }
        return new BatchCommand(params);
    }

    /**
     * createCommand 生成的command执行完毕后将结果封装到对应的HystrixCollapser 中
     * @param batchResponse  createCommand 生成命令的返回值
     * @param collapsedRequests 时间窗口内的HystrixCollapser
     */
    @Override
    protected void mapResponseToRequests(List<User> batchResponse, Collection<CollapsedRequest<User, String>> collapsedRequests) {
        int count = 0;
        for (CollapsedRequest<User, String> collapsedRequest : collapsedRequests) {
            User user = batchResponse.get(count++);
            collapsedRequest.setResponse(user);
        }
    }





    private static final class BatchCommand extends HystrixCommand<List<User>> {

        List<String>ids;

        public BatchCommand(List<String> ids) {
            super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("sdsd")));
            this.ids = ids;
        }

        @Override
        protected List<User> run() throws Exception {
            List<User>results = new ArrayList<>();
            for (String id : ids) {
                results.add(new User(id, "name" + id));
            }
            return results;
        }
    }



}

class User{
 String id;
 String name;



public User(String id, String name) {
    this.id = id;
    this.name = name;
}

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}