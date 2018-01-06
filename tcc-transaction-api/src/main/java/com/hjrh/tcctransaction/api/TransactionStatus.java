package com.hjrh.tcctransaction.api;

/**
 * 
 * 功能描述：事务状态枚举
 *
 * @author  吴俊明 
 *
 * <p>修改历史：(修改人，修改时间，修改原因/内容)</p>
 */
public enum TransactionStatus {

	/**
	 * 尝试中:1.
	 */
    TRYING(1), 
    
    /**
     * 确认中:2.
     */
    CONFIRMING(2), 
    
    /**
     * 取消中:3.
     */
    CANCELLING(3);

    private int id;

     TransactionStatus(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static TransactionStatus valueOf(int id) {

        switch (id) {
            case 1:
                return TRYING;
            case 2:
                return CONFIRMING;
            default:
                return CANCELLING;
        }
    }

}
