package pe.com.dms.inventariosoft.data.source.local.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Single;
import pe.com.dms.inventariosoft.data.models.LecturaEpc;
import pe.com.dms.inventariosoft.data.pojos.LecturaEpcDes;
import pe.com.dms.inventariosoft.data.pojos.LecturaRFID;

@Dao
public interface LecturaEpcDao extends BaseDao<LecturaEpc> {

    @Query("DELETE  FROM LecturaEpc")
    void deleteAll();

    @Query("SELECT l.id,l.idInventario,l.nroConteo,l.epc,l.fecha,l.procesado " +
            "FROM LecturaEpc l inner join Producto p on substr(l.epc,1,10) = p.codigo " +
            "where procesado = 1")
    Single<List<LecturaEpc>> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(LecturaEpc entity);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long[] insertAll(List<LecturaEpc> entities);

    @Update
    void update(LecturaEpc entity);

    @Delete
    void delete(LecturaEpc entity);

    //    --------------
    //    Custom Queries
    //    --------------
    @Query("DELETE FROM LecturaEpc WHERE substr(epc,1,10) = :cod")
    void deleteByCod(String cod);

    @Query("DELETE FROM LecturaEpc WHERE epc = :epc")
    void deleteByEpc(String epc);

    @Query("DELETE FROM LecturaEpc WHERE idInventario = :inventarioId")
    int deleteAll(int inventarioId);

    @Query("select count(1) from LecturaEpc where idInventario = :inventarioId and nroConteo = :conteo and epc = :epc")
    int searchEPC(String epc, int inventarioId, int conteo);


    @Query("select id, substr(epc,1,10) as codigo,epc, count(1) as cantidad from LecturaEpc " +
            "where idInventario = :inventarioId and procesado = 0 and nroConteo = :conteo group by substr(epc,1,10)")
    Flowable<List<LecturaRFID>> searchEPC(int inventarioId, int conteo);

    @Query("select * from LecturaEpc where idInventario = :inventarioId and procesado = 0 and nroConteo = :conteo")
    Single<List<LecturaEpc>> searchEPCWS(int inventarioId, int conteo);

    @Query("select l.id,l.idInventario,l.nroConteo,l.epc,l.fecha,l.procesado,p.descripcion " +
            "from LecturaEpc l inner join Producto p on substr(l.epc,1,10) = p.codigo " +
            "where idInventario = :inventarioId and procesado = 0 and nroConteo = :conteo")
    Single<List<LecturaEpcDes>> searchEPCDes(int inventarioId, int conteo);

    @Query("update LecturaEpc set procesado = :procesado where idInventario = :inventarioId and nroConteo = :conteo")
    void updateEPC(int inventarioId, int procesado, int conteo);
}
