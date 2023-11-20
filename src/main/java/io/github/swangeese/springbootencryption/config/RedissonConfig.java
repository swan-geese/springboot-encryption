package io.github.swangeese.springbootencryption.config;

import cn.hutool.core.text.CharSequenceUtil;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.stream.Stream;

/**
 * redisson配置，下面是单节点配置：
 *
 * @author GLP
 * @date 2023/7/14 13:40
 */
@Configuration
public class RedissonConfig {
    @Value("${spring.redis.host}")
    private String host;
    @Value("${spring.redis.port}")
    private String port;
    @Value("${spring.redis.password:}")
    private String password;
    @Value("${spring.redis.database}")
    private Integer database;
    @Value("${spring.redis.cluster.nodes:}")
    private String clusterNodes;

    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        if (!CharSequenceUtil.isEmpty(clusterNodes)) {
            //集群cluster
            String[] nodeAddr = Stream.of(clusterNodes.split(",")) .map(url -> String.format("redis://%s", url)).toArray(String[]::new);
            config.useClusterServers().setScanInterval(2000).addNodeAddress(nodeAddr);
            config.useClusterServers().setPassword(CharSequenceUtil.emptyToDefault(password, null));
        } else {
            //单节点
            config.useSingleServer().setAddress("redis://" + host + ":" + port);
            config.useSingleServer().setDatabase(database);
            config.useSingleServer().setPassword(CharSequenceUtil.emptyToDefault(password, null));
        }
        return Redisson.create(config);
    }
}
