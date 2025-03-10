package org.dejoker;

import jakarta.websocket.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.dejoker.dto.TickerDataDto;
import org.dejoker.dto.TickerMetaDto;

import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Properties;

import static org.dejoker.Utils.*;

@Slf4j
@ClientEndpoint
public class WebsocketProducer {

    private final String KAFKA_ENDPOINT = "localhost:9092";
    private final String KAFKA_TOPIC = "test";
    private final String UPBIT_REST_URI = "https://api.upbit.com/v1/market/all";
    private final String UPBIT_SOCKET_URI = "wss://api.upbit.com/websocket/v1";

    private KafkaProducer<String, String> producer;
    private Session session;
    private HashMap<String, TickerMetaDto> tickerInfo;

    public WebsocketProducer() throws Exception {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA_ENDPOINT);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        this.producer = new KafkaProducer<>(props);
        this.tickerInfo = getTickerInfo(UPBIT_REST_URI);
    }

    @OnOpen
    public void onOpen(Session session) throws IOException {

        log.info("Connected to server");
        this.session = session;
        String request = createRequestMessage(tickerInfo);
        log.info(request);
        sendMessage(request);
        log.info("Request sent to server: {}", request);
    }

    @OnMessage
    public void onMessage(ByteBuffer buffer) throws Exception {
        String message = new String(buffer.array(), StandardCharsets.UTF_8);
        TickerDataDto dto = new TickerDataDto(message);
        log.info(dto.toString());
        ProducerRecord<String, String> record = new ProducerRecord<>(KAFKA_TOPIC, dto.getCode(), dto.toString());
        producer.send(record);
    }

    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        log.info("Connection closed: {}", closeReason.getReasonPhrase());
    }

    @OnError
    public void onError(Session session, Throwable throwable) throws IOException {
        log.error(throwable.getMessage());
    }

    public void sendMessage(String message) {
        if (session != null && session.isOpen()) {
            try {
                session.getBasicRemote().sendText(message);
            } catch (Exception e) {
                log.error("Error sending message: ", e);
            }
        } else {
            log.warn("Session is not open. Cannot send message.");
        }
    }

    public void run() throws Exception {
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        container.connectToServer(this, URI.create(UPBIT_SOCKET_URI));
        log.error("test");
        Thread.sleep(5000);
        while (true) {
            Thread.sleep(1000);
        }
    }

}
