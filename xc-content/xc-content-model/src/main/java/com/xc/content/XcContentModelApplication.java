package com.xc.content;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//@MapperScan("com.xc.content.model.mapper")
@SpringBootApplication
public class XcContentModelApplication {

    public static void main(String[] args) {
        SpringApplication.run(XcContentModelApplication.class, args);
    }

}
