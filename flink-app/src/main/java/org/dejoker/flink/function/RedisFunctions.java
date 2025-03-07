package org.dejoker.flink.function;

import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.tuple.Tuple3;

public class RedisFunctions {

    public static class RedisPushFunction implements MapFunction<Tuple3<String, Long, Double>, String> {
        @Override
        public String map(Tuple3<String, Long, Double> stringLongDoubleTuple3) throws Exception {


            return null;
        }
    }

}
