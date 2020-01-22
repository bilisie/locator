package com.bilisie.locator.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableAutoConfiguration
@ComponentScan(basePackages = {"com.bilisie", "com.intuit.fuzzymatcher"})
public class Locator extends SpringBootServletInitializer implements CommandLineRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(Locator.class);

    private final ServerInfo serverInfo;

    @Autowired
    public Locator(ServerInfo serverInfo) {
        this.serverInfo = serverInfo;
    }

    public static void main(String[] args) {
        try {
            SpringApplication.run(Locator.class, args);
        } catch (Throwable tr) {
            LOGGER.error("An exception has occurred while starting the application", tr);
        }
    }

    @Override
    public void run(String... args) {
        LOGGER.error("Locator is running on port: " + serverInfo.getServer().getWebServer().getPort());
    }

}
