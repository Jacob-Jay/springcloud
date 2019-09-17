根据服务id查找服务执行请求





```java
LoadBalancerInterceptor   //拦截器实现rest的负载均衡

    
    LoadBalancerClient  //根据服务id获取到服务ILoadBalancer

ServiceInstance  //服务实例




```

ILoadBalancer  

1. ```java
   IRule //定义了负载均衡的算法
   ```

2. ```java
   @Monitor(name = PREFIX + "AllServerList", type = DataSourceType.INFORMATIONAL)protected volatile List<Server> allServerList = Collections        .synchronizedList(new ArrayList<Server>());
   @Monitor(name = PREFIX + "UpServerList", type = DataSourceType.INFORMATIONAL)protected volatile List<Server> upServerList = Collections        .synchronizedList(new ArrayList<Server>());  //保存所有服务，可用服务
   ```

3. ```java
   IPing//通过ping操作检测服务状态
   IPingStrategy   //ping检测的策略
   ```

4. ```java
   构造器开启了一个定时任务检测服务健康状态
   ```

5. ```
   LoadBalancerStats记录了服务的状态
   ```