package pe.com.dms.inventariosoft.data.pojos;

import androidx.room.ColumnInfo;

/**
 * Created by cwramirezg on 11/10/2017.
 */

public class LocationAndLabel {

    @ColumnInfo(name = "nombreHijo")
    private String ubicacion;

    private String etiqueta;

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    public void setEtiqueta(String etiqueta) {
        this.etiqueta = etiqueta;
    }
}
