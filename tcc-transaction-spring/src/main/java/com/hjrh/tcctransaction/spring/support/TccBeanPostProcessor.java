package com.hjrh.tcctransaction.spring.support;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import com.hjrh.tcctransaction.support.BeanFactory;
import com.hjrh.tcctransaction.support.BeanFactoryAdapter;
/**
 * 
 * 功能描述：TCC Bean后置处理程序
 *
 * @author  吴俊明 
 *
 * <p>修改历史：(修改人，修改时间，修改原因/内容)</p>
 */
@Component
public class TccBeanPostProcessor implements ApplicationListener<ContextRefreshedEvent> {

	/**
	 * Spring启动时加载.
	 */
    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        ApplicationContext applicationContext = contextRefreshedEvent.getApplicationContext();

        if (applicationContext.getParent() == null) {

            BeanFactoryAdapter.setBeanFactory(applicationContext.getBean(BeanFactory.class));
        }
    }
}
