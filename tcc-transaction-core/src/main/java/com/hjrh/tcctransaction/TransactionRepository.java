package com.hjrh.tcctransaction;

import java.util.Date;
import java.util.List;

import com.hjrh.tcctransaction.api.TransactionXid;

/**
 * 
 * 功能描述：事务库接口.
 *
 * @author  吴俊明 
 *
 * <p>修改历史：(修改人，修改时间，修改原因/内容)</p>
 */
public interface TransactionRepository {

	/**
	 * 
	 * 功能描述：创建事务日志记录.
	 *
	 * @author  吴俊明
	 * <p>创建日期 ：2017年7月4日 下午2:32:40</p>
	 *
	 * @param transaction
	 * @return
	 *
	 * <p>修改历史 ：(修改人，修改时间，修改原因/内容)</p>
	 */
    int create(Transaction transaction);

    /**
     * 
     * 功能描述：更新事务日志记录.
     *
     * @author  吴俊明
     * <p>创建日期 ：2017年7月4日 下午2:32:50</p>
     *
     * @param transaction
     * @return
     *
     * <p>修改历史 ：(修改人，修改时间，修改原因/内容)</p>
     */
    int update(Transaction transaction);

    /**
     * 
     * 功能描述：删除事务日志记录.
     *
     * @author  吴俊明
     * <p>创建日期 ：2017年7月4日 下午2:33:00</p>
     *
     * @param transaction
     * @return
     *
     * <p>修改历史 ：(修改人，修改时间，修改原因/内容)</p>
     */
    int delete(Transaction transaction);

    /**
     * 
     * 功能描述：根据xid查找事务日志记录.
     *
     * @author  吴俊明
     * <p>创建日期 ：2017年7月4日 下午2:33:10</p>
     *
     * @param xid
     * @return
     *
     * <p>修改历史 ：(修改人，修改时间，修改原因/内容)</p>
     */
    Transaction findByXid(TransactionXid xid);

    /**
     * 
     * 功能描述：找出所有未处理事务日志（从某一时间点开始）.
     *
     * @author  吴俊明
     * <p>创建日期 ：2017年7月4日 下午2:33:18</p>
     *
     * @param date
     * @return
     *
     * <p>修改历史 ：(修改人，修改时间，修改原因/内容)</p>
     */
    List<Transaction> findAllUnmodifiedSince(Date date);
}
