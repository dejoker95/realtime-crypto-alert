package org.dejoker.dto;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TickerData {

    private String type;
    private String code;
    private long timestamp;
    private double tradePrice;
    private double tradeVolume;

    public TickerData(String jsonString) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(jsonString);
        this.type = node.get("type").asText();
        this.code = node.get("code").asText();
        this.timestamp = node.get("timestamp").asLong();
        this.tradePrice = node.get("trade_price").asDouble();
        this.tradeVolume = node.get("trade_volume").asDouble();
    }

    @Override
    public String toString() {
        return "{" +
                "\"type\":\"" + type + '\"' +
                ", \"code\":\"" + code + '\"' +
                ", \"timestamp\":" + timestamp +
                ", \"tradePrice\":" + tradePrice +
                ", \"trade_volume\":" + tradeVolume +
                '}';
    }

}

