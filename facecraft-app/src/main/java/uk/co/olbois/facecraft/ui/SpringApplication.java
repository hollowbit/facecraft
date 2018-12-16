package uk.co.olbois.facecraft.ui;

import android.app.Application;

import uk.co.olbois.facecraft.server.ServerConfig;

public class SpringApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        ServerConfig.getInstance().setHost("10.0.2.2");
        ServerConfig.getInstance().setPort(9999);
        ServerConfig.getInstance().setProtocol("http");
    }

}
