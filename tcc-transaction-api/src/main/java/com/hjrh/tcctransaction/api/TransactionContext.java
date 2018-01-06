package com.hjrh.tcctransaction.api;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
/**
 * 
 * 功能描述：事务上下文
 *
 * @author  吴俊明 
 *
 * <p>修改历史：(修改人，修改时间，修改原因/内容)</p>
 */
public class TransactionContext implements Serializable {

    private static final long serialVersionUID = -8199390103169700387L;
    
    private TransactionXid xid;

    private int status;

    /**
     * 附加属性.
     */
    private Map<String, String> attachments = new ConcurrentHashMap<String, String>();

    public TransactionContext() {

    }

    /**
     * 构建事务上下文对像.
     * @param xid
     * @param status
     */
    public TransactionContext(TransactionXid xid, int status) {
        this.xid = xid;
        this.status = status;
    }

    public void setXid(TransactionXid xid) {
        this.xid = xid;
    }

    public TransactionXid getXid() {
        return xid.clone();
    }

    public void setAttachments(Map<String, String> attachments) {
        this.attachments = attachments;
    }

    public Map<String, String> getAttachments() {
        return attachments;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }


}
