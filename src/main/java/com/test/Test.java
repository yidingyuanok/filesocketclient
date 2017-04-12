package com.test;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Random;

/**
 * Created by Turing on 2017/4/5.
 */

//1. 抽象主题
 interface Moveable {
    void move()  throws Exception;
}
//2. 真实主题
 class Car implements Moveable {
    public void move() throws Exception {
        Thread.sleep(new Random().nextInt(1000));
        System.out.println("汽车行驶中…");
    }
}
//3.事务处理器
 class TimeHandler implements InvocationHandler {
    private Object target;

    public TimeHandler(Object target) {
        super();
        this.target = target;
    }

    /**
     * 参数：
     *proxy 被代理的对象
     *method 被代理对象的方法
     *args 方法的参数
     * 返回： Object 方法返回值
     */
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        long startTime = System.currentTimeMillis();
        System.out.println("汽车开始行驶…");
        method.invoke(target, args);
        long stopTime = System.currentTimeMillis();
        System.out.println("汽车结束行驶…汽车行驶时间：" + (stopTime - startTime) + "毫秒！");
        return null;
    }

}
//测试类
public class Test {
    public static void main(String[] args) throws Exception{
        Car car = new Car();
        InvocationHandler invocationHandler = new TimeHandler(car);
        Class<?> cls = car.getClass();
        /**
         *loader 类加载器
         *interfaces 被代理类实现的接口
         *invocationHandler InvocationHandler
         */
        Moveable moveable = (Moveable) Proxy.newProxyInstance(cls.getClassLoader(),cls.getInterfaces(), invocationHandler);
        moveable.move();
    }
}