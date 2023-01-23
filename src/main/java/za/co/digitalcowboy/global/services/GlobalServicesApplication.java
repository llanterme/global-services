package za.co.digitalcowboy.global.services;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class GlobalServicesApplication {

    public static void main(String[] args) {
        SpringApplication.run(GlobalServicesApplication.class, args);
    }
}
