package org.ein.erste.iot.account;

import org.ein.erste.iot.account.settings.Settings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PreDestroy;

@SpringBootApplication
public class AccountApplication {
    private static Logger logger;

    public static void main(String[] args) {
        Settings.loadCustomIniFile(args);
        logger = LoggerFactory.getLogger(AccountApplication.class);
        SpringApplication.run(AccountApplication.class, args);
        logger.info("Server Started");
        logger.info("HTTP Server Port " + System.getProperty("server.http.port"));
    }

    @PreDestroy
    public void onExit() {
        logger.info("###STOPing###");
        logger.info("###STOP FROM THE LIFECYCLE###");
    }
}