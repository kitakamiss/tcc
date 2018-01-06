package com.hjrh.tcctransaction.support;

/**
 * 
 * 功能描述：
 *
 * @author  吴俊明 
 *
 * <p>修改历史：(修改人，修改时间，修改原因/内容)</p>
 */
public class BeanFactoryAdapter {

    private static BeanFactory beanFactory;

    public static Object getBean(Class<?> aClass) {
        return beanFactory.getBean(aClass);
    }

    public static void setBeanFactory(BeanFactory beanFactory) {
        BeanFactoryAdapter.beanFactory = beanFactory;
    }
}
