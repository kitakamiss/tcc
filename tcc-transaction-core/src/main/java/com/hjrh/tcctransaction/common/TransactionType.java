package com.hjrh.tcctransaction.common;

/**
 * 
 * 功能描述：事务类型
 *
 * @author  吴俊明 
 *
 * <p>修改历史：(修改人，修改时间，修改原因/内容)</p>
 */
public enum TransactionType {

	/**
	 * 主事务:1.
	 */
    ROOT(1),
    
    /**
     * 分支事务:2.
     */
    BRANCH(2);

    int id;

    TransactionType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public  static TransactionType  valueOf(int id) {
        switch (id) {
            case 1:
                return ROOT;
            case 2:
                return BRANCH;
            default:
                return null;
        }
    }

}
