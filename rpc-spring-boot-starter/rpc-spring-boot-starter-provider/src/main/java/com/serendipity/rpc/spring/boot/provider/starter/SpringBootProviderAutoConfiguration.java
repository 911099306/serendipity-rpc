package com.serendipity.rpc.spring.boot.provider.starter;

import com.serendipity.rpc.provider.spring.RpcSpringServer;
import com.serendipity.rpc.spring.boot.provider.config.SpringBootProviderConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RPC 服务提供者的自动配置类
 *
 * @author serendipity
 * @version 1.0
 * @date 2024/2/22
 **/
@Configuration
public class SpringBootProviderAutoConfiguration {

    @Bean
    @ConfigurationProperties(prefix = "rpc.serendipity.provider")
    public SpringBootProviderConfig springBootProviderConfig(){
        return new SpringBootProviderConfig();
    }

    @Bean
    public RpcSpringServer rpcSpringServer(final SpringBootProviderConfig springBootProviderConfig){
        return new RpcSpringServer(
                springBootProviderConfig.getServerAddress(),
                springBootProviderConfig.getServerRegistryAddress(),
                springBootProviderConfig.getRegistryAddress(),
                springBootProviderConfig.getRegistryType(),
                springBootProviderConfig.getRegistryLoadBalanceType(),
                springBootProviderConfig.getReflectType(),
                springBootProviderConfig.getHeartbeatInterval(),
                springBootProviderConfig.getScanNotActiveChannelInterval(),
                springBootProviderConfig.getEnableResultCache(),
                springBootProviderConfig.getResultCacheExpire());
    }
}
