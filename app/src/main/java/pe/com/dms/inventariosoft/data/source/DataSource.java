package pe.com.dms.inventariosoft.data.source;


import java.util.HashMap;
import java.util.List;

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

public interface DataSource {

    interface Shared {

        Observable<Usuario> loginUsuario(String username, String password);

        Observable<MensajeResponse> logoutUsuario(Usuario usuario);

        Observable<List<Almacen>> listAlmacenesByUser(String username);

        Observable<MensajeResponse> contarLecturas(int inventarioId, String codUsuario);

        Completable deleteLecturaById(int id);

        Completable deleteAllLecturas(int inventarioId, String codUsuario);

        Observable<MensajeResponse> registerLectura(Lectura lectura);

        //        NOT SURE
        Observable<List<Lectura>> listLecturasWithFilters(
                int inventarioId,
                String codUsuario, String codUbicacion,
                String codProducto,
                String lote,
                String serie);

        Observable<List<Lectura>> listLastLecturas(int inventarioId, String codUsuario, int cant);

    }

    interface Remote {

        Observable<List<Usuario>> listAllUsuariosSync();

        Observable<List<Producto>> listAllProductosSync();

        Observable<List<Almacen>> listAllAlmacenesSync();

        Observable<List<Ubicacion>> listAllUbicacionesSync();

        Observable<List<Lectura>> getMissing(int idInventario, int conteo, String codigoUbicacion);

        Observable<Almacen> getNextInventario(String username, int idInventario);

        Observable<MensajeResponse> registerLecturaRFID(List<LecturaWS> lecturaRFIDList);

        Observable<MensajeResponse> registerEPC(EpcWs epcWs);

        Observable<MensajeResponse> crearProducto(HashMap<String, String> body);

        //nuevo_canela
        Observable<EstadoInventarioResponse> getEstadoInventario(int inventarioId, String username);
        Observable<MensajeResponse> responderParticipacion(int inventarioId, String username, boolean participa);
        Observable<List<ProductoAsignado>> listProductosAsignados(int inventarioId, String username);
        Observable<List<pe.com.dms.inventariosoft.data.models.OperadorEstado>> listEstadoOperadores(int inventarioId);
    }

    interface Local {

        Observable<List<Lectura>> listAllLecturasLocal();

        Observable<List<LecturaEpc>> listAllLecturasEPCLocal();

        Observable<List<Ubicacion>> listAllUbicacionesLocal();

        Completable cleanLocalDatabase();

        Ubicacion findUbicacionByCode(String codUbicacion);

        Producto findProductoByCode(String codProducto);
        Producto findProductoByDesc(String descProducto);

        Lectura findLecturaBySerie(int inventarioId, String serie);

        Observable<List<LecturaProducto>> getLecturaProductoByUbicacion(int inventarioId, String codUsuario, String codUbicacion);

        Observable<List<LecturaLote>> getLecturaLoteByProducto(int inventarioId, String codUbicacion, String codProducto);

        Observable<List<GroupConsulta>> getLecturaGroupConsultaUbicacion(String codProducto);

        Observable<List<HijoConsulta>> getLecturaGroupConsultaUbicacionHijo(String codigoProducto, String codigoUbicacion);

        Observable<List<GroupConsulta>> getLecturaGroupConsultaProducto(String codigoProducto);

        Observable<List<HijoConsulta>> getLecturaGroupConsultaProductoHijo(String codigoUbicacion, String codigoProducto);

        Observable<List<GroupConsulta>> getLecturaGroupConsultaLote(String lote);

        Observable<List<HijoConsulta>> getLecturaGroupConsultaLoteHijo(String codigoProducto, String lote);

        Observable<Long> saveRFID(String epc, int idInventario, int conteo);

        Observable<List<LecturaRFID>> listLastLecturasEPC(int inventarioId, int conteo);

        Observable<List<LecturaEpc>> searchEPCWS(int inventarioId, int conteo);

        Observable<List<LecturaEpcDes>> searchEPCDes(int inventarioId, int conteo);

        Observable<String> updateEPC(int inventarioId, int cod, int procesado, int conteo);

        Completable clearEPC();

        Completable deleteAllLecturasLocal(int inventarioId, String codUsuario);

        Completable deleteLecturaRFIDById(String epc);
    }

}