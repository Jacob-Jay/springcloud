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