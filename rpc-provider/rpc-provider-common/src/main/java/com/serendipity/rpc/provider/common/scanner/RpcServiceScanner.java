package com.serendipity.rpc.provider.common.scanner;

import com.serendipity.rpc.annotation.RpcService;
import com.serendipity.rpc.common.helper.RpcServiceHelper;
import com.serendipity.rpc.common.scanner.ClassScanner;
import com.serendipity.rpc.protocol.meta.ServiceMeta;
import com.serendipity.rpc.registry.api.RegistryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.spi.ServiceRegistry;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * RpcService注解的扫描器
 *
 * @author serendipity
 * @version 1.0
 * @date 2024/2/4
 **/
public class RpcServiceScanner extends ClassScanner {


    private static final Logger logger = LoggerFactory.getLogger(RpcServiceScanner.class);


    /**
     * 扫描指定包下的类，并筛选使用 @RpcService 注解标注的类
     * 将服务提供者的元数据信息注册到注册中心，并将标注的实现类放入一个Map缓存中
     *
     * @param scanPackage 指定的包文件
     * @return
     * @throws Exception
     */
    public static Map<String, Object> doScannerWithRpcServiceAnnotationFilterAndRegistryService(String host, int port, String scanPackage, RegistryService registryService) throws Exception {

        Map<String, Object> handlerMap = new HashMap<>();
        List<String> classNameList = getClassNameList(scanPackage);
        if (classNameList == null || classNameList.isEmpty()) {
            return handlerMap;
        }

        classNameList.stream().forEach(className -> {
            try {
                // 获得该类的注解信息
                Class<?> clazz = Class.forName(className);
                // 检测 该类是否被注解 RpcService 标注
                RpcService rpcService = clazz.getAnnotation(RpcService.class);
                if (rpcService != null) {
                    //构建当前类待注册到注册中心的的元数据信息
                    ServiceMeta serviceMeta = new ServiceMeta(getServiceName(rpcService), rpcService.version(), rpcService.group(), host, port);
                    // 将元数据注册到注册中心
                    registryService.register(serviceMeta);
                    // 构造 k-v 存入本地map
                    String key = RpcServiceHelper.buildServiceKey(serviceMeta.getServiceName(), serviceMeta.getServiceVersion(), serviceMeta.getServiceGroup());
                    handlerMap.put(key, clazz.newInstance());
                }

            } catch (Exception e) {
                logger.error("scan classes throws exception: {}", e);
            }
        });
        return handlerMap;
    }


    /**
     * 获取serviceName
     * @param rpcService RpcService 实例
     * @return serviceName的名称
     */
    private static String getServiceName(RpcService rpcService){
        //优先使用interfaceClass
        Class clazz = rpcService.interfaceClass();
        if (clazz == void.class){
            return rpcService.interfaceClassName();
        }
        String serviceName = clazz.getName();
        if (serviceName == null || serviceName.trim().isEmpty()){
            serviceName = rpcService.interfaceClassName();
        }
        return serviceName;
    }
}

