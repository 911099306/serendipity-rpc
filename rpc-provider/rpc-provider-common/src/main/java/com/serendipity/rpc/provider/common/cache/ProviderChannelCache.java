package com.serendipity.rpc.provider.common.cache;

import io.netty.channel.Channel;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @author serendipity
 * @version 1.0
 * @date 2024/2/21
 **/
public class ProviderChannelCache {
    private static volatile Set<Channel> channelCache = new CopyOnWriteArraySet<>();

    public static void add(Channel channel){
        channelCache.add(channel);
    }

    public static void remove(Channel channel){
        channelCache.remove(channel);
    }

    public static Set<Channel> getChannelCache(){
        return channelCache;
    }
}
