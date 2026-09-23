package pe.com.dms.inventariosoft.data.source.local.dao;

import java.util.List;



public interface BaseDao<T> {

    long insert(T entity);

    long[] insertAll(List<T> entities);

    void deleteAll();

//    --------------
//    Custom Queries
//    --------------


}
