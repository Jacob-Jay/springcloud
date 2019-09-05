# eureak（服务）

1. 服务注册与发现

# ribbon（消费者）

1. 使用restemplate实现负载均衡

# fegin（消费者）

1. 使用接口模拟httpClient实现负载均衡

# hytrix（熔断）

1. ribbon：添加注解设定错误回掉方法
2. fegin：自身就具有hytrix，但是需要配置文件打开，并且将接口实现类注册到容器中

# zull（路由）

1. 指定特定的api将请求转发到对应的服务，还可以在不同时机进行过滤