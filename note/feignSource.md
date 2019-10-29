# 主要类

# @EnableFeignClients

主要是开启feignclient扫描，加入spring容器中，并引入了FeignClientsRegistrar



## FeignClientsRegistrar

```java
registerDefaultConfiguration

如果@enableFeignClinets得defaultConfiguration有值就将其组装为一个FeignClientSpecification（包含名称和class【】）注入到spring容器中

		
```

 

```java
registerFeignClients
1、获取FeignClient扫描范围
	如果@enableFeignClinets#clients有值就使用给定类所在得包扫描不然就启动类包扫描
2、如果@FeignClinet得defaultConfigurconfiguration有值就将其组装为一个FeignClientSpecification（包含名称和class【】）注入到spring容器中
3、将每个feignClient包装为FeignClientFactoryBean注入到spring容器中
```



所以@EnableFeignClients得主要作用就是将feign配置类和feignClinet（FeignClientFactoryBean工厂bean）注入到spring容器中

## FeignClientFactoryBean