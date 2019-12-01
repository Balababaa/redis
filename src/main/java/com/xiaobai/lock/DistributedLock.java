package com.xiaobai.lock;

import com.xiaobai.util.RedisUtil;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.params.SetParams;

import java.util.Collections;
import java.util.UUID;

public class DistributedLock {
    private String key;

    private int MAX_LOCK_TIME = 100;

    private SetParams setParams = SetParams.setParams().nx().ex(MAX_LOCK_TIME);


    public DistributedLock() {
        key = UUID.randomUUID().toString();
    }

    /**
     * @param id
     * @param timeout
     * @return
     */
    public boolean lock(int id, long timeout) {
        Jedis jedis = RedisUtil.getJedis();
        long startTime = System.currentTimeMillis();
        for (; ; ) {
            if ("OK".equals(jedis.set(key, String.valueOf(id), setParams))) {
                jedis.close();
                return true;
            } else {
                if (System.currentTimeMillis() > startTime + timeout) {
                    return false;
                }
            }
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean unlock(int id) {
        Jedis jedis = RedisUtil.getJedis();
        String script = "if redis.call('GET',KEYS[1]) == ARGV[1] then return redis.call('DEL',KEYS[1]) else return 0 end";
        try {
            //Object result = jedis
            //        .eval(script, Collections.singletonList(key), Collections.singletonList(String.valueOf(id)));
            //if ("1".equals(result.toString())) {
            //    return true;
            //}
            //return false;
            int value = Integer.parseInt(jedis.get(key));
            if(value==id){
                jedis.del(key);
                return true;
            }else{
                return false;
            }
        } finally {
            jedis.close();
        }
    }
}
