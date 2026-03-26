package com.polywave.billservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;
import com.polywave.billservice.config.AgendaProperties;

@SpringBootApplication(scanBasePackages = {
        "com.polywave.billservice",
        "com.polywave.security",
        "com.polywave.common"
})
@EnableScheduling
@EnableConfigurationProperties(AgendaProperties.class)
public class BillServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(BillServiceApplication.class, args);
    }

}
