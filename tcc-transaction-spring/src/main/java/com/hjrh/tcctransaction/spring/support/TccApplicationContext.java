package com.hjrh.tcctransaction.spring.support;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import com.hjrh.tcctransaction.support.BeanFactory;
/**
 * 
 * 功能描述：TCC应用上下文
 *
 * @author  吴俊明 
 *
 * <p>修改历史：(修改人，修改时间，修改原因/内容)</p>
 */
@Component
public class TccApplicationContext implements BeanFactory, ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public Object getBean(Class<?> aClass) {
        return this.applicationContext.getBean(aClass);
    }
}
