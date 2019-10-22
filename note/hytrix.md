 

![1568702087882](hytrix.png)

```java
HystrixCommand
返回单个结果时创建的命令
R  value = execute();//同步执行返回结果
Future<R> result = queue();//异步执行返回future，从中获取结果
```

```java
HystrixObservableCommand
返回多个结果时创建的命令
observable hot = observable();
observable cold = toObservable();
```

