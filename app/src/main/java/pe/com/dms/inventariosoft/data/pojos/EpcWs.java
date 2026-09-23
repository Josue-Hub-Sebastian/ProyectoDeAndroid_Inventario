package pe.com.dms.inventariosoft.data.pojos;


public class EpcWs {

    private String Id_Inventario;
    private String NroConteo;
    private String EPC;
    private String fecha;

    public EpcWs() {
    }

    public String getId_Inventario() {
        return Id_Inventario;
    }

    public void setId_Inventario(String id_Inventario) {
        Id_Inventario = id_Inventario;
    }

    public String getNroConteo() {
        return NroConteo;
    }

    public void setNroConteo(String nroConteo) {
        NroConteo = nroConteo;
    }

    public String getEPC() {
        return EPC;
    }

    public void setEPC(String EPC) {
        this.EPC = EPC;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }
}
