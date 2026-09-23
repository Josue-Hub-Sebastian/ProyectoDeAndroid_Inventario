package pe.com.dms.inventariosoft.data.source;

import android.util.Log;

import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Observable;
import pe.com.dms.inventariosoft.data.models.Almacen;
import pe.com.dms.inventariosoft.data.models.EstadoInventarioResponse;
import pe.com.dms.inventariosoft.data.models.Lectura;
import pe.com.dms.inventariosoft.data.models.LecturaEpc;
import pe.com.dms.inventariosoft.data.models.Producto;
import pe.com.dms.inventariosoft.data.models.ProductoAsignado;
import pe.com.dms.inventariosoft.data.models.Ubicacion;
import pe.com.dms.inventariosoft.data.models.Usuario;
import pe.com.dms.inventariosoft.data.pojos.EpcWs;
import pe.com.dms.inventariosoft.data.pojos.GroupConsulta;
import pe.com.dms.inventariosoft.data.pojos.HijoConsulta;
import pe.com.dms.inventariosoft.data.pojos.LecturaEpcDes;
import pe.com.dms.inventariosoft.data.pojos.LecturaLote;
import pe.com.dms.inventariosoft.data.pojos.LecturaProducto;
import pe.com.dms.inventariosoft.data.pojos.LecturaRFID;
import pe.com.dms.inventariosoft.data.pojos.LecturaWS;
import pe.com.dms.inventariosoft.data.pojos.MensajeResponse;
import pe.com.dms.inventariosoft.injection.annotations.ApplicationScope;

