package pe.com.dms.inventariosoft.data.source.remote;

import java.util.HashMap;
import java.util.List;

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
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface WebServices {

    @GET(Urls.LIST_USUARIOS)
    Observable<List<Usuario>> listAllUsuarios();

    @POST(Urls.LOGIN)
    Observable<Usuario> loginUsuario(@Body LoginRequest body);

    // aqui esta el logout en cualquier caso eliminarlo juntoa  lo demas y dejarlo como estaba esta anotado en el bloc de notas
    @POST(Urls.LOGOUT)
    Observable<MensajeResponse> logoutUsuario(@Body Usuario usuario);

    @GET(Urls.LIST_PRODUCTOS)
    Observable<List<Producto>> listAllProductos();

    @GET(Urls.LIST_ALMACENES)
    Observable<List<Almacen>> listAllAlmacenes();

    @GET(Urls.LIST_ALMACENES)
    Observable<List<Almacen>> listAlmacenesByUser(
            @Query("username") String username);

    @GET(Urls.LIST_UBICACIONES)
    Observable<List<Ubicacion>> listAllUbicaciones();

//    @POST(Urls.NEW_UBICACION)
//    Observable<MensajeResponse> registerUbicacion(@Body Ubicacion ubicacion);

    @GET(Urls.LIST_LECTURAS)
    Observable<List<Lectura>> listLecturasWithFilters(
            @Query("id_inventario") int inventarioId,
            @Query("cod_usuario") String codUsuario,
            @Query("cod_ubicacion") String codUbicacion,
            @Query("cod_producto") String codProducto,
            //  @Query("username") String username,
            @Query("lote") String lote,
            @Query("serie") String serie);

    @GET(Urls.LIST_FALTANTES)
    Observable<List<Lectura>> listFaltantesWithFilters(
            @Query("id_inventario") int inventarioId,
            @Query("conteo") int codUsuario,
            @Query("cod_ubicacion") String codUbicacion);

    @GET(Urls.COUNT_LECTURAS)
    Observable<MensajeResponse> getLecturasCount(
            @Query("id_inventario") int inventarioId,
            @Query("cod_usuario") String codUsuario);

    @POST(Urls.NEW_LECTURA)
    Observable<MensajeResponse> registerLectura(
            @Body Lectura lectura);

    @POST(Urls.NEW_LECTURA_RFID)
    Observable<MensajeResponse> registerLecturaRFID(
            @Body List<LecturaWS> lecturaWSList);

    @POST(Urls.NEW_EPC)
    Observable<MensajeResponse> registerEPC(
            @Body EpcWs epcWs);

    @POST(Urls.DELETE_LECTURA)
    Completable deleteLecturaById(@Path(Urls.PATH_ID) int id);

    @FormUrlEncoded
    @POST(Urls.DELETE_ALL_LECTURAS)
    Completable deleteAllLecturas(
            @Field("id_inventario") int inventarioId,
            @Field("codigo_usuario") String codUsuario);

    @GET(Urls.LAST_LECTURAS)
    Observable<List<Lectura>> listLastLecturas(
            @Query("id_inventario") int inventarioId,
            @Query("cod_usuario") String codUsuario,
            @Query("cant") int cant);

    @GET(Urls.GET_INVENTARIO)
    Observable<Almacen> getInventario(
            @Query("username") String username,
            @Query("id_inventario") int inventarioId);

    @POST(Urls.CREAR_PRODUCTO)
    Observable<MensajeResponse> crearProducto(@Body HashMap<String, String> body);

    //nuevo_canela
    @GET(Urls.GET_ESTADO_INVENTARIO)
    Observable<EstadoInventarioResponse> getEstadoInventario(
            @Query("id_inventario") int inventarioId,
            @Query("username") String username);

    @FormUrlEncoded
    @POST(Urls.RESPONDER_PARTICIPACION)
    Observable<MensajeResponse> responderParticipacion(
            @Field("id_inventario") int inventarioId,
            @Field("username") String username,
            @Field("participa") boolean participa);

    @GET(Urls.LIST_PRODUCTOS_ASIGNADOS)
    Observable<List<ProductoAsignado>> listProductosAsignados(
            @Query("id_inventario") int inventarioId,
            @Query("username") String username);

    @GET(Urls.LIST_ESTADO_OPERADORES)
    Observable<List<pe.com.dms.inventariosoft.data.models.OperadorEstado>> listEstadoOperadores(
            @Query("id_inventario") int inventarioId);

}
