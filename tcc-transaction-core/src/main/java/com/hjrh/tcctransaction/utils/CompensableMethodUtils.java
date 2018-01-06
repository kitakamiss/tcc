package com.hjrh.tcctransaction.utils;

import com.hjrh.tcctransaction.api.TransactionContext;
import com.hjrh.tcctransaction.common.MethodType;

/**
 * 
 * 功能描述：可补偿方法工具类
 *
 * @author  吴俊明 
 *
 * <p>修改历史：(修改人，修改时间，修改原因/内容)</p>
 */
public class CompensableMethodUtils {

	/**
	 * 
	 * 功能描述：计算方法类型
	 *
	 * @author  吴俊明
	 * <p>创建日期 ：2017年7月4日 下午2:26:28</p>
	 *
	 * @param transactionContext
	 * @param isCompensable
	 * @return
	 *
	 * <p>修改历史 ：(修改人，修改时间，修改原因/内容)</p>
	 */
    public static MethodType calculateMethodType( TransactionContext transactionContext, boolean isCompensable) {

        if (transactionContext == null && isCompensable) {
        	// 没有事务上下文信息，并且方法有事务注解的，为可补偿事务根方法（也就是事务发起者）
            //isRootTransactionMethod
            return MethodType.ROOT;
        } else if (transactionContext == null && !isCompensable) {
        	// 没有事务上下文信息，并且方法没有事务注解的，为可补偿事务服务消费者（参考者）方法（一般为被调用的服务接口）
            //isSoaConsumer
            return MethodType.CONSUMER;
        } else if (transactionContext != null && isCompensable) {
        	// 有事务上下文信息，并且方法有事务注解的，为可补偿事务服务提供者方法（一般为被调用的服务接口的实现方法）
            //isSoaProvider
            return MethodType.PROVIDER;
        } else {
            return MethodType.NORMAL;
        }
    }

    /**
     * 
     * 功能描述：获取事务上下文参数的位置
     *
     * @author  吴俊明
     * <p>创建日期 ：2017年7月4日 下午2:26:36</p>
     *
     * @param parameterTypes
     * @return
     *
     * <p>修改历史 ：(修改人，修改时间，修改原因/内容)</p>
     */
    public static int getTransactionContextParamPosition(Class<?>[] parameterTypes) {

        int i = -1;

        for (i = 0; i < parameterTypes.length; i++) {
            if (parameterTypes[i].equals(com.hjrh.tcctransaction.api.TransactionContext.class)) {
                break;
            }
        }
        return i;
    }

    /**
     * 
     * 功能描述：从参数获取事务上下文
     *
     * @author  吴俊明
     * <p>创建日期 ：2017年7月4日 下午2:26:44</p>
     *
     * @param args
     * @return
     *
     * <p>修改历史 ：(修改人，修改时间，修改原因/内容)</p>
     */
    public static TransactionContext getTransactionContextFromArgs(Object[] args) {

        TransactionContext transactionContext = null;

        for (Object arg : args) {
            if (arg != null && com.hjrh.tcctransaction.api.TransactionContext.class.isAssignableFrom(arg.getClass())) {

                transactionContext = (com.hjrh.tcctransaction.api.TransactionContext) arg;
            }
        }

        return transactionContext;
    }
}
