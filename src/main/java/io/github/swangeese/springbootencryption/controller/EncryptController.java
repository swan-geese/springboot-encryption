package io.github.swangeese.springbootencryption.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zy
 * @version 1.0
 * @date Created in 2023/11/20 10:53 AM
 * @description 代码加密controller
 */

@RestController
@RequestMapping("/encrypt")
@Slf4j
public class EncryptController {

    @RequestMapping("/test")
    public String test() {
        return "test";
    }
}
