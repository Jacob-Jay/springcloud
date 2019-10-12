# Eureka

## 概览

### 服务注册

​	服务发送rest请求向服务注册中心注册，携带自身的ip、port等，维护在一个双层map中

#### 服务端

最下面instanceRegisrty是spring的主要负责事件的发布服务注册相关工作有netflix的eureka组件负责

![1570591647512](instanceRegistry.png)

- 接收注册请求

  com.netflix.eureka.resources.ApplicationResource#addInstance方法（使用的jersey实现rest）

- 检查续约时间设定

  org.springframework.cloud.netflix.eureka.server.InstanceRegistry#resolveInstanceLeaseDuration

```java
//如果服务提供者没有设置或者设置时间无效就使用默认值，反之使用设置的值
private int resolveInstanceLeaseDuration(final InstanceInfo info) {
		int leaseDuration = Lease.DEFAULT_DURATION_IN_SECS;
		if (info.getLeaseInfo() != null && info.getLeaseInfo().getDurationInSecs() > 0) {
			leaseDuration = info.getLeaseInfo().getDurationInSecs();
		}
		return leaseDuration;
	}
//服务提供者设定的参数值
org.springframework.cloud.netflix.eureka.EurekaInstanceConfigBean#leaseExpirationDurationInSeconds
```

- 发布服务注册事件

  org.springframework.cloud.netflix.eureka.server.InstanceRegistry#handleRegistration

-  注册到服务中心

  com.netflix.eureka.registry.AbstractInstanceRegistry#register

  ```java
  //注册信息维护在一个双层map中，外层key是appName，内层key是instenceId
  private final ConcurrentHashMap<String, Map<String, Lease<InstanceInfo>>> registry
              = new ConcurrentHashMap<String, Map<String, Lease<InstanceInfo>>>();
  
  //InstanceInfo  每个服务的属性实例
  //Lease  服务注册状态的实例，
  
  
  //注册时检测是否已存在当前appName的instanceId的应用，如果存在且还没有过期就使用原先存在的InstanceInfo，只需要更新一些属性，否则使用注册请求的InstanceInfo
  ```

  

- 将信心同步到兄弟节点

  com.netflix.eureka.registry.PeerAwareInstanceRegistryImpl#replicateToPeers

  ```java
   
              //如果是其他节点同步过来或者服务为空就不同步		
              if (peerEurekaNodes == Collections.EMPTY_LIST || isReplication) {
                  return;
              }
  
              for (final PeerEurekaNode node : peerEurekaNodes.getPeerEurekaNodes()) {
                  //自身忽略
                  if (peerEurekaNodes.isThisMyUrl(node.getServiceUrl())) {
                      continue;
                  }
                  replicateInstanceActionsToPeers(action, appName, id, info, newStatus, node);
              }
  ```

  

#### 客户端

cloudEurekaClient是spring实现的用于自动配置类是启动eureka，上面的都是netflix原生实现

![1570718941923](clientEureka.png)



- 初始化服务端

  ```java
  org.springframework.cloud.netflix.eureka.EurekaClientAutoConfiguration.EurekaClientConfiguration#eurekaClient  自动配置类进行初始化，构造父类时进行一些列操作
  
  参数设置、各种监视器初始化
  
   if (!config.shouldRegisterWithEureka() && !config.shouldFetchRegistry())
       检测当不需要注册 也不需要收索服务时直接放回
  ```

  ```java
  创建调度器以及心跳检测执行器和服务收索执行器
  ```

  ```java
  创建httpcilent
  ```

  ```java
  区域信息设置
  ```

  ```java
   if (clientConfig.shouldFetchRegistry() && !fetchRegistry(false)) {
              fetchRegistryFromBackup(); 
       }
  从注册中心获取服务，如果全部失败就使用默认的地址获取
  ```

  ```java
  if (this.preRegistrationHandler != null) {
              this.preRegistrationHandler.beforeRegistration();
     }
  注册前置处理
  
  ```

  ```java
  开始服务列表更新定时任务
   if (clientConfig.shouldFetchRegistry()) {
              
              int registryFetchIntervalSeconds = clientConfig.getRegistryFetchIntervalSeconds();   //执行间隔
              int expBackOffBound = clientConfig.getCacheRefreshExecutorExponentialBackOffBound();
              scheduler.schedule(
                      new TimedSupervisorTask(
                              "cacheRefresh",
                              scheduler,
                              cacheRefreshExecutor,
                              registryFetchIntervalSeconds,
                              TimeUnit.SECONDS,
                              expBackOffBound,
                              new CacheRefreshThread()
                      ),
                      registryFetchIntervalSeconds, TimeUnit.SECONDS);
          }
  ```

  ```java
  开启续约定时任务
   scheduler.schedule(
                      new TimedSupervisorTask(
                              "heartbeat",
                              scheduler,
                              heartbeatExecutor,
                              renewalIntervalInSecs,
                              TimeUnit.SECONDS,
                              expBackOffBound,
                              new HeartbeatThread()
                      ),
                      renewalIntervalInSecs, TimeUnit.SECONDS);
  ```

  ```java
  ​```注册
  instanceInfoReplicator = new InstanceInfoReplicator(
                      this,
                      instanceInfo,
                      clientConfig.getInstanceInfoReplicationIntervalSeconds(),
                      2); // burstSize
  
  //状态监听器
              statusChangeListener = new ApplicationInfoManager.StatusChangeListener() {
                  @Override
                  public String getId() {
                      return "statusChangeListener";
                  }
  
                  @Override
                  public void notify(StatusChangeEvent statusChangeEvent) {
                      if (InstanceStatus.DOWN == statusChangeEvent.getStatus() ||
                              InstanceStatus.DOWN == statusChangeEvent.getPreviousStatus()) {
                          // log at warn level if DOWN was involved
                          logger.warn("Saw local status change event {}", statusChangeEvent);
                      } else {
                          logger.info("Saw local status change event {}", statusChangeEvent);
                      }
                      instanceInfoReplicator.onDemandUpdate();
                  }
              };
  
              if (clientConfig.shouldOnDemandUpdateStatusChange()) {
                  applicationInfoManager.registerStatusChangeListener(statusChangeListener);
              }
  
              instanceInfoReplicator.start(clientConfig.getInitialInstanceInfoReplicationIntervalSeconds());  //开始注册任务
  
  
  
  public void run() {
          try {
              discoveryClient.refreshInstanceInfo();
  
              Long dirtyTimestamp = instanceInfo.isDirtyWithTime();
              if (dirtyTimestamp != null) {
                  discoveryClient.register();  //注册
                  instanceInfo.unsetIsDirty(dirtyTimestamp);
              }
          } catch (Throwable t) {
              logger.warn("There was a problem with the instance info replicator", t);
          } finally {
              Future next = scheduler.schedule(this, replicationIntervalSeconds, TimeUnit.SECONDS);
              scheduledPeriodicRef.set(next);
          }
      }
  
  //发送注册请求
  boolean register() throws Throwable {
          logger.info(PREFIX + "{}: registering service...", appPathIdentifier);
          EurekaHttpResponse<Void> httpResponse;
          try {
              httpResponse = eurekaTransport.registrationClient.register(instanceInfo);
          } catch (Exception e) {
              logger.warn(PREFIX + "{} - registration failed {}", appPathIdentifier, e.getMessage(), e);
              throw e;
          }
          if (logger.isInfoEnabled()) {
              logger.info(PREFIX + "{} - registration status: {}", appPathIdentifier, httpResponse.getStatusCode());
          }
          return httpResponse.getStatusCode() == Status.NO_CONTENT.getStatusCode();
      }
  ```

  

  

com.netflix.discovery.DiscoveryClient#initScheduledTasks



- 发送请求执行注册

com.netflix.discovery.DiscoveryClient#register

### 服务续约

服务提供者每隔一定时间（30s）向注册中心发送请求进行续约，防止被剔除



#### 客户端

```java
 private class HeartbeatThread implements Runnable {

        public void run() {
            if (renew()) {
                lastSuccessfulHeartbeatTimestamp = System.currentTimeMillis();
            }
        }
    }


