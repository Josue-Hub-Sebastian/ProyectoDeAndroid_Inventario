package pe.com.dms.inventariosoft.data.models;

import androidx.room.Entity;

import com.google.gson.annotations.SerializedName;


@Entity(primaryKeys = {"id"})
public class Ubicacion {

    @SerializedName("id")
    private int id;
    @SerializedName("id_almacen")
    private int idAlmacen;
    @SerializedName("codigo")
    private String codigo;
    @SerializedName("local")
    private int local;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdAlmacen() {
        return idAlmacen;
    }

    public void setIdAlmacen(int idAlmacen) {
        this.idAlmacen = idAlmacen;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public int getLocal() {
        return local;
    }

    public void setLocal(int local) {
        this.local = local;
    }
}
