package com.zhuxn.inforelease;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableTransactionManagement
@SpringBootApplication
@MapperScan(basePackages = { "com.zhuxn.inforelease.orm.mapper","com.zhuxn.inforelease.shiro.orm.mapper" })
public class InfoReleaseApplication {

	public static void main(String[] args) {
		SpringApplication.run(InfoReleaseApplication.class, args);
	}

}
