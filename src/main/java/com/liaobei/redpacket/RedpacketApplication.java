package com.liaobei.redpacket;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan(value = {"com.liaobei.redpacket.dao"})
public class RedpacketApplication {

    public static void main(String[] args) {
        SpringApplication.run(RedpacketApplication.class, args);
    }

}
