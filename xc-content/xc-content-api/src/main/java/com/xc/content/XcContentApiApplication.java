package com.xc.content;

import com.spring4all.swagger.EnableSwagger2Doc;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableSwagger2Doc
@SpringBootApplication(scanBasePackages = "com.xc")
public class XcContentApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(XcContentApiApplication.class, args);
    }

}
