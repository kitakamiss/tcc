package com.hjrh.tcctransaction.spring.repository;


import org.springframework.jdbc.datasource.DataSourceUtils;

import com.hjrh.tcctransaction.repository.JdbcTransactionRepository;

import java.sql.Connection;

/**
 * 
 * 功能描述：SpringJdbc事务库
 *
 * @author  吴俊明 
 *
 * <p>修改历史：(修改人，修改时间，修改原因/内容)</p>
 */
public class SpringJdbcTransactionRepository extends JdbcTransactionRepository {

    protected Connection getConnection() {
        return DataSourceUtils.getConnection(this.getDataSource());
    }

    protected void releaseConnection(Connection con) {
        DataSourceUtils.releaseConnection(con, this.getDataSource());
    }
}
