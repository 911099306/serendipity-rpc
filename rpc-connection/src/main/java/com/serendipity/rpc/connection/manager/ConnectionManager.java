package com.serendipity.rpc.connection.manager;

import com.serendipity.rpc.common.utils.StringUtils;
import com.serendipity.rpc.constants.RpcConstants;
import com.serendipity.rpc.disuse.api.DisuseStrategy;
import com.serendipity.rpc.disuse.api.connection.ConnectionInfo;
import com.serendipity.rpc.spi.loader.ExtensionLoader;
import io.netty.channel.Channel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 连接管理器
 *
 * @author serendipity
 * @version 1.0
 * @date 2024/2/23
 **/
public class ConnectionManager {

    private volatile Map<String, ConnectionInfo> connectionMap = new ConcurrentHashMap<>();
    private final DisuseStrategy disuseStrategy;
    private final int maxConnections;
    private static volatile ConnectionManager instance;

    private ConnectionManager(int maxConnections, String disuseStrategyType){
        this.maxConnections = maxConnections <= 0 ? Integer.MAX_VALUE : maxConnections;
        disuseStrategyType = StringUtils.isEmpty(disuseStrategyType) ? RpcConstants.RPC_CONNECTION_DISUSE_STRATEGY_DEFAULT : disuseStrategyType;
        this.disuseStrategy = ExtensionLoader.getExtension(DisuseStrategy.class, disuseStrategyType);
    }

    /**
     * 单例模式
     * @param maxConnections 最大连接数
     * @param disuseStrategyType 淘汰策略
     * @return 连接管理
     */
    public static ConnectionManager getInstance(int maxConnections, String disuseStrategyType){
        if (instance == null){
            synchronized (ConnectionManager.class){
                if (instance == null){
                    instance = new ConnectionManager(maxConnections, disuseStrategyType);
                }
            }
        }
        return instance;
    }

    /**
     * 添加连接
     * @param channel 连接channel
     */
    public void add(Channel channel){
        ConnectionInfo info = new ConnectionInfo(channel);
        System.out.println("加入了一个channel" + channel.id().asLongText());
        if (this.checkConnectionList(info)){
            connectionMap.put(getKey(channel), info);
        }
    }

    /**
     * 移除连接
     * @param channel 连接channel
     */
    public void remove(Channel channel){
        connectionMap.remove(getKey(channel));
    }

    /**
     * 更新连接信息
     * @param channel 连接信息
     */
    public void update(Channel channel){
        ConnectionInfo info = connectionMap.get(getKey(channel));
        info.setLastUseTime(System.currentTimeMillis());
        info.incrementUseCount();
        connectionMap.put(getKey(channel), info);
    }

    /**
     * 检测连接数量是否 大于等于 最大连接数，若是则通过淘汰策略获取一个链接进行淘汰，
     * @param info 连接信息
     * @return info是否成功连接并加入管理器
     */
    private boolean checkConnectionList(ConnectionInfo info) {
        List<ConnectionInfo> connectionList = new ArrayList<>(connectionMap.values());
        if (connectionList.size() >= maxConnections){
            try{
                ConnectionInfo cacheConnectionInfo = disuseStrategy.selectConnection(connectionList);
                if (cacheConnectionInfo != null){
                    // System.out.println("删除一个channel ：  "+cacheConnectionInfo.getChannel().id().asLongText());
                    cacheConnectionInfo.getChannel().close();
                    connectionMap.remove(getKey(cacheConnectionInfo.getChannel()));
                }
            }catch (RuntimeException e){
                info.getChannel().close();
                return false;
            }
        }
        return true;
    }


    private String getKey(Channel channel){
        return channel.id().asLongText();
    }
}
