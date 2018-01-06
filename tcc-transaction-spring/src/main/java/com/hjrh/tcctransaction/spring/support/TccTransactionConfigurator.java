package com.hjrh.tcctransaction.spring.support;

import org.springframework.beans.factory.annotation.Autowired;

import com.hjrh.tcctransaction.TransactionManager;
import com.hjrh.tcctransaction.TransactionRepository;
import com.hjrh.tcctransaction.recover.RecoverConfig;
import com.hjrh.tcctransaction.spring.recover.DefaultRecoverConfig;
import com.hjrh.tcctransaction.support.TransactionConfigurator;

/**
 * 
 * 功能描述：TCC事务配置器
 *
 * @author  吴俊明 
 *
 * <p>修改历史：(修改人，修改时间，修改原因/内容)</p>
 */
public class TccTransactionConfigurator implements TransactionConfigurator {

	/**
	 * 事务库
	 */
    @Autowired
    private TransactionRepository transactionRepository;

    /**
     * 事务恢复配置
     */
    @Autowired(required = false)
    private RecoverConfig recoverConfig = DefaultRecoverConfig.INSTANCE;

    /**
     * 根据事务配置器创建事务管理器.
     */
    private TransactionManager transactionManager = new TransactionManager(this);

    /**
     * 获取事务管理器.
     */
    @Override
    public TransactionManager getTransactionManager() {
        return transactionManager;
    }

    /**
     * 获取事务库.
     */
    @Override
    public TransactionRepository getTransactionRepository() {
        return transactionRepository;
    }

    /**
     * 获取事务恢复配置.
     */
    @Override
    public RecoverConfig getRecoverConfig() {
        return recoverConfig;
    }
}
