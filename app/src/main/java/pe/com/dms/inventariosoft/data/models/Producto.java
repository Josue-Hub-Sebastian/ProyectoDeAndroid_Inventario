package pe.com.dms.inventariosoft.data.models;

import androidx.room.Entity;
import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

@Entity(primaryKeys = {"codigo"})
public class Producto {

    @SerializedName("codigo")
    @NonNull
    private String codigo;
    @SerializedName("descripcion")
    private String descripcion;
    //CANELAZA
    // por cierto uso CANELAZA como identificador de las modificaciones que hago
    //raro pero efectivo
    @SerializedName(value = "unidadMedida", alternate = {"COD_UNIDAD", "codUnidad", "un", "UN", "unidad_medida"})
    private String unidadMedida;

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getUnidadMedida() {
        return unidadMedida;
    }

    public void setUnidadMedida(String unidadMedida) {
        this.unidadMedida = unidadMedida;
    }
}
