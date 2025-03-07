package org.dejoker.util;

import io.lettuce.core.LettuceFutures;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisFuture;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.async.RedisAsyncCommands;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class RedisTest {
    public static void main(String[] args) {
        try (RedisClient client = RedisClient.create("redis://localhost:6379")) {
            RedisAsyncCommands<String, String> commands = client.connect().async();
            RedisFuture<String> setFuture = commands.set("test-key", "test-val");
            RedisFuture<String> getFuture = commands.get("test-key");


            boolean allFinished = LettuceFutures.awaitAll(5, TimeUnit.SECONDS, setFuture, getFuture);
            if (allFinished) {
                System.out.println(setFuture.get());
                System.out.println(getFuture.get());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
