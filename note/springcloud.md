# eureak（服务）

1. 服务注册与发现

   1. 注册中心

      1. 服务剔除：服务非正常下线时将超过配置时间未续约的服务剔除
      2. 以一个双层map维护![img](/serviceMap.png)

   2. 服务

      1. 启动时会向注册中心发送请求注册自身（eureka.client.registerWithEureka=true确定是否注册）
      2. eureka.client.fetchRegistry=true确定是否从服务中心拉取服务列表
      3. 服务续约（间隔、失效两个参数）
      4. 获取服务（30秒更新默认是指客户端30秒去获取一次还是服务中心30秒更新一次？？？）
      5. 服务下线（发送请求，告知注册中心，注册中心会广播下线事件）
      6. ![1568280214457](/clientCOnfig)

   3.  实现逻辑

      1.  com.netflix.discovery.DiscoveryClient#initScheduledTasks，在构造方法中使用调度线程完成注册，续约以及获取服务的
      2. 服务续约com.netflix.discovery.DiscoveryClient#renew
      3. 注册中心接收注册请求com.netflix.eureka.resources.ApplicationResource#addInstance

      ​	

2. 自我保护

   1. 在15分钟内（可配置？？）心跳率低于85%的服务

3. region和zone

   

4. 配置

   1. 服务名称
   2. ip

# ribbon（消费者）

1. 使用restemplate实现负载均衡
2. 轮询算法（以及自定义）

# fegin（消费者）

1. 使用接口模拟httpClient实现负载均衡

# hytrix（熔断）

1. 服务熔断
   1. ribbon：添加注解设定错误回掉方法
   2. fegin：自身就具有hytrix，但是需要配置文件打开，并且将接口实现类注册到容器中
   
2. 服务降级

3. ```java
   1	//包装请求可能有风险的服务的请求，使用了命令模式
   HystrixComand   //单个结果   
       	//hot    ===》observer  无论是否有订阅者都会发布事件，订阅者接收部分事件
       	//cold ====》toObservable   有订阅者才会发布订阅者接收所有事件
   HystrixObservableCommand //多个结果
   
   2	执行处理
       
   3   结果是否被缓存（如果缓存功能开启且命中直接返回否则继续）
   4	断路器是否打开，如果打开直接至8
   5	检查资源是否用完（线程池，请求队列、信号量）用完至8，每个服务的资源是分割的不会互相影响
       
   6	执行请求，异常超时至8，否则返回结果至7
   7	计算短路器的健康值
   8	服务降级处理，尽量保证最后获取返回值的是本地的，否则就需要根据执行方法做出不同响应
   ```

4. ```java
   HystrixCircuitBreaker //断路器
       allowRequest//是否允许请求
       isOpen//断路器是否打开，根据标志或者度量指标qps以及错误百分比，打开时是记录时间com.netflix.hystrix.HystrixCircuitBreaker.HystrixCircuitBreakerImpl#isOpen
      
       				
       markSuccess //  闭合断路器，当请求成功时将打开的断路器关闭
       内部类
       Factory  //缓存了命令与断路器的关系
       NoOpCircuitBreaker //空实现
       HystrixCircuitBreakerImpl//基本实现（通过isopen和allowSingleTest实现断路器状态自动化）
       	properties //命令的属性
       	HystrixCommandMetrics  //命令的度量指标
       	circuitOpen  //断路器打开标志
       	circuitOpenedOrLastTestedTime  //断路器上一次打开或测试的时间戳
   ```

5. ```java
   使用线程池分离降低了服务之间的影响，会在一定程度上增加响应延迟，如果对延迟要求比较高的服务可以使用信号量代替线程池
   ```

6. ```java
   //异步请求
   ```

7. ```java
   //服务降级即使用一个本地方法返回值，指挥忽略HystrixBadRequestException异常可以通过ignoreExceptions忽略异常不触发降级处理
   	@HystrixCommand(fallbackMethod = "default"，ignoreExceptions = RuntimeException.class)
       public String helloError(String name,Throwable throwable) {
           return 。。。 //网络请求时仍需要注解标识
       }
   
   //当方法是本地处理时可以不进行降级处理
   

   @HystrixCommand
   	fallbackMethod  //降级处理方法
       ignoreExceptions  //不做处理的异常
       commandKey   //命令的key
       groupKey   //分组key   通常将组作为一个单位共享线程池
        threadPoolKey  //在group的基础之上在进行细分   
           
   ```
   
