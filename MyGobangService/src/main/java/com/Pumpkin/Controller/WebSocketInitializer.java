package com.Pumpkin.Controller;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import javax.websocket.WebSocketContainer;
import javax.websocket.server.ServerContainer;
import javax.websocket.server.ServerEndpointConfig;
import java.net.URI;
import java.net.URISyntaxException;

public class WebSocketInitializer {
    public static void main(String[] args) {
        ServerEndpointConfig serverEndpointConfig = ServerEndpointConfig.Builder.create(middleServerEndpoint.class,"/Game").build();
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        try {
            container.connectToServer(serverEndpointConfig,new URI("101.37.135.39"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
