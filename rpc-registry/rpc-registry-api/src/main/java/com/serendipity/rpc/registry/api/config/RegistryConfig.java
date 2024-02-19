package com.serendipity.rpc.registry.api.config;

import java.io.Serializable;

/**
 * 注册配置类
 *
 * @author serendipity
 * @version 1.0
 * @date 2024/2/18
 **/
public class RegistryConfig implements Serializable {
    private static final long serialVersionUID = -7248658103788758893L;

    /**
     * 注册地址
     */
    private String registryAddr;

    /**
     * 注册类型
     */
    private String registryType;


    public RegistryConfig(String registryAddr, String registryType) {
        this.registryAddr = registryAddr;
        this.registryType = registryType;
    }

    public String getRegistryAddr() {
        return registryAddr;
    }

    public void setRegistryAddr(String registryAddr) {
        this.registryAddr = registryAddr;
    }

    public String getRegistryType() {
        return registryType;
    }

    public void setRegistryType(String registryType) {
        this.registryType = registryType;
    }
}
