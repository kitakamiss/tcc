package com.hjrh.tcctransaction;

import org.apache.log4j.Logger;

import com.hjrh.tcctransaction.api.TransactionContext;
import com.hjrh.tcctransaction.api.TransactionStatus;
import com.hjrh.tcctransaction.api.TransactionXid;
import com.hjrh.tcctransaction.common.TransactionType;
import com.hjrh.tcctransaction.support.TransactionConfigurator;


/**
 * 
 * 功能描述：事务管理器.
 *
 * @author  吴俊明 
 *
 * <p>修改历史：(修改人，修改时间，修改原因/内容)</p>
 */
public class TransactionManager {

    static final Logger LOG = Logger.getLogger(TransactionManager.class.getSimpleName());

    /**
	 * 事务配置器
	 */
    private TransactionConfigurator transactionConfigurator;

    public TransactionManager(TransactionConfigurator transactionConfigurator) {
        this.transactionConfigurator = transactionConfigurator;
    }

    /**
     * 定义当前线程的事务局部变量.
     */
    private ThreadLocal<Transaction> threadLocalTransaction = new ThreadLocal<Transaction>();

    /**
     * 
     * 功能描述：事务开始（创建事务日志记录，并将该事务日志记录存入当前线程的事务局部变量中）
     *
     * @author  吴俊明
     * <p>创建日期 ：2017年7月4日 下午2:31:34</p>
     *
     *
     * <p>修改历史 ：(修改人，修改时间，修改原因/内容)</p>
     */
    public void begin() {
    	LOG.debug("==>begin()");
        Transaction transaction = new Transaction(TransactionType.ROOT); // 事务类型为ROOT:1
        LOG.debug("==>TransactionType:" + transaction.getTransactionType().toString() + ", Transaction Status:" + transaction.getStatus().toString());
        TransactionRepository transactionRepository = transactionConfigurator.getTransactionRepository();
        transactionRepository.create(transaction); // 创建事务记录,写入事务日志库
        threadLocalTransaction.set(transaction); // 将该事务日志记录存入当前线程的事务局部变量中
    }

    /**
     * 
     * 功能描述：基于全局事务ID扩展创建新的分支事务，并存于当前线程的事务局部变量中.
     *
     * @author  吴俊明
     * <p>创建日期 ：2017年7月4日 下午2:31:43</p>
     *
     * @param transactionContext
     *
     * <p>修改历史 ：(修改人，修改时间，修改原因/内容)</p>
     */
    public void propagationNewBegin(TransactionContext transactionContext) {

        Transaction transaction = new Transaction(transactionContext);
        LOG.debug("==>propagationNewBegin TransactionXid：" + TransactionXid.byteArrayToUUID(transaction.getXid().getGlobalTransactionId()).toString()
        		+ "|" + TransactionXid.byteArrayToUUID(transaction.getXid().getBranchQualifier()).toString());
        
        transactionConfigurator.getTransactionRepository().create(transaction);

        threadLocalTransaction.set(transaction);
    }

    
    /**
     * 
     * 功能描述：找出存在的事务并处理.
     *
     * @author  吴俊明
     * <p>创建日期 ：2017年7月4日 下午2:31:51</p>
     *
     * @param transactionContext
     * @throws NoExistedTransactionException
     *
     * <p>修改历史 ：(修改人，修改时间，修改原因/内容)</p>
     */
    public void propagationExistBegin(TransactionContext transactionContext) throws NoExistedTransactionException {
        TransactionRepository transactionRepository = transactionConfigurator.getTransactionRepository();
        Transaction transaction = transactionRepository.findByXid(transactionContext.getXid());

        if (transaction != null) {
        	
        	LOG.debug("==>propagationExistBegin TransactionXid：" + TransactionXid.byteArrayToUUID(transaction.getXid().getGlobalTransactionId()).toString()
            		+ "|" + TransactionXid.byteArrayToUUID(transaction.getXid().getBranchQualifier()).toString());
        	
            transaction.changeStatus(TransactionStatus.valueOf(transactionContext.getStatus()));
            threadLocalTransaction.set(transaction);
        } else {
            throw new NoExistedTransactionException();
        }
    }

    /**
     * 
     * 功能描述：提交.
     *
     * @author  吴俊明
     * <p>创建日期 ：2017年7月4日 下午2:31:59</p>
     *
     *
     * <p>修改历史 ：(修改人，修改时间，修改原因/内容)</p>
     */
    public void commit() {
    	LOG.debug("==>TransactionManager commit()");
        Transaction transaction = getCurrentTransaction();

        transaction.changeStatus(TransactionStatus.CONFIRMING);
        LOG.debug("==>update transaction status to CONFIRMING");
        transactionConfigurator.getTransactionRepository().update(transaction);

        try {
        	LOG.info("==>transaction begin commit()");
            transaction.commit();
            transactionConfigurator.getTransactionRepository().delete(transaction);
        } catch (Throwable commitException) {
            LOG.error("compensable transaction confirm failed.", commitException);
            throw new ConfirmingException(commitException);
        }
    }

    /**
     * 
     * 功能描述：获取当前事务.
     *
     * @author  吴俊明
     * <p>创建日期 ：2017年7月4日 下午2:32:09</p>
     *
     * @return
     *
     * <p>修改历史 ：(修改人，修改时间，修改原因/内容)</p>
     */
    public Transaction getCurrentTransaction() {
        return threadLocalTransaction.get();
    }
    /**
     * 
     * 功能描述：回滚事务.
     *
     * @author  吴俊明
     * <p>创建日期 ：2017年7月4日 下午2:32:16</p>
     *
     *
     * <p>修改历史 ：(修改人，修改时间，修改原因/内容)</p>
     */
    public void rollback() {

        Transaction transaction = getCurrentTransaction();
        transaction.changeStatus(TransactionStatus.CANCELLING);

        transactionConfigurator.getTransactionRepository().update(transaction);
        
        try {
        	LOG.info("==>transaction begin rollback()");
            transaction.rollback();
            transactionConfigurator.getTransactionRepository().delete(transaction);
        } catch (Throwable rollbackException) {
            LOG.error("compensable transaction rollback failed.", rollbackException);
            throw new CancellingException(rollbackException);
        }
    }
}
