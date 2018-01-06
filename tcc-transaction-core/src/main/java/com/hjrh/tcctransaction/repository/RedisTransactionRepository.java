package com.hjrh.tcctransaction.repository;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.transaction.xa.Xid;

import com.hjrh.tcctransaction.Transaction;
import com.hjrh.tcctransaction.common.TransactionType;
import com.hjrh.tcctransaction.repository.helper.JedisCallback;
import com.hjrh.tcctransaction.repository.helper.RedisHelper;
import com.hjrh.tcctransaction.repository.helper.TransactionSerializer;
import com.hjrh.tcctransaction.serializer.JdkSerializationSerializer;
import com.hjrh.tcctransaction.serializer.ObjectSerializer;
import com.hjrh.tcctransaction.utils.ByteUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;


/**
 * 
 * 功能描述：Redis缓存事务库
 *
 * @author  吴俊明 
 *
 * <p>修改历史：(修改人，修改时间，修改原因/内容)</p>
 */
public class RedisTransactionRepository extends CachableTransactionRepository {

    private JedisPool jedisPool;

    private String keyPrefix = "TCC:";

    public void setKeyPrefix(String keyPrefix) {
        this.keyPrefix = keyPrefix;
    }

    private ObjectSerializer serializer = new JdkSerializationSerializer();

    public void setSerializer(ObjectSerializer serializer) {
        this.serializer = serializer;
    }

    public JedisPool getJedisPool() {
        return jedisPool;
    }

    public void setJedisPool(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }

    @Override
    protected int doCreate(final Transaction transaction) {

        try {
            final byte[] key = RedisHelper.getRedisKey(keyPrefix, transaction.getXid());
            Long statusCode = RedisHelper.execute(jedisPool, new JedisCallback<Long>() {

                @Override
                public Long doInJedis(Jedis jedis) {
                    return jedis.hsetnx(key, ByteUtils.longToBytes(transaction.getVersion()), TransactionSerializer.serialize(serializer, transaction));
                }
            });

            return statusCode.intValue();
        } catch (Exception e) {
            throw new TransactionIOException(e);
        }
    }

    @Override
    protected int doUpdate(final Transaction transaction) {

        try {
            final byte[] key = RedisHelper.getRedisKey(keyPrefix, transaction.getXid());
            Long statusCode = RedisHelper.execute(jedisPool, new JedisCallback<Long>() {
                @Override
                public Long doInJedis(Jedis jedis) {
                    transaction.updateTime();
                    transaction.updateVersion();
                    return jedis.hsetnx(key, ByteUtils.longToBytes(transaction.getVersion()), TransactionSerializer.serialize(serializer, transaction));
                }
            });

            return statusCode.intValue();
        } catch (Exception e) {
            throw new TransactionIOException(e);
        }
    }

    @Override
    protected int doDelete(Transaction transaction) {
        try {
            final byte[] key = RedisHelper.getRedisKey(keyPrefix, transaction.getXid());
            Long result = RedisHelper.execute(jedisPool, new JedisCallback<Long>() {
                @Override
                public Long doInJedis(Jedis jedis) {
                    return jedis.del(key);
                }
            });
            return result.intValue();
        } catch (Exception e) {
            throw new TransactionIOException(e);
        }
    }

    @Override
    protected Transaction doFindOne(Xid xid) {

        try {

            final byte[] key = RedisHelper.getRedisKey(keyPrefix, xid);
            byte[] content = RedisHelper.getKeyValue(jedisPool, key);

            if (content != null) {
                return TransactionSerializer.deserialize(serializer, content);
            }
            return null;
        } catch (Exception e) {
            throw new TransactionIOException(e);
        }
    }

    @Override
    protected List<Transaction> doFindAllUnmodifiedSince(Date date) {

        List<Transaction> allTransactions = doFindAll();

        List<Transaction> allUnmodifiedSince = new ArrayList<Transaction>();

        for (Transaction transaction : allTransactions) {
            if (transaction.getTransactionType().equals(TransactionType.ROOT)
                    && transaction.getLastUpdateTime().compareTo(date) < 0) {
                allUnmodifiedSince.add(transaction);
            }
        }

        return allUnmodifiedSince;
    }

    //    @Override
    protected List<Transaction> doFindAll() {

        try {
            List<Transaction> transactions = new ArrayList<Transaction>();
            Set<byte[]> keys = RedisHelper.execute(jedisPool, new JedisCallback<Set<byte[]>>() {
                @Override
                public Set<byte[]> doInJedis(Jedis jedis) {
                    return jedis.keys((keyPrefix + "*").getBytes());
                }
            });

            for (final byte[] key : keys) {
                byte[] content = RedisHelper.getKeyValue(jedisPool, key);

                if (content != null) {
                    transactions.add(TransactionSerializer.deserialize(serializer, content));
                }
            }

            return transactions;
        } catch (Exception e) {
            throw new TransactionIOException(e);
        }
    }
}
