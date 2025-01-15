package cz.cvut.fel.ear.semestralka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SemestralkaApplication {
    public static void main(String[] args) {
        SpringApplication.run(SemestralkaApplication.class, args);
    }

}
