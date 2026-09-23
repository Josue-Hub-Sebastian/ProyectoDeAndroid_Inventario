package pe.com.dms.inventariosoft.utils;

public class Constants {

    public static final String PREF_USER = "PREF_USER";
    public static final String PREF_CONFIG = "PREF_CONFIG";

    public static final int MODE_MANUAL = 0;
    public static final int MODE_BARRIDO = 1;

    //  scanner urobo
    //  Scanner Urobo
    public static final String SCANNER_ACTION = "android.intent.ACTION_DECODE_DATA";
    public static final String SCANNER_STRING = "barcode_string";
    //  Honeywell
    public static final String SCANNER_STRING_HONEY = "data";
    public static final String SCANNER_STRING_ZEBRA = "com.symbol.datawedge.data_string";


    public static final String EXTRA_ONLY_IP = "EXTRA_ONLY_IP";
    public static final String EXTRA_COD_UBICACION = "EXTRA_COD_UBICACION";
    public static final String EXTRA_COD_PRODUCTO = "EXTRA_COD_PRODUCTO";
    public static final String EXTRA_LECTURE_LIST = "EXTRA_LECTURE_LIST";
    public static final String EXTRA_LECTURE = "EXTRA_LECTURE";
    public static final String EXTRA_CODE_TYPE = "EXTRA_CODE_TYPE";
    public static final String EXTRA_QUICK = "EXTRA_QUICK";
    public static final String EXTRA_GRAPHIC_TYPE = "EXTRA_GRAPHIC_TYPE";
    public static final String EXTRA_LIST_ALMACEN = "EXTRA_LIST_ALMACEN";
    public static final String EXTRA_LIST_ALMACEN_OBJ = "EXTRA_LIST_ALMACEN_OBJ";

    public static final int INTENT_ALMACEN = 1;
    public static final int INTENT_SCAN = 6;
    public static final int INTENT_SCAN_UBICACION = 10;
    public static final int INTENT_SCAN_PRODUCTO = 11;
    public static final int INTENT_SCAN_LOTE = 12;
    public static final int INTENT_SCAN_SERIE = 13;
    public static final int INTENT_SCAN_VALOR = 14;
    public static final int INTENT_LECTURA = 15;

    public static final String PREF_FLAG_FIRST = "PREF_FLAG_FIRST";
    public static final String PREF_FLAG_BD_SYNCED = "PREF_FLAG_BD_SYNCED";
    public static final String PREF_IS_REGISTERED = "PREF_IS_REGISTERED";


    //    App View Format
    public static final String DATE_FORMAT = "dd/MM/yyyy";
    public static final String TIME_FORMAT = "hh:mm a";
    public static final String DATETIME_FORMAT = "dd/MM/yyyy hh:mm a";
    public static final String TIMESTAMP = "yyyyMMddHHmmss";

    //    WS Format
    public static final String BD_DATE_FORMAT = "yyyy-MM-dd";
    public static final String BD_TIME_FORMAT = "HH:mm:ss";
    public static final String BD_DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

}
