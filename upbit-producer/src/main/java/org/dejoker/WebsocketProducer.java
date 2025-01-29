package org.dejoker;


import jakarta.websocket.*;

import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;


import static org.dejoker.Utils.*;

@ClientEndpoint
public class WebsocketProducer {

    private final String KAFKA_ENDPOINT = "localhost:9092";
    private final String UPBIT_REST_URI = "https://api.upbit.com/v1/market/all";
    private final String UPBIT_SOCKET_URI = "wss://api.upbit.com/websocket/v1";

    private Session session;
    private HashMap<String, TickerDto> tickerInfo;

    public WebsocketProducer() throws Exception {

        this.tickerInfo = getTickerInfo(UPBIT_REST_URI);
    }

    @OnOpen
    public void onOpen(Session session) throws IOException {
        this.session = session;
        System.out.println("Connected to server");
        sendMessage(createRequestMessage(tickerInfo));
    }

    @OnMessage
    public void onMessage(ByteBuffer buffer) {
        System.out.println(new String(buffer.array(), StandardCharsets.UTF_8));
    }

    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        System.out.println("Connection closed: " + closeReason.getReasonPhrase());
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        throwable.printStackTrace();
    }

    public void sendMessage(String message) {
        try {
            session.getBasicRemote().sendText(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void run() throws Exception {
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        container.connectToServer(WebsocketProducer.class, URI.create(UPBIT_SOCKET_URI));
        while (true) {
            Thread.sleep(1000);
        }
    }

}
