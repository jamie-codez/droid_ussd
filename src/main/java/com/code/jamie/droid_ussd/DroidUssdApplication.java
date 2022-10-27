package com.code.jamie.droid_ussd;

import com.elarian.Elarian;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class DroidUssdApplication {

    public static void main(String[] args) {
        SpringApplication.run(DroidUssdApplication.class, args);
    }
//    @Bean
//    public Elarian getElarian(){
//        return new Elarian("el_k_test_a1d6a7b64ce74a9739d116db38f3f39ddd9c774e4107d45c83416fbb50606e92",
//                "el_org_eu_WetuZC", "el_app_R6C6Yk");
//    }
}
