package pe.com.dms.inventariosoft.data.source.local.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import io.reactivex.Single;
import pe.com.dms.inventariosoft.data.models.Ubicacion;



@Dao
public interface UbicacionDao extends BaseDao<Ubicacion> {

    @Query("DELETE  FROM Ubicacion")
    void deleteAll();

    @Query("SELECT * FROM Ubicacion")
    Single<List<Ubicacion>> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Ubicacion entity);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long[] insertAll(List<Ubicacion> entities);

    @Update
    void update(Ubicacion entity);

    @Delete
    void delete(Ubicacion entity);

//    --------------
//    Custom Queries
//    --------------

    @Query("SELECT * FROM Ubicacion WHERE id = :id")
    Single<Ubicacion> getById(int id);

    @Deprecated
    @Query("SELECT * FROM Ubicacion WHERE local = 1")
    Single<List<Ubicacion>> getAllLocal();

    @Query("SELECT * FROM Ubicacion WHERE codigo = :codUbicacion")
    Single<Ubicacion> getByCode(String codUbicacion);
}
