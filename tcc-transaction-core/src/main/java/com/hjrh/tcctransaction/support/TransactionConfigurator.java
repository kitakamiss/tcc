package com.hjrh.tcctransaction.support;

import com.hjrh.tcctransaction.TransactionManager;
import com.hjrh.tcctransaction.TransactionRepository;
import com.hjrh.tcctransaction.recover.RecoverConfig;

/**
 * 
 * 功能描述：事务配置器接口
 *
 * @author  吴俊明 
 *
 * <p>修改历史：(修改人，修改时间，修改原因/内容)</p>
 */
public interface TransactionConfigurator {

	/**
	 * 获取事务管理器.
	 * @return
	 */
    public TransactionManager getTransactionManager();

    /**
     * 获取事务库.
     * @return
     */
    public TransactionRepository getTransactionRepository();

    /**
     * 获取事务恢复配置.
     * @return
     */
    public RecoverConfig getRecoverConfig();

}
