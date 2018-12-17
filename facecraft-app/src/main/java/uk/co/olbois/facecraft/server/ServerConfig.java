/*
 * Copyright (c) 2018 Ian Clement.  All rights reserved.
 */

package uk.co.olbois.facecraft.server;

import java.io.IOException;
import java.net.CookieManager;

/**
 * Server configuration singleton class.
 *
 * @author Ian Clement (ian.clement@johnabbott.qc.ca)
 * @version 1
 */
public class ServerConfig {

    // Server address and port config
    private static final String DEFAULT_HOST = "127.0.0.1";
    private static final int DEFAULT_PORT = 80;
    private static final String DEFAULT_PROTOCOL = "http";

    // Username and password for client login.
    private static final String CLIENT_USERNAME = "user";
    private static final String CLIENT_PASSWORD = "user";

    // store the singleton instance
    private static ServerConfig instance;

    private String host;
    private int port;
    private String protocol;

    /**
     * Get singleton instance.
     * @return
     */
    public static ServerConfig getInstance() {
        // lazy instantiation.
        if(instance == null)
            instance = new ServerConfig();
        return instance;
    }

    // stores all cookies for this server
    private CookieManager cookieManager;

    // flag to indicate whether the client app is logged in.
    private boolean loggedIn;

    // Create a ServerConfig instance.
    // private to implemented the singleton pattern
    private ServerConfig() {
        cookieManager = new CookieManager();
        host = DEFAULT_HOST;
        port = DEFAULT_PORT;
        protocol = DEFAULT_PROTOCOL;
    }

    /**
     * Set the host of the server.
     * @param host
     * @return
     */
    public ServerConfig setHost(String host) {
        this.host = host;
        return this;
    }

    /**
     * Set the port of the server
     * @param port
     * @return
     */
    public ServerConfig setPort(int port) {
        this.port = port;
        return this;
    }

    /**
     * Set the protocol of the server
     * @param protocol
     * @return
     */
    public ServerConfig setProtocol(String protocol) {
        this.protocol = protocol;
        return this;
    }

    /**
     * Get the URL prefix.
     * @return
     */
    public String getUrlPrefix() {
        return String.format("%s://%s:%s", protocol, host, port);
    }

    /**
     * Get the cookie manager.
     * @return
     */
    public CookieManager getCookieManager() {
        return cookieManager;
    }

    /**
     * Is the client logged in.
     * @return
     */
    public boolean isLoggedIn() {
        return loggedIn;
    }

    /**
     * Perform client login.
     * @throws IOException
     * @throws ServerException
     */
    public void clientLogin() throws IOException, ServerException {
        /*HttpResponse response = new HttpRequestBuilder("login")
                .method(HttpRequestBuilder.Method.POST)
                .withRequestBody(String.format("username=%s&password=%s", CLIENT_USERNAME, CLIENT_PASSWORD))
                .cookies()
                .expectingStatus(200)
                .perform();
        loggedIn = true;*/
    }

}
