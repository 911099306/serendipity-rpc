package com.serendipity.rpc.demo.spring.annotation.consumer.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author serendipity
 * @version 1.0
 * @date 2024/2/22
 **/
@Configuration
@ComponentScan(value = {"com.serendipity.rpc.*"})
public class SpringAnnotationConsumerConfig {
}