8. ```java
   	请求缓存
   	@CacheResult//开启缓存 默认key为参数组装
	@CacheRemove(commandKey = "helloAsync")  //清除缓存key为放入缓存的命令的key
   	可以通过以上两个注解的cacheKeyMethod值设置缓存key（优先级高）
   	也可以通过@CacheKey("name")制定缓存key
   ```
   
9. ```java
   //请求合并，将一定时间内的同一个服务的请求合并起来一次性请求
   com.netflix.hystrix.HystrixCollapser
   
    @HystrixCollapser(batchMethod = "getAll",collapserProperties = {
               @HystrixProperty(name = "timerDelayInMilliseconds",value = "100")
       }) //将100ms内单个请求合并到一起进行请求，只有在并发量高时才会提高系统性能，否则会降低性能
   ```

10. ```java
    //配置文件  从后往前依次覆盖
    1. 全局默认值
    2. 全局配置
    3. 实例默认值
    4. 实例配置值
    
    ```

11. command 控制HystrixCommand.run()的执行

    ```java
    execution.isolation.strategy  //控制隔离级别
        //thread  独立线程运行，受线程池中的线程数量约束
        //semaphore  信号量，调用线程上运行，受信号量计数约束
        
    execution.isolation. thread.timeoutinMilliseconds  //命令执行超时时间
    
    execution.timeout.enabled   //命令执行是否进行超时后进行服务降级
    
    execution.isolation.thread.interruptOnTimeout //超时时是否中断
    
    execution.isolation.thread.interruptOnCancel //取消时是否中断
    
    execution.isolation.semaphore.maxConcurrentRequests  //信号量控制时最大值
    
    ```

    

12. fallBack控制HystrixComrnand.getFallback ()的执行

    ```java
    fallback.isolation.semaphore. maxConcurrentRequests  //该方法执行的最大数量，超过抛异常
        
     fallback.enabled //是否开启服务降级   
    ```

13. circuitBreaker控制断路器的表现

    ```java
    circuitBreaker.enabled  //命令执行失败时是否跟踪健康状况和熔断请求
        
    circuitBreaker.requestVolumeThreshold  //滚动窗口失败触发熔断的个数
    
    circuitBreaker.sleepWindowinMilliseconds  //短路器休眠的时间，即打开断路器到尝试关闭的时间
    
    circuitBreaker.errorThresholdPercentage  //打开断路器的错误百分比阈值
    
    circuitBreaker.forceOpen   //是否强制打开断路器拒绝所有请求
    
    circuitBreaker.forceClosed   //是否强制关闭断路器接受所有请求
    ```

14. metrics  性能指标相关的参数

    ```java
    metrics.rollingStats.timeinMillionseconds  // 断路器收集信息的时间窗口
        
    metrics.rollingStats.numBuckets     //桶数量 和时间窗口结合给定统计的信息的单位时间必须整除，且不能动态修改
    
    me丘ics.rollingPercentile.bucketSize // 计算执行次数的值，比如100，执行了150次只统计最后50次的
    
    metrics.rollingPercentile.enabled  //
    
    metrics.rollingPercentile.timeinMilliseconds //
    
    metrics.rollingPercentile.numBuckets  //
    
    metrics.healthSnapshot.intervalinMilliseconds//
    ```

15. 合并请求参数

    ```java
    maxRequestsinBatch  //一次合并的最大请求数
        
    timerDelayinMillionseconds     //合并请求的单位时间
    
    requestCache.enabled  //是否开启缓存
    ```

16. 线程池属性

    ```java
    coreSize  //线程池的核心数，即最大并发数量
        
    maxQueueSize//最大队列容量
    
    queueSizeRejectionThreshold  //拒绝的量，即使没达到最大队列量
    ```

    

17. sd 

18. sd

19. 

20. ![1568702087882](hytrix.png)





# zull（路由）

1. 指定特定的api将请求转发到对应的服务，还可以在不同时机进行过滤

# config

## server

1. 使用application-xxx方式命名

   /{application}/{profile}[/{label}]		
   /{application}-{profile}.yml
   /{label}/{application}-{profile}.yml
   /{application}-{profile}.properties
   /{label}/{application}-{profile}.properties

2. 