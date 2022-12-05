package com.toy.live;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan(basePackages = "com.toy.live.mapper")
@SpringBootApplication
public class ToyLiveApplication {

	public static void main(String[] args) {
		SpringApplication.run(ToyLiveApplication.class, args);
	}

}
