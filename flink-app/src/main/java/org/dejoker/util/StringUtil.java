package org.dejoker.util;

import org.apache.flink.api.java.tuple.Tuple4;

public class StringUtil {

    public static String createKey(Tuple4<String, Double, Long, Long> t) {
        return t.f0 + ":" + t.f2 + ":" + t.f3;
    }
}
