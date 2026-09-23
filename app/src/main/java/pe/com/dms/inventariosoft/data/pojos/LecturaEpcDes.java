package pe.com.dms.inventariosoft.data.pojos;

public class LecturaEpcDes {

    private int id;
    private int idInventario;
    private int nroConteo;
    private String epc;
    private String fecha;
    private int procesado;
    private String descripcion;

    public LecturaEpcDes() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdInventario() {
        return idInventario;
    }

    public void setIdInventario(int idInventario) {
        this.idInventario = idInventario;
    }

    public int getNroConteo() {
        return nroConteo;
    }

    public void setNroConteo(int nroConteo) {
        this.nroConteo = nroConteo;
    }

    public String getEpc() {
        return epc;
    }

    public void setEpc(String epc) {
        this.epc = epc;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public int getProcesado() {
        return procesado;
    }

    public void setProcesado(int procesado) {
        this.procesado = procesado;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
