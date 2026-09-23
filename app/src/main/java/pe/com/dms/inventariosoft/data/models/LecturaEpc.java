package pe.com.dms.inventariosoft.data.models;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

@Entity(indices = {@Index(value = {"epc"}, unique = true)})
public class LecturaEpc implements Parcelable {

    public static final Creator<LecturaEpc> CREATOR = new Creator<LecturaEpc>() {
        @Override
        public LecturaEpc createFromParcel(Parcel source) {
            return new LecturaEpc(source);
        }

        @Override
        public LecturaEpc[] newArray(int size) {
            return new LecturaEpc[size];
        }
    };
    @PrimaryKey(autoGenerate = true)
    @SerializedName("id")
    private int id;
    @SerializedName("id_inventario")
    private int idInventario;
    @SerializedName("nro_conteo")
    private int nroConteo;
    @SerializedName("epc")
    private String epc;
    @SerializedName("fecha")
    private String fecha;
    @SerializedName("procesado")
    private int procesado; // 0: sin procesar 1: procesado 2:enviado al ws(Web Service)

    public LecturaEpc() {

    }

    protected LecturaEpc(Parcel in) {
        this.id = in.readInt();
        this.idInventario = in.readInt();
        this.nroConteo = in.readInt();
        this.epc = in.readString();
        this.fecha = in.readString();
        this.procesado = in.readInt();
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeInt(this.idInventario);
        dest.writeInt(this.nroConteo);
        dest.writeString(this.epc);
        dest.writeString(this.fecha);
        dest.writeInt(this.procesado);
    }

    @Override
    public String toString() {
        return "LecturaEpc{" +
                "id=" + id +
                ", idInventario=" + idInventario +
                ", nroConteo=" + nroConteo +
                ", epc='" + epc + '\'' +
                ", fecha='" + fecha + '\'' +
                ", procesado=" + procesado +
                '}';
    }
}
