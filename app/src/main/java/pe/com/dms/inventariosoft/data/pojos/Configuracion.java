package pe.com.dms.inventariosoft.data.pojos;

import pe.com.dms.inventariosoft.BuildConfig;
import pe.com.dms.inventariosoft.data.models.Almacen;
import pe.com.dms.inventariosoft.utils.Constants;

public class Configuracion {
    private int modo;
    private Almacen almacen;
    private String ubicacion;
    private boolean lote;
    private boolean serie;
    private boolean registrar;
    private boolean batch;
    private boolean cameraScan;
    private boolean solicitarConfirmacion;
    private String servidor;
    private boolean rfd;
    private int sizeNumber;
    private boolean numberDecimal;
    private boolean calculadora;

    public Configuracion() {
        this.modo = Constants.MODE_MANUAL;
        this.almacen = null;
        this.ubicacion = "";
        this.lote = false;
        this.serie = false;
        this.registrar = false;
        this.batch = false;
        this.cameraScan = false;
        this.solicitarConfirmacion = false;
        setServidor(BuildConfig.BASE_URL_IP);
        this.rfd = false;
        this.sizeNumber = 4;
        this.numberDecimal = false;
        this.calculadora = false;
    }

    public void setServidor(String servidor) {
        servidor = servidor.trim();
        int len = servidor.length();
        String lastChar = servidor.substring(len - 1);
        if (lastChar.equals("/")) servidor = servidor.substring(0, len - 1);
        this.servidor = servidor;
    }

    public int getModo() {
        return modo;
    }

    public void setModo(int modo) {
        this.modo = modo;
    }

    public Almacen getAlmacen() {
        return almacen;
    }

    public void setAlmacen(Almacen almacen) {
        this.almacen = almacen;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public boolean isLote() {
        return lote;
    }

    public void setLote(boolean lote) {
        this.lote = lote;
    }

    public boolean isSerie() {
        return serie;
    }

    public void setSerie(boolean serie) {
        this.serie = serie;
    }

    public boolean isRegistrar() {
        return registrar;
    }

    public void setRegistrar(boolean registrar) {
        this.registrar = registrar;
    }

    public boolean isBatch() {
        return batch;
    }

    public void setBatch(boolean batch) {
        this.batch = batch;
    }

    public boolean isCameraScan() {
        return cameraScan;
    }

    public void setCameraScan(boolean cameraScan) {
        this.cameraScan = cameraScan;
    }

    public boolean isSolicitarConfirmacion() {
        return solicitarConfirmacion;
    }

    public void setSolicitarConfirmacion(boolean solicitarConfirmacion) {
        this.solicitarConfirmacion = solicitarConfirmacion;
    }

    public String getServidor() {
        return servidor;
    }

    public boolean isRfd() {
        return rfd;
    }

    public void setRfd(boolean rfd) {
        this.rfd = rfd;
    }

    public int getSizeNumber() {
        return sizeNumber;
    }

    public void setSizeNumber(int sizeNumber) {
        this.sizeNumber = sizeNumber;
    }

    public boolean isNumberDecimal() {
        return numberDecimal;
    }

    public void setNumberDecimal(boolean numberDecimal) {
        this.numberDecimal = numberDecimal;
    }

    public boolean isCalculadora() {
        return calculadora;
    }

    public void setCalculadora(boolean calculadora) {
        this.calculadora = calculadora;
    }

    @Override
    public String toString() {
        return "Configuracion{" +
                "modo=" + modo +
                ", almacen=" + almacen +
                ", ubicacion='" + ubicacion + '\'' +
                ", lote=" + lote +
                ", serie=" + serie +
                ", registrar=" + registrar +
                ", batch=" + batch +
                ", cameraScan=" + cameraScan +
                ", solicitarConfirmacion=" + solicitarConfirmacion +
                ", servidor='" + servidor + '\'' +
                ", rfd=" + rfd +
                ", sizeNumber=" + sizeNumber +
                ", numberDecimal=" + numberDecimal +
                ", calculadora=" + calculadora +
                '}';
    }
}
