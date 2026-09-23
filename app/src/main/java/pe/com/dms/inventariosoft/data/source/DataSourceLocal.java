package pe.com.dms.inventariosoft.data.source;

import android.util.Log;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Observable;
import pe.com.dms.inventariosoft.data.models.Almacen;
import pe.com.dms.inventariosoft.data.models.Lectura;
import pe.com.dms.inventariosoft.data.models.LecturaEpc;
import pe.com.dms.inventariosoft.data.models.Producto;
import pe.com.dms.inventariosoft.data.models.Ubicacion;
import pe.com.dms.inventariosoft.data.models.Usuario;
import pe.com.dms.inventariosoft.data.pojos.GroupConsulta;
import pe.com.dms.inventariosoft.data.pojos.HijoConsulta;
import pe.com.dms.inventariosoft.data.pojos.LecturaEpcDes;
import pe.com.dms.inventariosoft.data.pojos.LecturaLote;
import pe.com.dms.inventariosoft.data.pojos.LecturaProducto;
import pe.com.dms.inventariosoft.data.pojos.LecturaRFID;
import pe.com.dms.inventariosoft.data.pojos.MensajeResponse;
import pe.com.dms.inventariosoft.data.source.local.AppDatabase;
import pe.com.dms.inventariosoft.injection.annotations.ApplicationScope;
import pe.com.dms.inventariosoft.utils.UtilMethods;

@ApplicationScope
public class DataSourceLocal implements DataSource.Shared, DataSource.Local {

    String TAG = DataSourceLocal.class.getSimpleName();

    private AppDatabase appDatabase;

    @Inject
    public DataSourceLocal(AppDatabase appDatabase) {
        this.appDatabase = appDatabase;
    }


    public void deleteLecturaByIdSync(int id) {
        appDatabase.lecturaDao().deleteById(id);
    }

    @Override
    public Ubicacion findUbicacionByCode(String codUbicacion) {
        Ubicacion obj = appDatabase.ubicacionDao().getByCode(codUbicacion)
                .onErrorReturnItem(new Ubicacion())
                .blockingGet();

        if (UtilMethods.isEmpty(obj.getCodigo())) return null;
        return obj;
    }

    @Override
    public Producto findProductoByCode(String codProducto) {
        Producto obj = appDatabase.productoDao().getByCode(codProducto)
                .onErrorReturnItem(new Producto())
                .blockingGet();

        if (UtilMethods.isEmpty(obj.getCodigo())) return null;
        return obj;
    }

    @Override
    public Producto findProductoByDesc(String descProducto) {
        Producto obj = appDatabase.productoDao().getByDesc((descProducto))
                .onErrorReturnItem(new Producto())
                .blockingGet();
        if(UtilMethods.isEmpty(obj.getDescripcion())) return null;
        return obj;
    }

    @Override
    public Lectura findLecturaBySerie(int inventarioId, String serie) {
        Lectura obj = appDatabase.lecturaDao().findLecturaBySerie(inventarioId, serie)
                .onErrorReturnItem(new Lectura())
                .blockingGet();

        if (UtilMethods.isEmpty(obj.getSerie())) return null;
        return obj;
    }

    @Override
    public Completable cleanLocalDatabase() {
        return Completable.fromAction(() -> {
            appDatabase.lecturaDao().deleteAll();
            appDatabase.ubicacionDao().deleteAll();
            appDatabase.almacenDao().deleteAll();
            appDatabase.productoDao().deleteAll();
            appDatabase.usuarioDao().deleteAll();
            appDatabase.lecturaEpcDao().deleteAll();
        });
    }

    void deleteAllLecturas() {
        appDatabase.lecturaDao().deleteAll();
    }

    @Override
    public Observable<List<Lectura>> listAllLecturasLocal() {
        return appDatabase.lecturaDao().getAll().toObservable();
    }

    @Override
    public Observable<List<LecturaEpc>> listAllLecturasEPCLocal() {
        return appDatabase.lecturaEpcDao().getAll().toObservable();
    }

    @Override
    public Observable<List<Ubicacion>> listAllUbicacionesLocal() {
        return appDatabase.ubicacionDao().getAllLocal().toObservable();
    }

