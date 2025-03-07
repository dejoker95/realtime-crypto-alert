package org.dejoker.flink.function;

import org.apache.flink.api.common.functions.AggregateFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;
import org.apache.flink.util.OutputTag;
import org.dejoker.dto.TickerData;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class AggFunctions {

    public static class SimpleAvgFunction implements AggregateFunction<TickerData, Tuple2<Double, Double>, Double> {
        @Override
        public Tuple2<Double, Double> createAccumulator() {
            return new Tuple2<>(0.0, 0.0);
        }

        @Override
        public Tuple2<Double, Double> add(TickerData tickerData, Tuple2<Double, Double> acc) {
            return new Tuple2<>(acc.f0 + 1, acc.f1 + tickerData.getTradePrice());
        }


        @Override
        public Double getResult(Tuple2<Double, Double> acc) {
            return new BigDecimal(acc.f1 / acc.f0).setScale(3, RoundingMode.HALF_UP).doubleValue();
        }

        @Override
        public Tuple2<Double, Double> merge(Tuple2<Double, Double> acc1, Tuple2<Double, Double> acc2) {
            return new Tuple2<>(acc1.f0 + acc2.f0, acc1.f1 + acc2.f1);
        }
    }

    public static class AvgFunctionWithKey implements AggregateFunction<TickerData, Tuple3<String, Double, Double>, Tuple2<String, Double>> {
        @Override
        public Tuple3<String, Double, Double> createAccumulator() {
            return new Tuple3<>("", 0.0, 0.0);
        }

        @Override
        public Tuple3<String, Double, Double> add(TickerData tickerData, Tuple3<String, Double, Double> acc) {
            return new Tuple3<>(tickerData.getCode(), acc.f1 + 1, acc.f2 + tickerData.getTradePrice());
        }

        @Override
        public Tuple2<String, Double> getResult(Tuple3<String, Double, Double> acc) {
            return new Tuple2<>(acc.f0, new BigDecimal(acc.f2 / acc.f1).setScale(3, RoundingMode.HALF_UP).doubleValue());
        }

        @Override
        public Tuple3<String, Double, Double> merge(Tuple3<String, Double, Double> a, Tuple3<String, Double, Double> b) {
            return new Tuple3<>(a.f0, a.f1 + b.f1, a.f2 + b.f2);
        }
    }
//    ProcessWindowFunction<IN, OUT, KEY, W extends Window>
    public static class SimpleProcessFunction extends ProcessWindowFunction<Double, String, String, TimeWindow> {

    @Override
        public void process(String key, Context ctx, Iterable<Double> aggregations, Collector<String> out) throws Exception {
            Double agg = aggregations.iterator().next();
            long start_ts = ctx.window().getStart();
            long end_ts = ctx.window().getEnd();

            String result = key + " " + start_ts + " " + end_ts + " " + System.currentTimeMillis() + " " + agg;

            out.collect(result);

        }
    }




}
