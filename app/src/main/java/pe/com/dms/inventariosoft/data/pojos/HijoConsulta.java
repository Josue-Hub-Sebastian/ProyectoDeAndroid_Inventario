package pe.com.dms.inventariosoft.data.pojos;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by cwramirezg on 23/11/2017.
 */



/**
 * Modificado por dms_josue el 22/07/2026
 * pd: en 2017 ni habia acabo la primaria... xd
 * */
public class HijoConsulta implements Parcelable {

    public static final Creator<HijoConsulta> CREATOR = new Creator<HijoConsulta>() {
        @Override
        public HijoConsulta createFromParcel(Parcel source) {
            return new HijoConsulta(source);
        }

        @Override
        public HijoConsulta[] newArray(int size) {
            return new HijoConsulta[size];
        }
    };
    private int id;
    private String codigoUbicacion;
    private String codigoProducto;
    private String codigoUsuario;
    private String descProducto;
    private String lote;
    private String serie;
    private double cantidad;
    private int position;

    public HijoConsulta(int id, String codigoUbicacion, String codigoProducto, String codigoUsuario,String descProducto, String lote, String serie, double cantidad, int position) {
        this.id = id;
        this.codigoUbicacion = codigoUbicacion;
        this.codigoProducto = codigoProducto;
        this.codigoUsuario = codigoUsuario;
        this.descProducto = descProducto;
        this.lote = lote;
        this.serie = serie;
        this.cantidad = cantidad;
        this.position = position;
    }
    
    protected HijoConsulta(Parcel in) {
        this.id = in.readInt();
        this.codigoUbicacion = in.readString();
        this.codigoProducto = in.readString();
        this.codigoUsuario = in.readString();
        this.descProducto = in.readString();
        this.lote = in.readString();
        this.serie = in.readString();
        this.cantidad = in.readDouble();
        this.position = in.readInt();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCodigoUbicacion() {
        return codigoUbicacion;
    }

    public void setCodigoUbicacion(String codigoUbicacion) {
        this.codigoUbicacion = codigoUbicacion;
    }

    public String getCodigoProducto() {
        return codigoProducto;
    }

    public void setCodigoProducto(String codigoProducto) {
        this.codigoProducto = codigoProducto;
    }

    public String getCodigoUsuario() {
        return codigoUsuario;
    }

    public void setCodigoUsuario(String codigoUsuario) {
        this.codigoUsuario = codigoUsuario;
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

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.codigoUbicacion);
        dest.writeString(this.codigoProducto);
        dest.writeString(this.codigoUsuario);
        dest.writeString(this.descProducto);
        dest.writeString(this.lote);
        dest.writeString(this.serie);
        dest.writeDouble(this.cantidad);
        dest.writeInt(this.position);
    }

    public String getDescProducto() {
        return descProducto;
    }

    public void setDescProducto(String descProducto) {
        this.descProducto = descProducto;
    }

    public String getCantidadString() {
        if (getCantidad() % 1 != 0) {
            return String.valueOf(getCantidad());
        } else {
            return String.valueOf((int) getCantidad());
        }
    }
}
