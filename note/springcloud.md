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

2. ```java
   //使用接口访问服务，参数与调用服务一致，实现类作为降级处理
   @FeignClient(value = "CLIENT-HI",fallback = HytrixError.class)
   @Service
   public interface HelloService {
   
       @RequestMapping("/hello")
       //必须加value否则报错
       String hello(@RequestParam(value = "name") String name);
       @RequestMapping("feign1")
       public String feign1(@RequestParam("name") String name) ;
       @RequestMapping("feign2")
       public User feign2(@RequestHeader("name") String name, @RequestHeader("age") Integer age);
       @RequestMapping("feign3")
       public String feign3(@RequestBody User user);
   
   }
   ```

3. ```java
   由于服务提供段和服务消费端接口一致可以单独提取一个项目两边引用继承
   ```

4. ```java
   serviceName.ribbon.key = value  指定特定服务的配置 
   ```

   ```java
   ad 
   ```

   ```java
   ad 
   ```

   ```java
   ad 
   ```

   ```java
   ad 
   ```

   ```java
   ad 
   ```

   ```java
   ad 
   ```

   ```java
   ad 
   ```

   

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

```java
概述：
	对服务访问路径通过eureka注册中心进行自动化，降低维护人员的工作
	同一控制整个系统的权限限制
	具有负载均衡功能？？？？
	熔断功能？？？？
```



```java
入门demo：
	引入依赖
	使用@enableZuulProxy开启路由功能
	
	配置路由关系：路由context转发到url或者服务（二选一）
		zuul.routes.api-a.path=/api-a/**             
		zuul.routes.api-a.url=http://localhost:8080/      无需注册到服务中心
		zuul.routes.api-a.serviceId=hello-service		 需要注册到服务中心	推荐使用
	将localhost：zullport/api-a/xxx重新路由到url为http://localhost:8080/xxx
	或者服务名为hello—service/xxx
```



```java
//创建过滤器对请求进行过滤
@Component
public class RouteFilter extends ZuulFilter {

    /**
     * 定义过滤器的执行时机
     * 1、pre    路由前执行
     * 2、route  路由时
     * 3、post    路由之后  错误之前
     * 4、error  发生错误
     * @return
     */
    @Override
    public String filterType() {
        return "pre";
    }

    /**
     * 存在多个过滤器时，定义当前路由器的执行顺序
     * @return
     */
    @Override
    public int filterOrder() {
        return 0;
    }

    /**
     * 定义是否需要进行过滤，可以根据请求进行判断
     * @return
     */
    @Override
    public boolean shouldFilter() {
        //获取当前请求
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        return true;
    }

    @Override
    public Object run() throws ZuulException {

        //获取当前请求
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();

        Object name = request.getParameter("name");
            if(name == null) {
                //是否返回路由的响应
                ctx.setSendZuulResponse(false);
                //设置响应状态码
                ctx.setResponseStatusCode(401);
                ctx.setResponseBody("name is empty");

                return null;
            }
            if (!"jq".equals(name)) {
                ctx.setSendZuulResponse(false);
                ctx.setResponseStatusCode(402);
                ctx.setResponseBody("name isn't  jq");
                return null;
            }
            System.out.println("ok");

        return null;

    }
}
```



```java
路由详解
	传统路由：不用进行服务注册，直接将请求转发到给定的url
		单实列：转发到唯一的url
			zuul. routes.user - service.path=/user - service/xx
			zuul.routes. user - service.url=http:/ /localhost:8080/
        多实例：动态负载均衡的转发到配置的各个url
        	zuul.routes.user - service.path=/user - service/xx
			zuul. routes.user - service.serviceid=<user - servicename>   //服务名称
			ribbon.eureka.enabled=false//由于需要负载均衡，但是有没有注册到服务中心所以禁用手动配置
			<user - servicename>.ribbon.listOfServers=
					http://localhost:8080/,http://localhost:8081/     //配置服务对应的地址
	
	服务路由：需要注册到服务中心，根据服务名称进行转发
		zuul.routes.user-service.path=/user-service/xx
		zuul.routes.user-service.serviceid=user-serviceId
		可以简化为
		zuul.routes.user-serviceId=/user-service/xx
		
	默认路由：默认会为注册中心的服务创建路由信息，context为服务名称
			可以通过配置过滤某些不想开放的服务不自动生成路由
			zuul:
 			 ignored-services:
    			- client-hi
    			- service-ribbon-hystrix	
```



```java
自定义路由映射规则

```



```java
路径匹配规则
    /?  匹配一个字符
    /*  匹配一层，可以多个字符
    /** 匹配多层任意字符
    
    且匹配是按照yaml申明顺序来匹配并不是详细匹配
            zuul:
              routes:
                service-feign: /api-a/xx/**   调换顺序就会报错
                api-a:
                  path: /api-a/**
                  serviceId: service-ribbon-hystrix
    
    zuul.ignored-patterns=/**/hello/**  忽略满足改匹配的请求不进行路由
```



```java
路由前缀
		zuul.prefix=/api  //在原有路由之前具有改规则才会匹配，并且路由时会默认去除
```



```java
cookie与头信息
	由于默认会过滤cookie，下游应用会无法接收使用
	
	全部放行（不推荐）
		zuul.sensitiveHeaders=
	
	自定义路由放行
        ＃方法一：对指定路由开启自定义敏感头
        zuul.routes.<router>.customSensitiveHeaders=true
        ＃方法二：将指定路由的敏感头设置为空
        zuul.routes.<router>.sensitiveHeaders=
```



```java
请求从定向
	比如登陆跳转怎么解决
```



```java

```



# config

## server

1. 使用application-xxx方式命名

   /{application}/{profile}[/{label}]		
   /{application}-{profile}.yml
   /{label}/{application}-{profile}.yml
   /{application}-{profile}.properties
   /{label}/{application}-{profile}.properties

2. 