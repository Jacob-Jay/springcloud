# Eureka

## 概览

### 服务注册

​	服务发送rest请求向服务注册中心注册，携带自身的ip、port等，维护在一个双层map中

### 服务续约

服务提供者每隔一定时间（30s）向注册中心发送请求进行续约，防止被剔除

### 服务同步

​	多个服务注册中心之间进行服务注册信息的同步，维护服务的一致性

### 获取服务

​	服务注册时获取服务注册中心维护的只读服务列表默认服务和服务注册中心的列表都是30秒跟新

### 服务调用

​	服务获取到清单后根据请求以及服务的region和zone的关系请求服务，一个region包含多个zone，优先调用同一个zone的服务提供者

### 服务下线

​	当服务提供者需要重启或者下线时需要发送rest请求给注册中心，注册中心收到请求后会将该服务标记为down状态，并发布事件通知各个服务提供者

### 服务剔除

​	由于网络原因，服务提供者没有发起下线服务，注册中心启动时会创建定时任务，每隔一定时间（60s）就将超时没续约的服务进行剔除（默认90s）

### 自我保护

超过一定规模（85%）的服务与注册中心失去联系就会触发自我保护，避免因网络原因触发的服务剔除

## 源码

### 客户端配置

```java
线程数：心跳  服务获取
时间： 连接 读取超时时间
数量：实例个数单个注册中心 全部注册中心
是否压缩
是否注册、优先使用同一zone的服务


运行时修改是否生效？？？？？？？怎么实现的
```

### 服务端

```java
InstanceRegistry类维护服务注册表
org.springframework.cloud.netflix.eureka.server.InstanceRegistry#renew（）续约
org.springframework.cloud.netflix.eureka.server.InstanceRegistry#cancel（）下线
org.springframework.cloud.netflix.eureka.server.InstanceRegistry#register(com.netflix.appinfo.InstanceInfo, int, boolean) 注册
```







### lookupService

### eurekaClient

### discoverClient

### application

### applications

### instenceInfo

### EurekaBootStrap