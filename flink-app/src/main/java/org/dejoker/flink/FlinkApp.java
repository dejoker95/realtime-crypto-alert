package org.dejoker.flink;

import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.AggregateFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.windowing.assigners.SlidingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;
import org.dejoker.dto.TickerData;

public class FlinkApp {

    private final String KAFKA_TOPIC = "test";
    private final String KAFKA_GROUP_ID = "test-group";
    private final String KAFKA_BROKERS = "localhost:9092";

    public void run() throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        KafkaSource<String> source = KafkaSource.<String>builder()
                .setBootstrapServers(KAFKA_BROKERS)
                .setTopics(KAFKA_TOPIC)
                .setGroupId(KAFKA_GROUP_ID)
                .setStartingOffsets(OffsetsInitializer.latest())
                .setValueOnlyDeserializer(new SimpleStringSchema())
                .build();

        DataStream<String> kafkaSource = env.fromSource(source, WatermarkStrategy.noWatermarks(), "Kafka Source");

        kafkaSource.map(TickerData::new)
                        .keyBy(TickerData::getCode)
                        .window(SlidingProcessingTimeWindows.of(Time.seconds(10), Time.seconds(5)))
                        .aggregate(new CountFunction(), new WindowResultProcessor())
                        .print();

        env.execute();
    }

    private static class CountFunction implements AggregateFunction<TickerData, Long, Long>{
        @Override
        public Long createAccumulator() {
            return 0L;
        }

        @Override
        public Long add(TickerData value, Long acc) {
            return acc + 1;
        }

        @Override
        public Long getResult(Long acc) {
            return acc;
        }

        @Override
        public Long merge(Long acc1, Long acc2) {
            return acc1 + acc2;
        }
    }

    private static class WindowResultProcessor extends ProcessWindowFunction<Long, AggResult, String, TimeWindow> {
        @Override
        public void process(String key,
                            Context context,
                            Iterable<Long> counts,
                            Collector<AggResult> out) throws Exception {
            Long count = counts.iterator().next();
            out.collect(new AggResult(key, count, context.window()));

        }
    }

    private static class AggResult{
        public String key;
        public Long aggResult;
        public TimeWindow window;

        public AggResult(String key, Long aggResult, TimeWindow window) {
            this.key = key;
            this.aggResult = aggResult;
            this.window = window;
        }

        @Override
        public String toString() {
            return "AggResult{" +
                    "key='" + key + '\'' +
                    ", aggResult=" + aggResult +
                    ", window=" + window +
                    '}';
        }
    }
}
