package org.dejoker;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import org.dejoker.dto.TickerMetaDto;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


public class Utils {

    public static HashMap<String, TickerMetaDto> getTickerInfo(String uri) throws Exception {
        HttpClient client = HttpClient.newBuilder().build();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(uri)).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            ObjectMapper mapper = new ObjectMapper();
            CollectionType collectionType = mapper.getTypeFactory().constructCollectionType(List.class, TickerMetaDto.class);
            List<TickerMetaDto> list = mapper.readValue(response.body(), collectionType);
            return list
                    .stream()
                    .filter(dto -> dto.getMarket().startsWith("KRW"))
                    .collect(Collectors.toMap(
                            TickerMetaDto::getMarket,
                            dto -> dto,
                            (existing, replacement) -> existing,
                            HashMap::new
                    ));
        } else {
            throw new Exception("Response code not 200.");
        }
    }

    public static String createRequestMessage(HashMap<String, TickerMetaDto> map) {
        StringBuffer sb = new StringBuffer("[");
        map.keySet().stream().forEach(key -> sb.append("\"" + key +"\","));
        sb.deleteCharAt(sb.length() - 1);
        sb.append("]");
        String ticketField = "{\"ticket\":\"" + UUID.randomUUID().toString() + "\"}";
        String typeField = "{\"type\":\"ticker\",\"is_only_realtime\":\"true\",\"codes\":" + sb.toString() + "}";
        return "[" + ticketField + "," + typeField + "]";
//        return "[{\"ticket\":\"asdlfna;ioefnlsdnf-oainedsf-ansf\"},{\"type\":\"ticker\",\"is_only_realtime\":\"true\",\"codes\":[\"KRW-BTC\"]}]";
    }

}
