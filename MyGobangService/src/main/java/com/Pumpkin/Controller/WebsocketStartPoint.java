package com.Pumpkin.Controller;

import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import javax.websocket.WebSocketContainer;
import javax.websocket.server.ServerEndpointConfig;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebsocketStartPoint {
    public static void main(String[] args) {
        ServerEndpointConfig serverEndpointConfig = ServerEndpointConfig.Builder.create(middleServerEndpoint.class, "/Game").build();
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        try {
            container.connectToServer(serverEndpointConfig, new URI("ws://101.37.135.39:8081/"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
