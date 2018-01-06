package com.hjrh.tcctransaction.recover;

/**
 * 
 * 功能描述：事务恢复配置接口
 *
 * @author  吴俊明 
 *
 * <p>修改历史：(修改人，修改时间，修改原因/内容)</p>
 */
public interface RecoverConfig {

	/**
	 * 获取最大重试次数
	 * @return
	 */
    public int getMaxRetryCount();

    /**
     * 获取需要执行事务恢复的持续时间.
     * @return
     */
    public int getRecoverDuration();

    /**
     * 获取定时任务规则表达式.
     * @return
     */
    public String getCronExpression();
}
