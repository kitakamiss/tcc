package com.hjrh.tcctransaction;

import org.apache.log4j.Logger;

import com.hjrh.tcctransaction.support.BeanFactoryAdapter;
import com.hjrh.tcctransaction.utils.StringUtils;

import java.io.Serializable;
import java.lang.reflect.Method;

/**
 * 
 * 功能描述：终结者
 *
 * @author  吴俊明 
 *
 * <p>修改历史：(修改人，修改时间，修改原因/内容)</p>
 */
public class Terminator implements Serializable {
	
	static final Logger LOG = Logger.getLogger(Terminator.class.getSimpleName());

    private static final long serialVersionUID = -164958655471605778L;
    
    /**
     * 确认调用的上下文.
     */
    private InvocationContext confirmInvocationContext;

    /**
     * 取消调用的上下文.
     */
    private InvocationContext cancelInvocationContext;

    public Terminator() {

    }

    /**
     * 
     * 构造函数：构建终结者对像
     *
     * @param confirmInvocationContext
     * @param cancelInvocationContext
     */
    public Terminator(InvocationContext confirmInvocationContext, InvocationContext cancelInvocationContext) {
        this.confirmInvocationContext = confirmInvocationContext;
        this.cancelInvocationContext = cancelInvocationContext;
    }

    /**
     * 
     * 功能描述：提交参与者事务（在Participant中调用）.
     *
     * @author  吴俊明
     * <p>创建日期 ：2017年7月4日 下午2:29:44</p>
     *
     *
     * <p>修改历史 ：(修改人，修改时间，修改原因/内容)</p>
     */
    public void commit() {
    	LOG.debug("==>Terminator commit invoke");
        invoke(confirmInvocationContext);
    }

    
    /**
     * 
     * 功能描述：回滚参与者事务（在Participant中调用）.
     *
     * @author  吴俊明
     * <p>创建日期 ：2017年7月4日 下午2:29:52</p>
     *
     *
     * <p>修改历史 ：(修改人，修改时间，修改原因/内容)</p>
     */
    public void rollback() {
    	LOG.debug("==>Terminator rollback invoke");
        invoke(cancelInvocationContext);
    }

    /**
     * 
     * 功能描述：根据调用上下文，获取目标方法并执行方法调用.
     *
     * @author  吴俊明
     * <p>创建日期 ：2017年7月4日 下午2:30:00</p>
     *
     * @param invocationContext
     * @return
     *
     * <p>修改历史 ：(修改人，修改时间，修改原因/内容)</p>
     */
    private Object invoke(InvocationContext invocationContext) {

        if (StringUtils.isNotEmpty(invocationContext.getMethodName())) {
        	
        	LOG.debug("==>Terminator invoke " + invocationContext.getTargetClass().getName() + "." + invocationContext.getMethodName());

            try {
                Object target = BeanFactoryAdapter.getBean(invocationContext.getTargetClass());

                if (target == null && !invocationContext.getTargetClass().isInterface()) {
                    target = invocationContext.getTargetClass().newInstance();
                }

                Method method = null;
                // 找到要调用的目标方法
                method = target.getClass().getMethod(invocationContext.getMethodName(), invocationContext.getParameterTypes());

                // 调用服务方法，被再次被TccTransactionContextAspect和ResourceCoordinatorInterceptor拦截，但因为事务状态已经不再是TRYING了，所以直接执行远程服务
                return method.invoke(target, invocationContext.getArgs()); // 调用服务方法

            } catch (Exception e) {
                throw new SystemException(e);
            }
        }
        return null;
    }
}
