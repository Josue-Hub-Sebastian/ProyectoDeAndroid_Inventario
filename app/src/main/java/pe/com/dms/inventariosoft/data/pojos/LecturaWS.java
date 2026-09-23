package pe.com.dms.inventariosoft.data.pojos;

public class LecturaWS {

    private String CodUbicacion;
    private String CodProducto;
    private String CodAlmacen;
    private String CodUsuario;
    private String Lote;
    private String Serie;
    private int Cantidad;
    private int flgProducto = 0;
    private String EPC;

    public LecturaWS() {
    }

    public String getCodUbicacion() {
        return CodUbicacion;
    }

    public void setCodUbicacion(String codUbicacion) {
        CodUbicacion = codUbicacion;
    }

    public String getCodProducto() {
        return CodProducto;
    }

    public void setCodProducto(String codProducto) {
        CodProducto = codProducto;
    }

    public String getCodAlmacen() {
        return CodAlmacen;
    }

    public void setCodAlmacen(String codAlmacen) {
        CodAlmacen = codAlmacen;
    }

    public String getCodUsuario() {
        return CodUsuario;
    }

    public void setCodUsuario(String codUsuario) {
        CodUsuario = codUsuario;
    }

    public String getLote() {
        return Lote;
    }

    public void setLote(String lote) {
        Lote = lote;
    }

    public String getSerie() {
        return Serie;
    }

    public void setSerie(String serie) {
        Serie = serie;
    }

    public int getCantidad() {
        return Cantidad;
    }

    public void setCantidad(int cantidad) {
        Cantidad = cantidad;
    }

    public int getFlgProducto() {
        return flgProducto;
    }

    public void setFlgProducto(int flgProducto) {
        this.flgProducto = flgProducto;
    }

    public String getEPC() {
        return EPC;
    }

    public void setEPC(String EPC) {
        this.EPC = EPC;
    }

    @Override
    public String toString() {
        return "LecturaWS{" +
                "CodUbicacion='" + CodUbicacion + '\'' +
                ", CodProducto='" + CodProducto + '\'' +
                ", CodAlmacen='" + CodAlmacen + '\'' +
                ", CodUsuario='" + CodUsuario + '\'' +
                ", Lote='" + Lote + '\'' +
                ", Serie='" + Serie + '\'' +
                ", Cantidad=" + Cantidad +
                ", flgProducto=" + flgProducto +
                ", EPC='" + EPC + '\'' +
                '}';
    }
}
