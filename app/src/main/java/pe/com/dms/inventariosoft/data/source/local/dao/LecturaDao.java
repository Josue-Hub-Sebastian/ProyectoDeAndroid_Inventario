package pe.com.dms.inventariosoft.data.source.local.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import io.reactivex.Single;
import pe.com.dms.inventariosoft.data.models.Lectura;
import pe.com.dms.inventariosoft.data.pojos.GroupConsulta;
import pe.com.dms.inventariosoft.data.pojos.HijoConsulta;
import pe.com.dms.inventariosoft.data.pojos.LecturaLote;
import pe.com.dms.inventariosoft.data.pojos.LecturaProducto;

@Dao
public interface LecturaDao extends BaseDao<Lectura> {

    @Query("DELETE  FROM Lectura")
    void deleteAll();

    @Query("SELECT * FROM Lectura")
    Single<List<Lectura>> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Lectura entity);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long[] insertAll(List<Lectura> entities);

    @Update
    void update(Lectura entity);

    @Delete
    void delete(Lectura entity);

//    --------------
//    Custom Queries
//    --------------

    @Query("SELECT * FROM Lectura WHERE id = :id")
    Lectura getById(int id);

    @Query("SELECT * " +
            "FROM Lectura L " +
            "WHERE L.idInventario = :inventarioId AND " +
            "(:codUbicacion = '' OR L.codigoUbicacion = :codUbicacion) AND " +
            "(:codProducto = '' OR L.codigoProducto = :codProducto) AND " +
            "(:lote = '' OR L.lote = :lote) AND " +
            "(:serie = '' OR L.serie = :serie)")
    Single<List<Lectura>> filterBy(int inventarioId, String codUbicacion, String codProducto, String lote, String serie);

    @Query("DELETE FROM Lectura WHERE id = :id")
    int deleteById(int id);

    @Query("DELETE FROM Lectura WHERE idInventario = :inventarioId")
    int deleteAll(int inventarioId);

    @Query("SELECT L.codigoProducto, L.descProducto, SUM(L.cantidad) as cantidad " +
            "FROM Lectura L " +
            "WHERE L.idInventario = :inventarioId AND " +
            "   L.codigoUbicacion = :codUbicacion " +
            "GROUP BY L.codigoProducto, L.descProducto")
    Single<List<LecturaProducto>> getLecturaProductoByUbicacion(int inventarioId, String codUbicacion);

    @Query("SELECT L.lote,SUM(L.cantidad) as cantidad " +
            "FROM Lectura L " +
            "WHERE L.idInventario = :inventarioId AND " +
            "   L.codigoUbicacion = :codUbicacion AND " +
            "   L.codigoProducto = :codProducto " +
            "GROUP BY L.codigoProducto, L.lote")
    Single<List<LecturaLote>> getLecturaLoteByProducto(int inventarioId, String codUbicacion, String codProducto);

    @Query("SELECT codigoProducto as tvProducto, sum(cantidad) as tvtotal " +
            "FROM Lectura " +
            "WHERE codigoUbicacion = :codigoUbicacion " +
            "GROUP BY codigoProducto " +
            "order by MAX(id) desc")
    Single<List<GroupConsulta>> getLecturaGroupConsultaUbicacion(String codigoUbicacion);

    @Query("select MAX(id) as id, codigoUbicacion, codigoProducto, codigoUsuario as codigoUsuario, descProducto, MAX(lote) as lote, MAX(serie) as serie, SUM(cantidad) as cantidad, 0 as position " +
            "from Lectura " +
            "where codigoProducto = :codigoProducto " +
            "and codigoUbicacion = :codigoUbicacion " +
            "group by codigoUbicacion, codigoProducto, descProducto, codigoUsuario " +
            "order by MAX(id) desc")
    Single<List<HijoConsulta>> getLecturaGroupConsultaUbicacionHijo(String codigoProducto, String codigoUbicacion);

    @Query("SELECT codigoUbicacion as tvProducto, sum(cantidad) as tvtotal " +
            "FROM Lectura " +
            "WHERE codigoProducto = :codigoProducto " +
            "GROUP BY codigoUbicacion " +
            "order by MAX(id) desc")
    Single<List<GroupConsulta>> getLecturaGroupConsultaProducto(String codigoProducto);

    @Query("select MAX(id) as id, codigoUbicacion, codigoProducto, codigoUsuario as codigoUsuario, descProducto, MAX(lote) as lote, MAX(serie) as serie, SUM(cantidad) as cantidad, 0 as position " +
            "from Lectura " +
            "where codigoUbicacion = :codigoUbicacion " +
            "and codigoProducto = :codigoProducto " +
            "group by codigoUbicacion, codigoProducto, descProducto, codigoUsuario " +
            "order by MAX(id) desc")
    Single<List<HijoConsulta>> getLecturaGroupConsultaProductoHijo(String codigoUbicacion, String codigoProducto);

    @Query("SELECT codigoProducto as tvProducto, sum(cantidad) as tvtotal " +
            "FROM Lectura " +
            "WHERE lote = :lote " +
            "GROUP BY codigoProducto " +
            "order by MAX(id) desc")
    Single<List<GroupConsulta>> getLecturaGroupConsultaLote(String lote);

    @Query("select MAX(id) as id, codigoUbicacion, codigoProducto, codigoUsuario as codigoUsuario, descProducto, MAX(lote) as lote, MAX(serie) as serie, SUM(cantidad) as cantidad, 0 as position " +
            "from Lectura " +
            "where codigoProducto = :codigoProducto " +
            "and lote = :lote " +
            "group by codigoUbicacion, codigoProducto, descProducto, codigoUsuario " +
            "order by MAX(id) desc")
    Single<List<HijoConsulta>> getLecturaGroupConsultaLoteHijo(String codigoProducto, String lote);


    @Query("SELECT COUNT(1) " +
            "FROM Lectura L " +
            "WHERE L.codigoUsuario = :codUsuario " +
            "   AND L.idInventario = :inventarioId ")
    Single<Integer> getCount(int inventarioId, String codUsuario);

    @Query("SELECT * " +
            "FROM Lectura L " +
            "WHERE L.codigoUsuario = :codUsuario " +
            "   AND L.idInventario = :inventarioId " +
            "ORDER BY L.id DESC " +
            "LIMIT :cant")
    Single<List<Lectura>> listLastLecturas(int inventarioId, String codUsuario, int cant);

    @Query("SELECT * " +
            "FROM Lectura L " +
            "WHERE L.serie = :serie " +
            "AND L.idInventario = :inventarioId ")
    Single<Lectura> findLecturaBySerie(int inventarioId, String serie);
}
