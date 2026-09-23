package pe.com.dms.inventariosoft.data.source.local;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import pe.com.dms.inventariosoft.data.models.Almacen;
import pe.com.dms.inventariosoft.data.models.Lectura;
import pe.com.dms.inventariosoft.data.models.LecturaEpc;
import pe.com.dms.inventariosoft.data.models.Producto;
import pe.com.dms.inventariosoft.data.models.Ubicacion;
import pe.com.dms.inventariosoft.data.models.Usuario;
import pe.com.dms.inventariosoft.data.source.local.dao.AlmacenDao;
import pe.com.dms.inventariosoft.data.source.local.dao.LecturaDao;
import pe.com.dms.inventariosoft.data.source.local.dao.LecturaEpcDao;
import pe.com.dms.inventariosoft.data.source.local.dao.ProductoDao;
import pe.com.dms.inventariosoft.data.source.local.dao.UbicacionDao;
import pe.com.dms.inventariosoft.data.source.local.dao.UsuarioDao;

@Database(entities = {
        Usuario.class, Ubicacion.class, Almacen.class, Lectura.class, Producto.class, LecturaEpc.class
}, version = 2, exportSchema = false) // CANELAZA _ se cambio la version de la base de datos local ahora pasa a ser version 2 antes era 1
public abstract class AppDatabase extends RoomDatabase {

    public abstract UsuarioDao usuarioDao();

    public abstract UbicacionDao ubicacionDao();

    public abstract AlmacenDao almacenDao();

    public abstract LecturaDao lecturaDao();

    public abstract ProductoDao productoDao();

    public abstract LecturaEpcDao lecturaEpcDao();

}