com.netflix.discovery.DiscoveryClient#renew

boolean renew() {
        EurekaHttpResponse<InstanceInfo> httpResponse;
        try {
            httpResponse = eurekaTransport.registrationClient.sendHeartBeat(instanceInfo.getAppName(), instanceInfo.getId(), instanceInfo, null);//发送续约请求
            logger.debug(PREFIX + "{} - Heartbeat status: {}", appPathIdentifier, httpResponse.getStatusCode());
            if (httpResponse.getStatusCode() == Status.NOT_FOUND.getStatusCode()) {
                REREGISTER_COUNTER.increment();
                logger.info(PREFIX + "{} - Re-registering apps/{}", appPathIdentifier, instanceInfo.getAppName());
                long timestamp = instanceInfo.setIsDirtyWithTime();
                boolean success = register();
                if (success) {
                    instanceInfo.unsetIsDirty(timestamp);
                }
                return success;
            }
            return httpResponse.getStatusCode() == Status.OK.getStatusCode();
        } catch (Throwable e) {
            logger.error(PREFIX + "{} - was unable to send heartbeat!", appPathIdentifier, e);
            return false;
        }
    }
```



#### 服务端



com.netflix.eureka.resources.InstanceResource#renewLease

```java
@PUT
    public Response renewLease(
            @HeaderParam(PeerEurekaNode.HEADER_REPLICATION) String isReplication,
            @QueryParam("overriddenstatus") String overriddenStatus,
            @QueryParam("status") String status,
            @QueryParam("lastDirtyTimestamp") String lastDirtyTimestamp) {
        boolean isFromReplicaNode = "true".equals(isReplication);
        boolean isSuccess = registry.renew(app.getName(), id, isFromReplicaNode);

        // Not found in the registry, immediately ask for a register
        if (!isSuccess) {
            logger.warn("Not Found (Renew): {} - {}", app.getName(), id);
            return Response.status(Status.NOT_FOUND).build();
        }
        // Check if we need to sync based on dirty time stamp, the client
        // instance might have changed some value
        Response response;
        if (lastDirtyTimestamp != null && serverConfig.shouldSyncWhenTimestampDiffers()) {
            response = this.validateDirtyTimestamp(Long.valueOf(lastDirtyTimestamp), isFromReplicaNode);
            // Store the overridden status since the validation found out the node that replicates wins
            if (response.getStatus() == Response.Status.NOT_FOUND.getStatusCode()
                    && (overriddenStatus != null)
                    && !(InstanceStatus.UNKNOWN.name().equals(overriddenStatus))
                    && isFromReplicaNode) {
                registry.storeOverriddenStatusIfRequired(app.getAppName(), id, InstanceStatus.valueOf(overriddenStatus));
            }
        } else {
            response = Response.ok().build();
        }
        logger.debug("Found (Renew): {} - {}; reply status={}", app.getName(), id, response.getStatus());
        return response;
    }
```



### 服务同步

​	多个服务注册中心之间进行服务注册信息的同步，维护服务的一致性

#### 发送方

#### 接收方

### 获取服务

​	服务注册时获取服务注册中心维护的只读服务列表默认服务和服务注册中心的列表都是30秒跟新

#### 服务端

#### 客户端

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

封装了服务的各种信息

![1570585817893](instenceinfo.png)

### EurekaBootStrap