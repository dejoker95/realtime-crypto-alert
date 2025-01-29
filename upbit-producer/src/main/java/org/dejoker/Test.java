package org.dejoker;

import jakarta.websocket.ContainerProvider;
import jakarta.websocket.Session;
import jakarta.websocket.WebSocketContainer;

import java.net.URI;

import static org.dejoker.Utils.*;

public class Test {
    public static void main(String[] args) throws Exception {
        String uri = "wss://api.upbit.com/websocket/v1";
        try {
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            Session session = container.connectToServer(TestClient.class, URI.create(uri));
            Thread.sleep(5000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
