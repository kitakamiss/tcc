package com.hjrh.tcctransaction.serializer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.hjrh.tcctransaction.InvocationContext;
import com.hjrh.tcctransaction.Participant;
import com.hjrh.tcctransaction.Terminator;
import com.hjrh.tcctransaction.Transaction;
import com.hjrh.tcctransaction.api.TransactionStatus;
import com.hjrh.tcctransaction.api.TransactionXid;
import com.hjrh.tcctransaction.common.TransactionType;

/**
 * 
 * 功能描述：
 *
 * @author  吴俊明 
 *
 * <p>修改历史：(修改人，修改时间，修改原因/内容)</p>
 */
public class KryoTransactionSerializer implements ObjectSerializer<Transaction> {

    private static Kryo kryo = null;

    static {
        kryo = new Kryo();

        kryo.register(Transaction.class);
        kryo.register(TransactionXid.class);
        kryo.register(TransactionStatus.class);
        kryo.register(TransactionType.class);
        kryo.register(Participant.class);
        kryo.register(Terminator.class);
        kryo.register(InvocationContext.class);
    }


    @Override
    public byte[] serialize(Transaction transaction) {
        Output output = new Output(256, -1);
        kryo.writeObject(output, transaction);
        return output.toBytes();
    }

    @Override
    public Transaction deserialize(byte[] bytes) {
        Input input = new Input(bytes);
        Transaction transaction = kryo.readObject(input, Transaction.class);
        return transaction;
    }
}
