package com.serendipity.rpc.test.spi;

import com.serendipity.rpc.spi.loader.ExtensionLoader;
import com.serendipity.rpc.test.spi.service.SPIService;
import org.junit.Test;

/**
 * @author serendipity
 * @version 1.0
 * @date 2024/2/19
 **/
public class SPITest {

    @Test
    public void testSpiLoader() {
        SPIService spiService = ExtensionLoader.getExtension(SPIService.class, "spiService");
        String serendipity = spiService.hello("serendipity");
        System.out.println("serendipity = " + serendipity);
    }
}
