package com.serendipity.rpc.test.scanner;


import com.serendipity.rpc.common.scanner.ClassScanner;
import com.serendipity.rpc.common.scanner.reference.RpcReferenceScanner;
import com.serendipity.rpc.provider.common.scanner.RpcServiceScanner;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author serendipity
 * @version 1.0
 * @date 2024/2/4
 **/
public class ScannerTest {


    /**
     * 扫描 com.serendipity.rpc.test.scanner 包下所有类
     * @throws IOException
     */
    @Test
    public void testScannerClassNameList() throws IOException {
        List<String> classNameList = ClassScanner.getClassNameList("com.serendipity.rpc.test.scanner");
        classNameList.forEach(System.out::println);
    }

    /**
     * 查询 com.serendipity.rpc.test.scanner 包下的所有 RpcService 类
     * @throws Exception
     */
    @Test
    public void testScannerClassNameListByRpcService() throws  Exception {
        // Map<String, Object> handlerMap = RpcServiceScanner.doScannerWithRpcServiceAnnotationFilterAndRegistryService("com.serendipity.rpc.test.scanner");
    }

    @Test
    public void testScannerClassNameListByRpcReference() throws Exception {
        // Map<String, Object> stringObjectMap = RpcReferenceScanner.doScannerWithRpcReferenceAnnotationFilter("com.serendipity.rpc.test.scanner");
    }
}
