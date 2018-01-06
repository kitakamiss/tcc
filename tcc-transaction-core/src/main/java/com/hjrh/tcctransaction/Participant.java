package com.hjrh.tcctransaction;

import org.apache.log4j.Logger;

import com.hjrh.tcctransaction.api.TransactionXid;

import java.io.Serializable;

/**
 * 
 * 功能描述：事务参与者
 *
 * @author  吴俊明 
 *
 * <p>修改历史：(修改人，修改时间，修改原因/内容)</p>
 */
public class Participant implements Serializable {
	
	static final Logger LOG = Logger.getLogger(Participant.class.getSimpleName());

    private static final long serialVersionUID = 4127729421281425247L;
    
    private TransactionXid xid;

    private Terminator terminator;

    public Participant() {

    }

    public Participant(TransactionXid xid, Terminator terminator) {
        this.xid = xid;
        this.terminator = terminator;
    }

    /**
     * 
     * 功能描述：回滚参与者事务（在Transaction中被调用）
     *
     * @author  吴俊明
     * <p>创建日期 ：2017年7月4日 下午2:28:53</p>
     *
     *
     * <p>修改历史 ：(修改人，修改时间，修改原因/内容)</p>
     */
    public void rollback() {
    	LOG.debug("==>Participant.rollback()");
        terminator.rollback();
    }

    /**
     * 
     * 功能描述：提交参与者事务（在Transaction中被调用）.
     *
     * @author  吴俊明
     * <p>创建日期 ：2017年7月4日 下午2:29:01</p>
     *
     *
     * <p>修改历史 ：(修改人，修改时间，修改原因/内容)</p>
     */
    public void commit() {
    	LOG.debug("==>Participant.rollback()");
        terminator.commit();
    }

}
