package pe.com.dms.inventariosoft.data.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

@Entity
public class Lectura implements Parcelable {

    public static final Parcelable.Creator<Lectura> CREATOR = new Parcelable.Creator<Lectura>() {
        @Override
        public Lectura createFromParcel(Parcel source) {
            return new Lectura(source);
        }

        @Override
        public Lectura[] newArray(int size) {
            return new Lectura[size];
        }
    };
    @PrimaryKey(autoGenerate = true)
    @SerializedName("id")
    private int id;
    @SerializedName("id_inventario")
    private int idInventario;
    @SerializedName("lote")
    private String lote;
    @SerializedName("serie")
    private String serie;
    @SerializedName("cantidad")
    private double cantidad;
    @SerializedName(value = "codigo_producto", alternate = {"COD_PRODUCTO", "cod_producto"})
    private String codigoProducto;
    @SerializedName(value = "codigo_ubicacion", alternate = {"COD_UBICACION", "cod_ubicacion"})
    private String codigoUbicacion;
    @SerializedName(value = "codigo_almacen", alternate = {"COD_ALMACEN", "cod_almacen"})
    private String codigoAlmacen;
    @SerializedName(value = "codigo_usuario", alternate = {"COD_USUARIO", "COD_USUARIO_REGISTRO", "cod_usuario"})

    private String codigoUsuario;
    @SerializedName(value = "descripcion_producto", alternate = {"DSC_PRODUCTO", "desc_producto"})
    private String descProducto;
    @SerializedName("flag_nuevo_producto")
    private int flagNuevoProducto = 0;
    @SerializedName("local")
    private int local;
    @SerializedName("epc")
    private String epc = "";

    public Lectura() {

    }

    protected Lectura(Parcel in) {
        this.id = in.readInt();
        this.idInventario = in.readInt();
        this.lote = in.readString();
        this.serie = in.readString();
        this.cantidad = in.readDouble();
        this.codigoProducto = in.readString();
        this.codigoUbicacion = in.readString();
        this.codigoAlmacen = in.readString();
        this.codigoUsuario = in.readString();
        this.local = in.readInt();
        this.epc = in.readString();
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

    public String getLote() {
        return lote;
    }

    public void setLote(String lote) {
        this.lote = lote;
    }

    public String getSerie() {
        return serie;
    }

    public void setSerie(String serie) {
        this.serie = serie;
    }

    public double getCantidad() {
        return cantidad;
    }

    public void setCantidad(double cantidad) {
        this.cantidad = cantidad;
    }

    public String getCantidadString() {
        if (getCantidad() % 1 != 0) {
            return String.valueOf(getCantidad());
        } else {
            return String.valueOf((int) getCantidad());
        }
    }

    public String getCodigoProducto() {
        return codigoProducto; //checkpoint
    }

    public void setCodigoProducto(String codigoProducto) {
        this.codigoProducto = codigoProducto;
    }

    public String getCodigoUbicacion() {
        return codigoUbicacion;
    }

    public void setCodigoUbicacion(String codigoUbicacion) {
        this.codigoUbicacion = codigoUbicacion;
    }

    public String getCodigoAlmacen() {
        return codigoAlmacen;
    }

    public void setCodigoAlmacen(String codigoAlmacen) {
        this.codigoAlmacen = codigoAlmacen;
    }

    public String getCodigoUsuario() {
        return codigoUsuario;
    }

    public void setCodigoUsuario(String codigoUsuario) {
        this.codigoUsuario = codigoUsuario;
    }

    public int getLocal() {
        return local;
    }

    public void setLocal(int local) {
        this.local = local;
    }

    public String getDescProducto() {
        return descProducto;
    }

    public void setDescProducto(String descProducto) {
        this.descProducto = descProducto;
    }

    public int getFlagNuevoProducto() {
        return flagNuevoProducto;
    }

    public void setFlagNuevoProducto(int flagNuevoProducto) {
        this.flagNuevoProducto = flagNuevoProducto;
    }

    public String getEpc() {
        return epc;
    }

    public void setEpc(String epc) {
        this.epc = epc;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeInt(this.idInventario);
        dest.writeString(this.lote);
        dest.writeString(this.serie);
        dest.writeDouble(this.cantidad);
        dest.writeString(this.codigoProducto);
        dest.writeString(this.codigoUbicacion);
        dest.writeString(this.codigoAlmacen);
        dest.writeString(this.codigoUsuario);
        dest.writeInt(this.local);
        dest.writeString(this.epc);
    }

    @Override
    public String toString() {
        return "Lectura{" +
                "id=" + id +
                ", idInventario=" + idInventario +
                ", lote='" + lote + '\'' +
                ", serie='" + serie + '\'' +
                ", cantidad=" + cantidad +
                ", codigoProducto='" + codigoProducto + '\'' +
                ", codigoUbicacion='" + codigoUbicacion + '\'' +
                ", codigoAlmacen='" + codigoAlmacen + '\'' +
                ", codigoUsuario='" + codigoUsuario + '\'' +
                ", descProducto='" + descProducto + '\'' +
                ", flagNuevoProducto=" + flagNuevoProducto +
                ", local=" + local +
                ", epc='" + epc + '\'' +
                '}';
    }
}
