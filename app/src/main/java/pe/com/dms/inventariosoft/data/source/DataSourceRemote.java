package pe.com.dms.inventariosoft.data.source;

import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Observable;
import pe.com.dms.inventariosoft.data.models.Almacen;
import pe.com.dms.inventariosoft.data.models.EstadoInventarioResponse;
import pe.com.dms.inventariosoft.data.models.Lectura;
import pe.com.dms.inventariosoft.data.models.Producto;
import pe.com.dms.inventariosoft.data.models.ProductoAsignado;
import pe.com.dms.inventariosoft.data.models.Ubicacion;
import pe.com.dms.inventariosoft.data.models.Usuario;
import pe.com.dms.inventariosoft.data.pojos.EpcWs;
import pe.com.dms.inventariosoft.data.pojos.LecturaWS;
import pe.com.dms.inventariosoft.data.pojos.LoginRequest;
import pe.com.dms.inventariosoft.data.pojos.MensajeResponse;
import pe.com.dms.inventariosoft.data.source.remote.WebServices;
import pe.com.dms.inventariosoft.injection.annotations.ApplicationScope;
import pe.com.dms.inventariosoft.utils.UtilMethods;

@ApplicationScope
public class DataSourceRemote implements DataSource.Shared, DataSource.Remote {

    private WebServices webServices;

    @Inject
    public DataSourceRemote(WebServices webServices) {
        this.webServices = webServices;
    }


    @Override
    public Observable<List<Usuario>> listAllUsuariosSync() {
        return webServices.listAllUsuarios();
    }

    @Override
    public Observable<Usuario> loginUsuario(String username, String password) {
        String encryptedPassword = UtilMethods.md5(password);
        LoginRequest body = new LoginRequest();
        body.setUsername(username);
        body.setPassword(encryptedPassword);
        return webServices.loginUsuario(body);
    }

    @Override
    public Observable<MensajeResponse> logoutUsuario(Usuario usuario) {
        return webServices.logoutUsuario(usuario);
    }

    @Override
    public Observable<List<Producto>> listAllProductosSync() {
        return webServices.listAllProductos();
    }

    @Override
    public Observable<List<Almacen>> listAllAlmacenesSync() {
        return webServices.listAllAlmacenes();
    }

    @Override
    public Observable<List<Almacen>> listAlmacenesByUser(String username) {
        return webServices.listAlmacenesByUser(username);
    }

    @Override
    public Observable<List<Ubicacion>> listAllUbicacionesSync() {
        return webServices.listAllUbicaciones();
    }

    @Override
    public Observable<MensajeResponse> contarLecturas(int inventarioId, String codUsuario) {
        return webServices.getLecturasCount(inventarioId, codUsuario);
    }

    @Override
    public Observable<List<Lectura>> listLecturasWithFilters(int inventarioId, String codUsuario, String codUbicacion, String codProducto, String lote, String serie) {
        return webServices.listLecturasWithFilters(inventarioId, codUsuario, codUbicacion, codProducto, lote, serie);
    }

    @Override
    public Observable<MensajeResponse> registerLectura(Lectura lectura) {
        return webServices.registerLectura(lectura);
    }

    @Override
    public Completable deleteLecturaById(int id) {
        return webServices.deleteLecturaById(id);
    }

    @Override
    public Completable deleteAllLecturas(int inventarioId, String codUsuario) {
        return webServices.deleteAllLecturas(inventarioId, codUsuario);
    }

    @Override
    public Observable<List<Lectura>> getMissing(int idInventario, int conteo, String codigoUbicacion) {
        return webServices.
                listFaltantesWithFilters(idInventario, conteo, codigoUbicacion);
    }

    @Override
    public Observable<List<Lectura>> listLastLecturas(int inventarioId, String codUsuario, int cant) {
        return webServices.listLastLecturas(inventarioId, codUsuario, cant);
    }

    @Override
    public Observable<Almacen> getNextInventario(String username, int idInventario) {
        return webServices.getInventario(username, idInventario);
    }

    @Override
    public Observable<MensajeResponse> registerLecturaRFID(List<LecturaWS> lecturaRFIDList) {
        return webServices.registerLecturaRFID(lecturaRFIDList);
    }

    @Override
    public Observable<MensajeResponse> registerEPC(EpcWs epcWs) {
        return webServices.registerEPC(epcWs);
    }

    @Override
    public Observable<MensajeResponse> crearProducto(HashMap<String, String> body) {
        return webServices.crearProducto(body);
    }

    //nuevo_canela
    @Override
    public Observable<EstadoInventarioResponse> getEstadoInventario(int inventarioId, String username) {
        return webServices.getEstadoInventario(inventarioId, username);
    }
    @Override
    public Observable<MensajeResponse> responderParticipacion(int inventarioId, String username, boolean participa) {
        return webServices.responderParticipacion(inventarioId, username, participa);
    }
    @Override
    public Observable<List<ProductoAsignado>> listProductosAsignados(int inventarioId, String username) {
        return webServices.listProductosAsignados(inventarioId, username);
    }
    @Override
    public Observable<List<pe.com.dms.inventariosoft.data.models.OperadorEstado>> listEstadoOperadores(int inventarioId) {
        return webServices.listEstadoOperadores(inventarioId);
    }
}
