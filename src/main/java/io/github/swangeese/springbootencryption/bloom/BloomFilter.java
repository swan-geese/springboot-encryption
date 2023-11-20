package io.github.swangeese.springbootencryption.bloom;

import cn.hutool.core.util.RandomUtil;
import io.github.swangeese.springbootencryption.config.BloomConfig;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RMapCache;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Set;

/**
 * @author zy
 * @date 2023/11/13 15:07
 * @description:
 */
@Component
@RequiredArgsConstructor
public class BloomFilter implements DisposableBean {

    private final RedissonClient redissonClient;

    private volatile long curTimeStamp;

    private RBloomFilter<String> cur;

    private RBloomFilter<String> feature;

    private static String PRI = "%s:bloom:%s";

    private static final String KEY = "keys:bloom:";


    @Resource
    private BloomConfig bloomConfig;


    public void init(String prefix) {

        long timeMillis = System.currentTimeMillis();
        if (cur == null) {
            RMapCache<Object, Object> mapCache = redissonClient.getMapCache(KEY);
            // 当前过滤器
            String random = RandomUtil.randomString(8);
            String key = String.format(PRI, prefix, random);

            mapCache.put(key, timeMillis);
            cur = redissonClient.getBloomFilter(key);
            // 使用最多允许 size 个元素的布隆过滤器
            cur.tryInit(bloomConfig.getSize(), bloomConfig.getMissRate());
            curTimeStamp = timeMillis;
        }
        if (timeMillis - curTimeStamp >= bloomConfig.getExpireTime() && feature == null) {
            RMapCache<Object, Object> mapCache = redissonClient.getMapCache(KEY);
            // T + 2 过滤器
            String randomNext = RandomUtil.randomString(8);
            String nextKey = String.format(PRI, prefix, randomNext);

            mapCache.put(nextKey, timeMillis);
            feature = redissonClient.getBloomFilter(nextKey);
            // 使用最多允许 size 个元素的布隆过滤器
            feature.tryInit(bloomConfig.getSize(), bloomConfig.getMissRate());
            clearBitMap(mapCache, timeMillis);
        }

    }

    private void clearBitMap(RMapCache<Object, Object> mapCache, long timeMillis) {
        final int three = 3;
        final int second = 2;
        Set<Object> keySet = mapCache.keySet();
        if (keySet.size() <= second) {
            return;
        }
        for (Object o : keySet) {
            String key = (String) o;
            if (timeMillis - (Long) mapCache.get(key) > three * bloomConfig.getExpireTime()) {
                RBloomFilter<String> delete = redissonClient.getBloomFilter(key);
                delete.deleteAsync();
                mapCache.removeAsync(key);
            }
        }
    }


    public boolean filter(String prefix, String value) {
        init(prefix);
        final int multiple = 2;
        if (System.currentTimeMillis() - curTimeStamp < bloomConfig.getExpireTime()) {
            boolean contains = cur.contains(value);
            if (!contains) {
                cur.add(value);
            }
            return contains;
        } else if (System.currentTimeMillis() - curTimeStamp < multiple * bloomConfig.getExpireTime()) {
            boolean contains = cur.contains(value);
            if (!contains) {
                cur.add(value);
                feature.add(value);
            }
            return contains;
        } else {
            curTimeStamp = System.currentTimeMillis();
            cur.deleteAsync();
            cur = feature;
            feature = null;
            boolean contains = cur.contains(value);
            if (!contains) {
                cur.add(value);
            }
            return contains;
        }

    }

    @Override
    public void destroy() throws Exception {
        if (cur != null) {
            cur.deleteAsync();
        }
        if (feature != null) {
            feature.deleteAsync();
        }
    }
}
