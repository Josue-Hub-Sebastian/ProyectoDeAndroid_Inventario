package pe.com.dms.inventariosoft.data.source.remote;

public class Urls {

    public static final String LOGIN = "login";
    public static final String LOGOUT = "logout";
    public static final String LIST_USUARIOS = "usuarios";
    public static final String LIST_ALMACENES = "almacenes";
    public static final String GET_INVENTARIO = "almacenes/inventario";
    public static final String LIST_PRODUCTOS = "productos";
    public static final String LIST_DETALLE_INVENTARIO = "detalle_inventario";
    public static final String LIST_UBICACIONES = "ubicaciones";
    public static final String NEW_UBICACION = "ubicaciones";
    public static final String LIST_LECTURAS = "lecturas";
    public static final String COUNT_LECTURAS = "lecturas/contar";
    public static final String LAST_LECTURAS = "lecturas/ultimas";
    public static final String NEW_LECTURA = "lecturas";
    public static final String NEW_LECTURA_RFID = "lecturas/RFID";
    public static final String NEW_EPC = "lecturas/EPC";
    public static final String DELETE_ALL_LECTURAS = "lecturas/delete";
    public static final String DELETE_LECTURA_TOTALIZADA = "lecturas/delete";
    public static final String LIST_FALTANTES = "reporte/faltantes";
    public static final String GET_RFD = "rfd";
    static final String PATH_ID = "ID";
    static final String EXTRA_PATH_ID = "/{" + PATH_ID + "}";
    public static final String DELETE_LECTURA = "lecturas" + EXTRA_PATH_ID + "/delete";
    public static final String CREAR_PRODUCTO = "productos";

    //nuevo_canela
    public static final String GET_ESTADO_INVENTARIO = "inventario/estado";
    public static final String RESPONDER_PARTICIPACION = "inventario/conteo/responder";
    public static final String LIST_PRODUCTOS_ASIGNADOS = "inventario/conteo/mis-productos";
    public static final String LIST_ESTADO_OPERADORES = "inventario/conteo/estado-operadores";
}
