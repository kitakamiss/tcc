package com.hjrh.tcctransaction.interceptor;

import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

import com.hjrh.tcctransaction.NoExistedTransactionException;
import com.hjrh.tcctransaction.OptimisticLockException;
import com.hjrh.tcctransaction.api.TransactionContext;
import com.hjrh.tcctransaction.api.TransactionStatus;
import com.hjrh.tcctransaction.common.MethodType;
import com.hjrh.tcctransaction.support.TransactionConfigurator;
import com.hjrh.tcctransaction.utils.CompensableMethodUtils;
import com.hjrh.tcctransaction.utils.ReflectionUtils;

import java.lang.reflect.Method;

/**
 * 
 * 功能描述：
 *
 * @author  吴俊明 
 *
 * <p>修改历史：(修改人，修改时间，修改原因/内容)</p>
 */
public class CompensableTransactionInterceptor {

    static final Logger logger = Logger.getLogger(CompensableTransactionInterceptor.class.getSimpleName());

    /**
     * 事务配置器
     */
    private TransactionConfigurator transactionConfigurator;

    /**
     * 设置事务配置器.
     * @param transactionConfigurator
     */
    public void setTransactionConfigurator(TransactionConfigurator transactionConfigurator) {
        this.transactionConfigurator = transactionConfigurator;
    }

    /**
     * 
     * 功能描述：拦截补偿方法
     *
     * @author  吴俊明
     * <p>创建日期 ：2017年7月4日 下午2:19:33</p>
     *
     * @param pjp
     * @return
     * @throws Throwable
     *
     * <p>修改历史 ：(修改人，修改时间，修改原因/内容)</p>
     */
    public Object interceptCompensableMethod(ProceedingJoinPoint pjp) throws Throwable {

    	// 从拦截方法的参数中获取事务上下文
        TransactionContext transactionContext = CompensableMethodUtils.getTransactionContextFromArgs(pjp.getArgs());
        
        // 计算可补偿事务方法类型
        MethodType methodType = CompensableMethodUtils.calculateMethodType(transactionContext, true);
        
        logger.debug("==>interceptCompensableMethod methodType:" + methodType.toString());

        switch (methodType) {
            case ROOT:
                return rootMethodProceed(pjp); // 主事务方法的处理
            case PROVIDER:
                return providerMethodProceed(pjp, transactionContext); // 服务提供者事务方法处理
            default:
                return pjp.proceed(); // 其他的方法都是直接执行
        }
    }

    /**
     * 
     * 功能描述：主事务方法的处理
     *
     * @author  吴俊明
     * <p>创建日期 ：2017年7月4日 下午2:19:43</p>
     *
     * @param pjp
     * @return
     * @throws Throwable
     *
     * <p>修改历史 ：(修改人，修改时间，修改原因/内容)</p>
     */
    private Object rootMethodProceed(ProceedingJoinPoint pjp) throws Throwable {
    	logger.debug("==>rootMethodProceed");

        transactionConfigurator.getTransactionManager().begin(); // 事务开始（创建事务日志记录，并在当前线程缓存该事务日志记录）

        Object returnValue = null; // 返回值
        try {
        	
        	logger.debug("==>rootMethodProceed try begin");
            returnValue = pjp.proceed();  // Try (开始执行被拦截的方法)
            logger.debug("==>rootMethodProceed try end");
            
        } catch (OptimisticLockException e) {
        	logger.warn("==>compensable transaction trying exception.", e);
            throw e; //do not rollback, waiting for recovery job
        } catch (Throwable tryingException) {
            logger.warn("compensable transaction trying failed.", tryingException);
            transactionConfigurator.getTransactionManager().rollback();
            throw tryingException;
        }

        logger.info("===>rootMethodProceed begin commit()");
        transactionConfigurator.getTransactionManager().commit(); // Try检验正常后提交(事务管理器在控制提交)

        return returnValue;
    }
    /**
     * 
     * 功能描述：服务提供者事务方法处理
     *
     * @author  吴俊明
     * <p>创建日期 ：2017年7月4日 下午2:19:53</p>
     *
     * @param pjp
     * @param transactionContext
     * @return
     * @throws Throwable
     *
     * <p>修改历史 ：(修改人，修改时间，修改原因/内容)</p>
     */
    private Object providerMethodProceed(ProceedingJoinPoint pjp, TransactionContext transactionContext) throws Throwable {
    	
    	logger.debug("==>providerMethodProceed transactionStatus:" + TransactionStatus.valueOf(transactionContext.getStatus()).toString());

        switch (TransactionStatus.valueOf(transactionContext.getStatus())) {
            case TRYING:
            	logger.debug("==>providerMethodProceed try begin");
            	// 基于全局事务ID扩展创建新的分支事务，并存于当前线程的事务局部变量中.
                transactionConfigurator.getTransactionManager().propagationNewBegin(transactionContext);
                logger.debug("==>providerMethodProceed try end");
                return pjp.proceed();
            case CONFIRMING:
                try {
                	logger.debug("==>providerMethodProceed confirm begin");
                	// 找出存在的事务并处理.
                    transactionConfigurator.getTransactionManager().propagationExistBegin(transactionContext);
                    transactionConfigurator.getTransactionManager().commit(); // 提交
                    logger.debug("==>providerMethodProceed confirm end");
                } catch (NoExistedTransactionException excepton) {
                    //the transaction has been commit,ignore it.
                }
                break;
            case CANCELLING:
                try {
                	logger.debug("==>providerMethodProceed cancel begin");
                    transactionConfigurator.getTransactionManager().propagationExistBegin(transactionContext);
                    transactionConfigurator.getTransactionManager().rollback(); // 回滚
                    logger.debug("==>providerMethodProceed cancel end");
                } catch (NoExistedTransactionException exception) {
                    //the transaction has been rollback,ignore it.
                }
                break;
        }

        Method method = ((MethodSignature) (pjp.getSignature())).getMethod();

        return ReflectionUtils.getNullValue(method.getReturnType());
    }

}
