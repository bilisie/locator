package com.bilisie.locator.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import org.springframework.stereotype.Component;

@SuppressWarnings({"unused"})
@Component
public class ServerInfo {

    @Autowired
    private ServletWebServerApplicationContext server;

    public ServletWebServerApplicationContext getServer() {
        return server;
    }

    public void setServer(ServletWebServerApplicationContext server) {
        this.server = server;
    }

}