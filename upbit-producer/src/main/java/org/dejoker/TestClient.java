package org.dejoker;

import jakarta.websocket.*;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

@ClientEndpoint
public class TestClient {

    private Session session;

    @OnOpen
    public void onOpen(Session session) throws IOException {
        this.session = session;
        System.out.println("Connected to server");
        sendMessage("[{\"ticket\":\"test\"},{\"type\":\"ticker\",\"is_only_realtime\":\"true\",\"codes\":[\"KRW-BTC\"]}]");
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
}
