package pe.com.dms.inventariosoft.data.source.local.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import io.reactivex.Single;
import pe.com.dms.inventariosoft.data.models.Producto;



@Dao
public interface ProductoDao extends BaseDao<Producto> {

    @Query("DELETE  FROM Producto")
    void deleteAll();

    @Query("SELECT * FROM Producto")
    Single<List<Producto>> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Producto entity);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long[] insertAll(List<Producto> entities);

    @Update
    void update(Producto entity);

    @Delete
    void delete(Producto entity);

//    --------------
//    Custom Queries
//    --------------

    @Query("SELECT * FROM Producto WHERE codigo = :codProducto")
    Single<Producto> getByCode(String codProducto);

    @Query("SELECT * FROM Producto WHERE descripcion = :descProducto")
    Single<Producto> getByDesc(String descProducto);
}
