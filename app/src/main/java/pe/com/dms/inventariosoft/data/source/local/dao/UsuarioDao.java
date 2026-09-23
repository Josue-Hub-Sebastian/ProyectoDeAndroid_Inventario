package pe.com.dms.inventariosoft.data.source.local.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import io.reactivex.Single;
import pe.com.dms.inventariosoft.data.models.Usuario;

@Dao
public interface UsuarioDao extends BaseDao<Usuario> {

    @Query("DELETE  FROM Usuario")
    void deleteAll();

    @Query("SELECT * FROM Usuario")
    Single<List<Usuario>> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Usuario entity);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long[] insertAll(List<Usuario> entities);

    @Update
    void update(Usuario entity);

    @Delete
    void delete(Usuario entity);

//    --------------
//    Custom Queries
//    --------------

    @Query("SELECT * FROM Usuario WHERE Lower(username) = :username and password = :encryptedPassword")
    Single<Usuario> loginUsuario(String username, String encryptedPassword);

    @Query("SELECT count(1) FROM Usuario")
    int getCount();

    @Query("SELECT * FROM Usuario WHERE username = :username and password = :password")
    Usuario getValidUser(String username, String password);


}
