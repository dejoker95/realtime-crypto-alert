package org.dejoker;

public class ProducerMain {
    public static void main(String[] args) {
        try {
            new WebsocketProducer().run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}