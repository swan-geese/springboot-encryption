package io.github.swangeese.springbootencryption.controller;

import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.ObjectUtil;
import io.github.swangeese.springbootencryption.bloom.BloomFilter;
import io.github.swangeese.springbootencryption.config.BloomConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zy
 * @version 1.0
 * @date Created in 2023/11/9 7:26 PM
 * @description 布隆过滤器controller
 */

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/bloom")
public class BloomFilterController {

    private final BloomFilter bloomFilter;

    private final BloomConfig bloomConfig;

    @GetMapping("/testBloom")
    public Boolean existBloom(String id) {
        String reqId = UUID.randomUUID().toString();
        return checkBloomFilter(id, reqId);
    }

    /**
     * 校验布隆过滤器
     */
    private Boolean checkBloomFilter(String id, String reqId) {
        // 布隆过滤器是否开启
        if (!bloomConfig.getEnable()) {
            log.info("布隆过滤器未开启");
            return false;
        }

        final String prefix = "filter";
        boolean flag = bloomFilter.filter(prefix, ObjectUtil.defaultIfBlank(id , reqId));
        if (flag) {
            log.info("布隆过滤器判定数据可能已经存在，不进行后续操作");
            return true;
        }
        return false;
    }
}
