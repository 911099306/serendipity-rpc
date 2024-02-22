package com.serendipity.rpc.demo.spring.boot.consumer;

import com.serendipity.rpc.demo.spring.boot.consumer.service.ConsumerDemoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author serendipity
 * @version 1.0
 * @date 2024/2/22
 **/
@SpringBootApplication
@ComponentScan(basePackages = {"com.serendipity.rpc"})
public class SpringBootConsumerDemoStarter {
    private static final Logger logger = LoggerFactory.getLogger(SpringBootConsumerDemoStarter.class);
    public static void main(String[] args){
        ConfigurableApplicationContext context = SpringApplication.run(SpringBootConsumerDemoStarter.class, args);
        ConsumerDemoService consumerDemoService = context.getBean(ConsumerDemoService.class);
        String result = consumerDemoService.hello("binghe");
        logger.info("返回的结果数据===>>> " + result);
    }
}
