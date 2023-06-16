package com.b2c.oms;

import com.b2c.oms.thymeleaf.PagingDialect;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.bind.annotation.CrossOrigin;

@ComponentScan(basePackages = {"com.b2c.oms", "com.b2c.repository", "com.b2c.service"})
@SpringBootApplication
@EnableScheduling
public class OmsApplication {
//    private static Logger log = LoggerFactory.getLogger(OmsApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(OmsApplication.class, args);
    }
    @Bean
    public PagingDialect paging() {
        return new PagingDialect();
    }




}
