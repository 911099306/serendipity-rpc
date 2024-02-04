package com.serendipity.rpc.common.scanner.server;

import com.serendipity.rpc.annotation.RpcService;
import com.serendipity.rpc.common.scanner.ClassScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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


    private static final Logger LOGGER = LoggerFactory.getLogger(RpcServiceScanner.class);


    /**
     * 扫描指定包下的类，并筛选使用 @RpcService 注解标注的类
     * 将服务提供者的元数据信息注册到注册中心，并将标注的实现类放入一个Map缓存中
     *
     * @param scanPackage 指定的包文件
     * @return
     * @throws Exception
     */
    public static Map<String, Object> doScannerWithRpcServiceAnnotationFilterAndRegistryService(String scanPackage) throws Exception {

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
                    //优先使用interfaceClass， interfaceClass 的name为空，再使用interfaceClassName
                    // TODO 后续逻辑向注册中心注册服务元数据，同时向 handlerMap中记录标注了RpcService注解的类实例
                    LOGGER.info("当前标注了@RpcService注解的类实例名称===>>> " + clazz.getName());
                    LOGGER.info("@RpcService注解上标注的属性信息如下：");
                    LOGGER.info("interfaceClass===>>> " + rpcService.interfaceClass().getName());
                    LOGGER.info("interfaceClassName===>>> " + rpcService.interfaceClassName());
                    LOGGER.info("version===>>> " + rpcService.version());
                    LOGGER.info("group===>>> " + rpcService.group());
                }

            } catch (ClassNotFoundException e) {
                LOGGER.error("scan classes throws exception: {}", e);
                throw new RuntimeException(e);
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

