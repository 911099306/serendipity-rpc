package com.serendipity.rpc.annotation;

import org.springframework.beans.factory.annotation.Autowired;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author serendipity
 * @version 1.0.0
 * @date 2024/2/3
 **/
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Autowired
public @interface RpcReference {

    /**
     * 版本号
     * @return
     */
    String version() default "1.0.0";


    /**
     * 注册中心： zookeeper、 consul、 etcd、 nacos
     * @return
     */
    String registryType() default "zookeeper";

    /**
     * 注册地址
     */
    String registryAddress() default "127.0.0.1:2181";

    /**
     * 负载均衡类型：默认基于 zk 的一致性 hash
     */
    String loadBalanceType() default "zkconsistenthash";


    /**
     * 序列话类型：jdk、hessian2、protostuff、json、fst、kryo
     */
    String serializationType() default "protostuff";

    /**
     * 超时时间，默认 5s
     */
    long timeout() default 5000;

    /**
     * 是否异步执行
     */
    boolean async() default false;

    /**
     * 是否单项调用
     */
    boolean oneway() default false;

    /**
     * 代理类型：jdk、Cglib、javassist
     * @return
     */
    String proxy() default "jdk";

    /**
     * 服务分组，默认为空
     * @return
     */
    String group() default "";
}