    void saveAllUsuarios(List<Usuario> usuarios) {
        appDatabase.usuarioDao().insertAll(usuarios);
    }

    @Override
    public Observable<Usuario> loginUsuario(String username, String password) {
        Log.d(TAG, "loginUsuario username: " + username + ", password: " + password);
        String encryptedPassword = UtilMethods.md5(password);
        Log.d(TAG, "loginUsuario encryptedPassword: " + encryptedPassword);
        return appDatabase.usuarioDao().loginUsuario(username, encryptedPassword).toObservable();
    }

    @Override
    public Observable<MensajeResponse> logoutUsuario(Usuario usuario) {
        return Observable.just(new MensajeResponse(1, "Local logout success"));
    }

    void saveAllProductos(List<Producto> productos) {
        appDatabase.productoDao().insertAll(productos);
    }

    void saveAllAlmacenes(List<Almacen> almacenes) {
        appDatabase.almacenDao().insertAll(almacenes);
    }

    @Override
    public Observable<List<Almacen>> listAlmacenesByUser(String username) {
        return appDatabase.almacenDao().listByUser(username).toObservable();
    }

    void saveAllUbicaciones(List<Ubicacion> ubicaciones) {
        appDatabase.ubicacionDao().insertAll(ubicaciones);
    }

    @Override
    public Observable<MensajeResponse> contarLecturas(int inventarioId, String codUsuario) {
        return appDatabase.lecturaDao().getCount(inventarioId, codUsuario).toObservable()
                .map(cantidad -> new MensajeResponse(cantidad, ""));
    }

    public void saveAllLecturas(List<Lectura> lecturas) {
        appDatabase.lecturaDao().insertAll(lecturas);
    }

    @Override
    public Observable<List<Lectura>> listLecturasWithFilters(int inventarioId, String codUsuario, String codUbicacion, String codProducto, String lote, String serie) {
        return appDatabase.lecturaDao().filterBy(inventarioId, codUbicacion, codProducto, lote, serie).toObservable();
    }

    @Override
    public Observable<MensajeResponse> registerLectura(Lectura lectura) {
        return Observable.create(emitter -> {
            try {
                appDatabase.lecturaDao().insert(lectura);
                emitter.onNext(new MensajeResponse(1, lectura.getCodigoProducto()));
                emitter.onComplete();
            } catch (Exception e) {
                e.printStackTrace();
                emitter.onError(e);
            }
        });

    }
//findProductoByDesc
    @Override
    public Completable deleteLecturaById(int id) {
        return Completable.fromAction(() -> {
            Lectura lectura = appDatabase.lecturaDao().getById(id);
            if (lectura != null) {
                appDatabase.lecturaDao().deleteById(lectura.getId());
                appDatabase.lecturaEpcDao().deleteByEpc(lectura.getEpc());
            }
        });
    }

    @Override
    public Completable deleteAllLecturas(int inventarioId, String codUsuario) {
        return Completable.fromAction(() -> {
            appDatabase.lecturaDao().deleteAll(inventarioId);
            appDatabase.lecturaEpcDao().deleteAll(inventarioId);
        });
    }

    @Override
    public Completable deleteAllLecturasLocal(int inventarioId, String codUsuario) {
        return Completable.fromAction(() -> {
            appDatabase.lecturaDao().deleteAll(inventarioId);
            appDatabase.lecturaEpcDao().deleteAll(inventarioId);
        });
    }

    @Override
    public Observable<List<LecturaProducto>> getLecturaProductoByUbicacion(int inventarioId, String codUsuario, String codUbicacion) {
        return appDatabase.lecturaDao().getLecturaProductoByUbicacion(inventarioId, codUbicacion).toObservable();

    }

    @Override
    public Observable<List<LecturaLote>> getLecturaLoteByProducto(int inventarioId, String codUbicacion, String codProducto) {
        return appDatabase.lecturaDao().getLecturaLoteByProducto(inventarioId, codUbicacion, codProducto).toObservable();
    }

    @Override
    public Observable<List<GroupConsulta>> getLecturaGroupConsultaUbicacion(String codProducto) {
        return appDatabase.lecturaDao().getLecturaGroupConsultaUbicacion(codProducto).toObservable();
    }

