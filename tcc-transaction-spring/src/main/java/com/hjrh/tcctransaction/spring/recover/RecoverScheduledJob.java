package com.hjrh.tcctransaction.spring.recover;

import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.MethodInvokingJobDetailFactoryBean;

import com.hjrh.tcctransaction.SystemException;
import com.hjrh.tcctransaction.recover.TransactionRecovery;
import com.hjrh.tcctransaction.support.TransactionConfigurator;

/**
 * 
 * 功能描述：事务恢复定时任务
 *
 * @author  吴俊明 
 *
 * <p>修改历史：(修改人，修改时间，修改原因/内容)</p>
 */
public class RecoverScheduledJob {

	/**
	 * 事务恢复
	 */
    private TransactionRecovery transactionRecovery;

    /**
     * 注入的是TCC事务配置器.
     */
    private TransactionConfigurator transactionConfigurator;

    /**
     * 事务恢复任务调度器(这里注入的是org.springframework.scheduling.quartz.SchedulerFactoryBean实例)
     */
    private Scheduler scheduler;

    /**
     * 初始化方法，Spring启动时执行.
     */
    public void init() {

        try {
        	// MethodInvokingJobDetailFactoryBean 负责生成具体的任务，只需要指定某个对象的某个方法，在触发器触发时，即调用指定对象的指定方法。
            MethodInvokingJobDetailFactoryBean jobDetail = new MethodInvokingJobDetailFactoryBean();
            // 指定该任务对应的调用对象，这个对象所属的类无需实现任何接口
            jobDetail.setTargetObject(transactionRecovery);
            // 指定在targetObject对象中某个的方法(此处调用TransactionRecovery中的startRecover方法)
            jobDetail.setTargetMethod("startRecover");
            // 设置任务名称
            jobDetail.setName("transactionRecoveryJob");
            // 是否允许任务并发执行，类默认是并发执行的，这时候如果不设置“concurrent”为false，很可能带来并发或者死锁的问题，而且几率较小，不容易复现,
            // 设置为false表示等上一个任务执行完后再开启新的任务
            jobDetail.setConcurrent(false);
            jobDetail.afterPropertiesSet();

            // 触发器生成器类，用被指定的调度器调度生成指定规则的触发器对象
            // 该类负责在spring容器中创建一个触发器，该类的ID应该在SchedulerFactoryBean属性的List中被引用，这样这个触发器才能保证被某个指定调度器调度
            CronTriggerFactoryBean cronTrigger = new CronTriggerFactoryBean();
            // 设置触发器名称
            cronTrigger.setBeanName("transactionRecoveryCronTrigger");
            cronTrigger.setJobDetail((JobDetail) jobDetail.getObject());
            // 触发规则（这里通过事务配置器获取事务恢复定时任务规则）
            cronTrigger.setCronExpression(transactionConfigurator.getRecoverConfig().getCronExpression());
            cronTrigger.afterPropertiesSet();
            
            // 设置调度任务
            scheduler.scheduleJob((JobDetail) jobDetail.getObject(), cronTrigger.getObject());
            // 启动任务调度器
            scheduler.start();

        } catch (Exception e) {
            throw new SystemException(e);
        }
    }

    public void setTransactionRecovery(TransactionRecovery transactionRecovery) {
        this.transactionRecovery = transactionRecovery;
    }

    public void setTransactionConfigurator(TransactionConfigurator transactionConfigurator) {
        this.transactionConfigurator = transactionConfigurator;
    }

    public Scheduler getScheduler() {
        return scheduler;
    }

    public void setScheduler(Scheduler scheduler) {
        this.scheduler = scheduler;
    }
}
