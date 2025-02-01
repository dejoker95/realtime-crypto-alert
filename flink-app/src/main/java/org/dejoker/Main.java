package org.dejoker;


import lombok.extern.slf4j.Slf4j;
import org.dejoker.flink.FlinkApp;

@Slf4j
public class Main {
    public static void main(String[] args) {
        try {
            new FlinkApp().run();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}