package pe.com.dms.inventariosoft.data.source.local.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import io.reactivex.Single;
import pe.com.dms.inventariosoft.data.models.Almacen;



@Dao
public interface AlmacenDao extends BaseDao<Almacen> {

    @Query("DELETE  FROM Almacen")
    void deleteAll();

    @Query("SELECT * FROM Almacen")
    Single<List<Almacen>> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Almacen entity);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long[] insertAll(List<Almacen> entities);

    @Update
    void update(Almacen entity);

    @Delete
    void delete(Almacen entity);

//    --------------
//    Custom Queries
//    --------------

    @Query("SELECT * FROM Almacen WHERE username = :username")
    Single<List<Almacen>> listByUser(String username);



}
