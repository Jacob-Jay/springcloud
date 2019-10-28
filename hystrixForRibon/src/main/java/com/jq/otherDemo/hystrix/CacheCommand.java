package com.jq.otherDemo.hystrix;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixRequestCache;
import com.netflix.hystrix.strategy.concurrency.HystrixConcurrencyStrategyDefault;
import com.netflix.hystrix.strategy.concurrency.HystrixRequestContext;

/**
 * @author Jiangqing
 * @version 1.0
 * @since 2019-10-25 10:00
 */
public class CacheCommand extends HystrixCommand<CacheCommand.User> {
    private String key;
    private static final HystrixCommandKey COMMANDKEY = HystrixCommandKey.Factory
            .asKey("CachedCommand_cmd");

    public CacheCommand(String key) {
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("CachedCommand"))
                .andCommandKey(COMMANDKEY));
        this.key = key;
    }

    /**
     * 决定是否使用缓存
     * @return
     */
    @Override
    protected boolean isRequestCachingEnabled() {
        return false;
    }

    //重写此方法，根据返回的值判断是否是已有缓存
    @Override
    protected String getCacheKey() {
        return this.key;
    }

    //清除缓存
    public static void flushCache(String key) {
        HystrixRequestCache.getInstance(COMMANDKEY,
                HystrixConcurrencyStrategyDefault.getInstance()).clear(key);
    }

    @Override
    protected User run() throws Exception {
        return new User();
    }

    public static void main(String[] args) {
        HystrixRequestContext context = HystrixRequestContext.initializeContext();
        CacheCommand cacheCommand1 = new CacheCommand("cachekey");
        CacheCommand cacheCommand2 = new CacheCommand("cachekey");
        User execute = cacheCommand1.execute();
//        flushCache("cachekey");
        User execute1 = cacheCommand2.execute();
        System.out.println(execute==execute1);
        System.out.println(cacheCommand2.isResponseFromCache());
        context.shutdown();
    }

    class  User{}
}