@ApplicationScope
    public class DataSourceRepository implements DataSource.Shared, DataSource.Remote, DataSource.Local {

    String TAG = DataSourceRepository.class.getSimpleName();

    private final DataSourceLocal local;
    private final DataSourceRemote remote;

    private boolean online;

    @Inject
    public DataSourceRepository(@NonNull DataSourceLocal local,
                                @NonNull DataSourceRemote remote) {
        this.local = local;
        this.remote = remote;
    }

    public void setOnlineMode(boolean online) {
        this.online = online;
    }

    private DataSource.Shared getSource() {
        return online ? remote : local;
    }

    @Override
    public Completable cleanLocalDatabase() {
        return local.cleanLocalDatabase();
    }

    @Override
    public Ubicacion findUbicacionByCode(String codUbicacion) {
        return local.findUbicacionByCode(codUbicacion);
    }

    @Override
    public Producto findProductoByCode(String codProducto) {
        return local.findProductoByCode(codProducto);
    }

    @Override
    public Producto findProductoByDesc(String descProducto) {
        return local.findProductoByDesc(descProducto);
    }


    @Override
    public Lectura findLecturaBySerie(int inventarioId, String serie) {
        return local.findLecturaBySerie(inventarioId, serie);
    }

    @Override
    public Observable<List<Usuario>> listAllUsuariosSync() {
        return remote.listAllUsuariosSync()
                .doOnNext(local::saveAllUsuarios);
    }

    @Override
    public Observable<List<Lectura>> listAllLecturasLocal() {
        return local.listAllLecturasLocal();
    }

    @Override
    public Observable<List<LecturaEpc>> listAllLecturasEPCLocal() {
        return local.listAllLecturasEPCLocal();
    }

    @Override
    public Observable<MensajeResponse> registerEPC(EpcWs epcWs) {
        return remote.registerEPC(epcWs);
    }

    @Override
    public Observable<MensajeResponse> crearProducto(HashMap<String, String> body) {
        return remote.crearProducto(body);
    }

    @Override
    public Observable<List<Ubicacion>> listAllUbicacionesLocal() {
        return local.listAllUbicacionesLocal();
    }

    @Override
    public Observable<Usuario> loginUsuario(String username, String password) {
        Log.d(TAG, "loginUsuario online: " + online);
        Log.d(TAG, "loginUsuario username: " + username + ", password: " + password);
        if (online) {
            return remote.loginUsuario(username, password);
        } else {
            return local.loginUsuario(username.toLowerCase(Locale.ROOT), password);
        }
    }

    @Override
    public Observable<MensajeResponse> logoutUsuario(Usuario usuario) {
        // Siempre usamos el modo remoto para el logout, incluso si la app está en modo batch,
        // para garantizar que el FLG_ONLINE cambie a 0 en el servidor.
        return remote.logoutUsuario(usuario);
    }

    @Override
    public Observable<List<Producto>> listAllProductosSync() {
        return remote.listAllProductosSync().doOnNext(local::saveAllProductos);
    }

    @Override
    public Observable<List<Almacen>> listAllAlmacenesSync() {
        return remote.listAllAlmacenesSync().doOnNext(local::saveAllAlmacenes);
    }

    @Override
    public Observable<List<Almacen>> listAlmacenesByUser(String username) {
        if (online) {
            return remote.listAlmacenesByUser(username).doOnNext(local::saveAllAlmacenes);
        } else {
            return local.listAlmacenesByUser(username);
        }
    }

        @Override
        public Observable<List<Ubicacion>> listAllUbicacionesSync() {
            return remote.listAllUbicacionesSync().doOnNext(local::saveAllUbicaciones);
        }

    @Override
    public Completable clearEPC() {
        return local.clearEPC();
    }
//    @Override
//    public Observable<MensajeResponse> registerUbicacion(Ubicacion ubicacion) {
//        if (online) {
//            return remote.registerUbicacion(ubicacion);
//        } else {
//            return local.registerUbicacion(ubicacion);
//        }
//    }

    @Override
    public Observable<MensajeResponse> contarLecturas(int inventarioId, String codUsuario) {
        if (online) {
            return remote.contarLecturas(inventarioId, codUsuario);
        } else {
            return local.contarLecturas(inventarioId, codUsuario);
        }
    }


    public Observable<List<Lectura>> syncLecturaAll(int inventarioId, String codUsuario) {
        if (online) {
            local.deleteAllLecturas();
            return remote.listLecturasWithFilters(inventarioId, codUsuario, "", "", "", "")
                    .flatMap(lecturas -> {
                        local.saveAllLecturas(lecturas);
                        return local.listLecturasWithFilters(inventarioId, codUsuario, "", "", "", "");
                    });
        } else {
            return Observable.empty();
        }
    }

    @Override
    public Observable<List<Lectura>> listLecturasWithFilters(int inventarioId, String codUsuario, String codUbicacion, String codProducto, String lote, String serie) {
        if (online) {
            return remote.listLecturasWithFilters(inventarioId, codUsuario, codUbicacion, codProducto, lote, serie)
                    .flatMap(lecturas -> {
                        local.saveAllLecturas(lecturas);
                        return local.listLecturasWithFilters(inventarioId, codUsuario, codUbicacion, codProducto, lote, serie);
                    });
        } else {
            return local.listLecturasWithFilters(inventarioId, codUsuario, codUbicacion, codProducto, lote, serie);
        }
    }

    @Override
    public Observable<List<LecturaProducto>> getLecturaProductoByUbicacion(int inventarioId, String codUsuario, String codUbicacion) {
        if (online) {
            return remote.listLecturasWithFilters(inventarioId, codUsuario, codUbicacion, "", "", "")
                    .flatMap(lecturas -> {
                        local.saveAllLecturas(lecturas);
                        return local.getLecturaProductoByUbicacion(inventarioId, codUsuario, codUbicacion);
                    });
        } else {
            return local.getLecturaProductoByUbicacion(inventarioId, codUsuario, codUbicacion);
        }
    }

    @Override
    public Observable<List<LecturaLote>> getLecturaLoteByProducto(int inventarioId, String codUbicacion, String codProducto) {
        return local.getLecturaLoteByProducto(inventarioId, codUbicacion, codProducto);
    }

    @Override
    public Observable<MensajeResponse> registerLectura(Lectura lectura) {
        if (online) {
            return remote.registerLectura(lectura);
        } else {
            return local.registerLectura(lectura);
        }
    }

    @Override
    public Completable deleteLecturaById(int id) {
        if (online) {
            return remote.deleteLecturaById(id)
                    .doOnComplete(() -> local.deleteLecturaByIdSync(id));
        } else {
            return local.deleteLecturaById(id);
        }
    }

    @Override
    public Completable deleteLecturaRFIDById(String epc) {
        return local.deleteLecturaRFIDById(epc);
    }

    @Override
    public Completable deleteAllLecturas(int inventarioId, String codUsuario) {
        if (online) {
            return remote.deleteAllLecturas(inventarioId, codUsuario)
                    .doOnComplete(() -> local.deleteAllLecturas(inventarioId, codUsuario));
        } else {
            return local.deleteAllLecturas(inventarioId, codUsuario);
        }
    }

    @Override
    public Completable deleteAllLecturasLocal(int inventarioId, String codUsuario) {
        return local.deleteAllLecturasLocal(inventarioId, codUsuario);
    }

    @Override
    public Observable<List<GroupConsulta>> getLecturaGroupConsultaUbicacion(String codProducto) {
        return local.getLecturaGroupConsultaUbicacion(codProducto);
    }

    @Override
    public Observable<List<HijoConsulta>> getLecturaGroupConsultaUbicacionHijo(String codigoProducto, String codigoUbicacion) {
        return local.getLecturaGroupConsultaUbicacionHijo(codigoProducto, codigoUbicacion);
    }

    @Override
    public Observable<List<GroupConsulta>> getLecturaGroupConsultaProducto(String codigoProducto) {
        return local.getLecturaGroupConsultaProducto(codigoProducto);
    }

    @Override
    public Observable<List<HijoConsulta>> getLecturaGroupConsultaProductoHijo(String codigoUbicacion, String codigoProducto) {
        return local.getLecturaGroupConsultaProductoHijo(codigoUbicacion, codigoProducto);
    }

    @Override
    public Observable<List<GroupConsulta>> getLecturaGroupConsultaLote(String lote) {
        return local.getLecturaGroupConsultaLote(lote);
    }

    @Override
    public Observable<List<HijoConsulta>> getLecturaGroupConsultaLoteHijo(String codigoProducto, String lote) {
        return local.getLecturaGroupConsultaLoteHijo(codigoProducto, lote);
    }

    @Override
    public Observable<List<Lectura>> getMissing(int idInventario, int conteo, String codigoUbicacion) {
        return remote.getMissing(idInventario, conteo, codigoUbicacion);
    }

    @Override
    public Observable<List<Lectura>> listLastLecturas(int inventarioId, String codUsuario, int cant) {
        if (online) {
            return remote.listLastLecturas(inventarioId, codUsuario, cant);
        } else {
            return local.listLastLecturas(inventarioId, codUsuario, cant);
        }
    }

    @Override
    public Observable<List<LecturaRFID>> listLastLecturasEPC(int inventarioId, int conteo) {
        return local.listLastLecturasEPC(inventarioId, conteo);
    }

    @Override
    public Observable<Almacen> getNextInventario(String username, int idInventario) {
        return remote.getNextInventario(username, idInventario);
    }

    @Override
    public Observable<Long> saveRFID(String epc, int idInventario, int conteo) {
        return local.saveRFID(epc, idInventario, conteo);
    }

    @Override
    public Observable<List<LecturaEpc>> searchEPCWS(int inventarioId, int conteo) {
        return local.searchEPCWS(inventarioId, conteo);
    }

    @Override
    public Observable<List<LecturaEpcDes>> searchEPCDes(int inventarioId, int conteo) {
        return local.searchEPCDes(inventarioId, conteo);
    }

    @Override
    public Observable<MensajeResponse> registerLecturaRFID(List<LecturaWS> lecturaRFIDList) {
        return remote.registerLecturaRFID(lecturaRFIDList);
    }

    @Override
    public Observable<String> updateEPC(int inventarioId, int cod, int procesado, int conteo) {
        return local.updateEPC(inventarioId, cod, procesado, conteo);
    }
//nuevo_canela
    @Override
    public Observable<EstadoInventarioResponse> getEstadoInventario(int inventarioId, String username) {
        return remote.getEstadoInventario(inventarioId, username);
    }

    @Override
    public Observable<MensajeResponse> responderParticipacion(int inventarioId, String username, boolean participa) {
        return remote.responderParticipacion(inventarioId, username, participa);
    }

    @Override
    public Observable<List<ProductoAsignado>> listProductosAsignados(int inventarioId, String username) {
        return remote.listProductosAsignados(inventarioId, username);
    }

    @Override
    public Observable<List<pe.com.dms.inventariosoft.data.models.OperadorEstado>> listEstadoOperadores(int inventarioId) {
        return remote.listEstadoOperadores(inventarioId);
    }

}
