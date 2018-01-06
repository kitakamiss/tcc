package com.hjrh.tcctransaction.server.dao;

import java.util.List;

import com.hjrh.tcctransaction.server.vo.TransactionVo;

/**
 * 
 * 功能描述：
 *
 * @author  吴俊明 
 *
 * <p>修改历史：(修改人，修改时间，修改原因/内容)</p>
 */
public interface TransactionDao {

    public List<TransactionVo> findTransactions(String domain, Integer pageNum, int pageSize);

    public Integer countOfFindTransactions(String domain);

    public boolean resetRetryCount(String domain, byte[] globalTxId, byte[] branchQualifier);
}

