package com.xiaobai.util;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.io.IOException;
import java.util.Properties;


/**
 * @Author: xiaobaiyouruhe
 * @Date: 2019/12/1 17:44
 */
public class RedisUtil {
    private RedisUtil() {}


    public static Jedis getJedis() {
        return Pool.jedisPool.getResource();
    }

    public static void closeJedis(Jedis jedis) {
        jedis.close();
    }


    private static class Pool {
        private static JedisPool jedisPool;

        static {
            Properties properties = new Properties();
            try {
                properties.load(Pool.class.getClassLoader().getResourceAsStream("redis.properties"));
                String host = String.valueOf(properties.getProperty("redis.host"));
                int port = Integer.parseInt(properties.getProperty("redis.port"));
                int maxTotal = Integer.parseInt(properties.getProperty("redis.max-total"));
                int maxIdle = Integer.parseInt(properties.getProperty("redis.max-idle"));
                int maxWait = Integer.parseInt(properties.getProperty("redis.max-wait"));
                boolean testOnBorrow = Boolean.parseBoolean(properties.getProperty("redis.test-on-borrow"));
                boolean testOnReturn = Boolean.parseBoolean(properties.getProperty("redis.test-on-return"));
                boolean testOnCreate = Boolean.parseBoolean(properties.getProperty("redis.test-on-create"));

                JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
                jedisPoolConfig.setMaxTotal(maxTotal);
                jedisPoolConfig.setMaxIdle(maxIdle);
                jedisPoolConfig.setMaxWaitMillis(maxWait);
                jedisPoolConfig.setTestOnBorrow(testOnBorrow);
                jedisPoolConfig.setTestOnReturn(testOnReturn);
                jedisPoolConfig.setTestOnCreate(testOnCreate);

                jedisPool = new JedisPool(jedisPoolConfig, host, port);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
