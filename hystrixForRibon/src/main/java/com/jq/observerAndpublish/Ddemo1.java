package com.jq.observerAndpublish;


import rx.Observable;
import rx.Subscriber;

/**
 * @author Jiangqing
 * @version 1.0
 * @since 2019-09-17 14:41
 */
public class Ddemo1 {
    public static void main(String[] args) {
di();
    }

    public static  void di() {
       //创建事件源observable
        Observable<String> observable =
            Observable.create(new Observable.OnSubscribe<String>() {//在被订阅时触发
              @Override
              public void call(Subscriber<? super String> subscriber) {
                  subscriber.onNext("Hello RxJava");
                  subscriber.onNext("I am程序猿DD");
                  subscriber.onCompleted();
              }
          });


        //创建订阅者subscriber
            Subscriber<String> subscriber = new Subscriber<String>() {
                @Override
                public void onCompleted() {

                    //结束事件的处理
                    System.out.println("wanc");
                }

                @Override
                public void onError(Throwable e) {
                    //处理遇见异常时的处理
                    System.out.println("error");
                }


                @Override
                public void onNext(String s) {
                    //接收到事件进行处理
                    System.out.println("Subscriber : " + s);
                    //throw new RuntimeException("ss");
                }
            };
        //订阅
        observable.subscribe(subscriber);
        observable.subscribe(subscriber);
    }
}