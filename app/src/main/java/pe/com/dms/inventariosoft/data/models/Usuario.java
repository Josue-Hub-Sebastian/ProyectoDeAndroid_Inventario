package pe.com.dms.inventariosoft.data.models;

import androidx.room.Entity;

import com.google.gson.annotations.SerializedName;

@Entity(primaryKeys = {"id"})
public class Usuario {

    @SerializedName(value = "id", alternate = {"cod", "ID_USUARIO"})
    private int id;
    private String nombre;
    private String username;
    private String password;
    @SerializedName(value = "flgOnline", alternate = {"FLG_ONLINE", "flg_online"})
    private boolean flgOnline;

    public Usuario() {
        super();
        this.id = 0;
        this.nombre = "";
        this.username = "";
        this.password = "";
        this.flgOnline = false;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isFlgOnline() {
        return flgOnline;
    }

    public void setFlgOnline(boolean flgOnline) {
        this.flgOnline = flgOnline;
    }
}
