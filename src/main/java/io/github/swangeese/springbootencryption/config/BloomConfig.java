package io.github.swangeese.springbootencryption.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


/**
 * @author zy
 * @desc 布隆过滤器配置
 */
@Configuration
@ConfigurationProperties(prefix = "bloom")
@Getter
@Setter
public class BloomConfig {
    /**
     * 是否开启布隆过滤器
     */
    private Boolean enable;
    /**
     * 布隆过滤器误判率
     */
    private Double missRate;
    /**
     * 布隆过滤器大小
     */
    private Long size;
    /**
     * 布隆过滤器过期时间
     */
    private Long expireTime;
    /**
     * 布隆过滤器复位时间(以天为单位，且需大于expireTime)
     */
    private Long reset;



}
