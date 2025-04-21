package com.Pumpkin.Controller;

import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import javax.websocket.WebSocketContainer;
import javax.websocket.server.ServerEndpointConfig;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class RoomWebSocketContainer {

    public static void main(String[] args) {
        ServerEndpointConfig serverConfig = ServerEndpointConfig.Builder.create(RoomServerEndpoint.class,"/Room").build();
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();

        try{
            container.connectToServer(serverConfig,new URI("ws://101.37.135.39:8081/"));

        } catch (DeploymentException | URISyntaxException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
