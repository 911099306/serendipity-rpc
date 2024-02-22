package com.serendipity.rpc.demo.spring.boot.provider;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author serendipity
 * @version 1.0
 * @date 2024/2/22
 **/
@SpringBootApplication
@ComponentScan(value = {"com.serendipity.rpc"})
public class SpringBootProviderDemoStarter {
    public static void main(String[] args){
        SpringApplication.run(SpringBootProviderDemoStarter.class, args);
    }
}
