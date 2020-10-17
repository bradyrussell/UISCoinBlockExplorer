package com.bradyrussell.blockexplorerweb;

import org.jetbrains.annotations.NotNull;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;

import javax.servlet.ServletContextListener;

@SpringBootApplication
public class BlockexplorerwebApplication {

    public static void main(String[] args) {
        SpringApplication.run(BlockexplorerwebApplication.class, args);
    }

    @NotNull
    @Bean
    ServletListenerRegistrationBean<ServletContextListener> myServletListener() {
        ServletListenerRegistrationBean<ServletContextListener> srb = new ServletListenerRegistrationBean<>();
        srb.setListener(new BlockChainServletContextListener());
        return srb;
    }
}
