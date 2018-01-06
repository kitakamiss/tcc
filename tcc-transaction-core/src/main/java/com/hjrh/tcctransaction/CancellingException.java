package com.hjrh.tcctransaction;
/**
 * 
 * 功能描述：取消异常
 *
 * @author  吴俊明 
 *
 * <p>修改历史：(修改人，修改时间，修改原因/内容)</p>
 */
public class CancellingException extends RuntimeException {


    public CancellingException(Throwable cause) {
        super(cause);
    }
}
