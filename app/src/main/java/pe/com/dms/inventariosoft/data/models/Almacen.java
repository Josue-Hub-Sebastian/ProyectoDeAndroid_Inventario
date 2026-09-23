package pe.com.dms.inventariosoft.data.models;

import androidx.room.Entity;
import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;



@Entity(primaryKeys = {"id", "username"})
public class Almacen {

    @SerializedName("id")
    private int id;
    @SerializedName("codigo")
    private String codigo;
    @SerializedName("descripcion")
    private String descripcion;
    @SerializedName("conteo")
    private int conteo;
    @SerializedName("id_inventario")
    private int idInventario;
    @SerializedName("username")
    @NonNull
    private String username;
    @SerializedName("diferenciado")
    private int diferenciado;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

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

    public int getConteo() {
        return conteo;
    }

    public void setConteo(int conteo) {
        this.conteo = conteo;
    }

    public int getIdInventario() {
        return idInventario;
    }

    public void setIdInventario(int idInventario) {
        this.idInventario = idInventario;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getDiferenciado() {
        return diferenciado;
    }

    public void setDiferenciado(int diferenciado) {
        this.diferenciado = diferenciado;
    }
}