    @Override
    public Observable<List<HijoConsulta>> getLecturaGroupConsultaUbicacionHijo(String codigoProducto, String codigoUbicacion) {
        return appDatabase.lecturaDao().getLecturaGroupConsultaUbicacionHijo(codigoProducto, codigoUbicacion).toObservable();
    }

    @Override
    public Observable<List<GroupConsulta>> getLecturaGroupConsultaProducto(String codigoProducto) {
        return appDatabase.lecturaDao().getLecturaGroupConsultaProducto(codigoProducto).toObservable();
    }

    @Override
    public Observable<List<HijoConsulta>> getLecturaGroupConsultaProductoHijo(String codigoUbicacion, String codigoProducto) {
        return appDatabase.lecturaDao().getLecturaGroupConsultaProductoHijo(codigoUbicacion, codigoProducto).toObservable();
    }

    @Override
    public Observable<List<GroupConsulta>> getLecturaGroupConsultaLote(String lote) {
        return appDatabase.lecturaDao().getLecturaGroupConsultaLote(lote).toObservable();
    }

    @Override
    public Observable<List<HijoConsulta>> getLecturaGroupConsultaLoteHijo(String codigoProducto, String lote) {
        return appDatabase.lecturaDao().getLecturaGroupConsultaLoteHijo(codigoProducto, lote).toObservable();
    }

    @Override
    public Observable<List<Lectura>> listLastLecturas(int inventarioId, String codUsuario, int cant) {
        return appDatabase.lecturaDao().listLastLecturas(inventarioId, codUsuario, cant).toObservable();
    }

    @Override
    public Observable<List<LecturaRFID>> listLastLecturasEPC(int inventarioId, int conteo) {
        return appDatabase.lecturaEpcDao().searchEPC(inventarioId, conteo).toObservable();
    }

    @Override
    public Observable<Long> saveRFID(String epc, int idInventario, int conteo) {
        return Observable.create(emitter -> {
            try {
                if (appDatabase.lecturaEpcDao().searchEPC(epc, idInventario, conteo) == 0) {
                    LecturaEpc lecturaEpc = new LecturaEpc();
                    lecturaEpc.setIdInventario(idInventario);
                    lecturaEpc.setNroConteo(conteo);
                    lecturaEpc.setEpc(epc);
                    lecturaEpc.setFecha(UtilMethods.getTimestapEPC());
                    lecturaEpc.setProcesado(0);
                    Long a = appDatabase.lecturaEpcDao().insert(lecturaEpc);
                    emitter.onNext(a);
                }
                emitter.onComplete();
            } catch (Exception e) {
                emitter.onError(new Throwable("Error al insertar en la temporal"));
            }
        });
    }

    @Override
    public Observable<List<LecturaEpc>> searchEPCWS(int inventarioId, int conteo) {
        return appDatabase.lecturaEpcDao().searchEPCWS(inventarioId, conteo).toObservable();
    }

    @Override
    public Observable<List<LecturaEpcDes>> searchEPCDes(int inventarioId, int conteo) {
        return appDatabase.lecturaEpcDao().searchEPCDes(inventarioId, conteo).toObservable();
    }

    @Override
    public Observable<String> updateEPC(int inventarioId, int cod, int procesado, int conteo) {
        return Observable.create(emitter -> {
            try {
                if (cod == 0) {
                    appDatabase.lecturaEpcDao().updateEPC(inventarioId, procesado, conteo);
                    emitter.onNext("0");
                    emitter.onComplete();
                } else {
                    emitter.onError(new Throwable("Error en el webservice"));
                }
            } catch (Exception e) {
                emitter.onError(new Throwable("No se actualizo las marcaciones"));
            }
        });
    }

    @Override
    public Completable clearEPC() {
        return Completable.create(emitter -> {
            try {
                appDatabase.lecturaEpcDao().deleteAll();
                emitter.onComplete();
            } catch (Exception e) {
                emitter.onError(new Throwable("No se borro la tabla EPC"));
            }
        });
    }

    @Override
    public Completable deleteLecturaRFIDById(String cod) {
        return Completable.fromAction(() ->
                appDatabase.lecturaEpcDao().deleteByCod(cod)
        );
    }
}
