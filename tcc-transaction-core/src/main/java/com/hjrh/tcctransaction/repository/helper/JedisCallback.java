package com.hjrh.tcctransaction.repository.helper;

import redis.clients.jedis.Jedis;

/**
 * 
 * 功能描述：
 *
 * @author  吴俊明 
 *
 * <p>修改历史：(修改人，修改时间，修改原因/内容)</p>
 */
public interface JedisCallback<T> {

    public T doInJedis(Jedis jedis);
}