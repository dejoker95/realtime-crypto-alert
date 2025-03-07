package org.dejoker.flink;

import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.SlidingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.util.OutputTag;
import org.dejoker.dto.TickerData;
import org.dejoker.flink.function.AggFunctions.*;


import java.time.Duration;

public class FlinkApp {

    private final OutputTag<String> outputTag = new OutputTag<>("side-output"){};

    private final String KAFKA_TOPIC = "test";
    private final String KAFKA_GROUP_ID = "test-group";
    private final String KAFKA_BROKERS = "localhost:9092";

    public void run() throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(2);

        KafkaSource<String> source = KafkaSource.<String>builder()
                .setBootstrapServers(KAFKA_BROKERS)
                .setTopics(KAFKA_TOPIC)
                .setGroupId(KAFKA_GROUP_ID)
                .setStartingOffsets(OffsetsInitializer.latest())
                .setValueOnlyDeserializer(new SimpleStringSchema())
                .build();

        DataStream<String> kafkaSource = env.fromSource(source, WatermarkStrategy.noWatermarks(),  "Kafka Source");

//        DataStream<TickerData> tickerStream = kafkaSource.map(TickerData::new)
//                .assignTimestampsAndWatermarks(
//                WatermarkStrategy.<TickerData>noWatermarks()
//                        .withTimestampAssigner((event, ts) -> event.getTimestamp())
//        );

        SingleOutputStreamOperator<String> aggstream = kafkaSource.map(TickerData::new)
                .keyBy(TickerData::getCode)
                .window(TumblingProcessingTimeWindows.of(Time.seconds(10)))
                .aggregate(new SimpleAvgFunction(), new SimpleProcessFunction());

//        aggstream.getSideOutput(outputTag).print();

        aggstream.print();


        env.execute();
    }







}
