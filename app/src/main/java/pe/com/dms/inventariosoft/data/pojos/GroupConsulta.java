package pe.com.dms.inventariosoft.data.pojos;

/**
 * Created by cwramirezg on 23/11/2017.
 */

public class GroupConsulta {
    private String tvProducto;
    private String tvtotal;

    public GroupConsulta(String tvProducto, String tvtotal) {
        this.tvProducto = tvProducto;
        this.tvtotal = tvtotal;
    }

    public String getTvProducto() {
        return tvProducto;
    }

    public void setTvProducto(String tvProducto) {
        this.tvProducto = tvProducto;
    }

    public String getTvtotal() {
        return tvtotal;
    }

    public void setTvtotal(String tvtotal) {
        this.tvtotal = tvtotal;
    }
}
