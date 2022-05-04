package com.csu.mall.util;


import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class TokenCacheUtil {

    private static Logger logger = LoggerFactory.getLogger(TokenCacheUtil.class);

    private static LoadingCache<String, String> localCache =
            CacheBuilder.newBuilder().initialCapacity(1000).maximumSize(5000)
                    .expireAfterAccess(10, TimeUnit.MINUTES)
                    .build(new CacheLoader<String, String>() {
                        @Override
                        public String load(String s) throws Exception {
                            return null;
                        }
                    });

    public static void setToken(String key, String value) {
        localCache.put(key, value);
    }

    public static String getToken(String key) {
        String token = null;
        try {
            token = localCache.get(key);
            localCache.invalidate(key);
        } catch (Exception e) {
            logger.error("Get token from Guava LocalCache Error...",e);
        }
        return token;
    }
}
