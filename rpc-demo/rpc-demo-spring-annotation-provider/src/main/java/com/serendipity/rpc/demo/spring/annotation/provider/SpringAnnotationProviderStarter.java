package com.serendipity.rpc.demo.spring.annotation.provider;

import com.serendipity.rpc.demo.spring.annotation.provider.config.SpringAnnotationProviderConfig;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author serendipity
 * @version 1.0
 * @date 2024/2/22
 **/
public class SpringAnnotationProviderStarter {
    public static void main(String[] args) {
        new AnnotationConfigApplicationContext(SpringAnnotationProviderConfig.class);
    }
}
