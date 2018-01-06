package com.hjrh.tcctransaction.utils;

import java.util.Collection;

/**
 * 
 * 功能描述：
 *
 * @author  吴俊明 
 *
 * <p>修改历史：(修改人，修改时间，修改原因/内容)</p>
 */
public final class CollectionUtils {

    private CollectionUtils() {

    }

    public static boolean isEmpty(Collection collection) {
        return (collection == null || collection.isEmpty());
    }
}