package com.serendipity.rpc.test.registry;

import com.serendipity.rpc.protocol.meta.ServiceMeta;
import com.serendipity.rpc.registry.api.RegistryService;
import com.serendipity.rpc.registry.api.config.RegistryConfig;
import com.serendipity.rpc.registry.zookeeper.ZookeeperRegistryService;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

/**
 * @author serendipity
 * @version 1.0
 * @date 2024/2/18
 **/
public class ZookeeperRegistryTest {

    private RegistryService registryService;

    private ServiceMeta serviceMeta;

    @Before
    public void init() throws Exception {
        RegistryConfig registryConfig = new RegistryConfig("127.0.0.1:2181", "zookeeper", "random");
        this.registryService = new ZookeeperRegistryService();
        this.registryService.init(registryConfig);
        this.serviceMeta = new ServiceMeta(ZookeeperRegistryTest.class.getName(), "1.0.0", "serendipity", "127.0.0.1", 8080);
    }

    @Test
    public void testRegister() throws Exception {
        this.registryService.register(serviceMeta);
    }

    @Test
    public void testUnRegister() throws Exception {
        this.registryService.unRegister(serviceMeta);
    }

    @Test
    public void testDiscovery() throws Exception {
        this.registryService.discovery(RegistryService.class.getName(), "serendipity".hashCode());
    }

    @Test
    public void testDestroy() throws IOException {
        this.registryService.destroy();
    }
}
