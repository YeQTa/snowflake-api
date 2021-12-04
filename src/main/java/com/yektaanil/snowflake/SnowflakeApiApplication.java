package com.yektaanil.snowflake;

import com.yektaanil.snowflake.generator.IdGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SnowflakeApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(SnowflakeApiApplication.class, args);
    }

}
