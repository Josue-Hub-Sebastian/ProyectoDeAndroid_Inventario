package pe.com.dms.inventariosoft.data.models;

public class ListImei {

    private String _comment;
    private String imei;

    public String get_comment() {
        return _comment;
    }

    public void set_comment(String _comment) {
        this._comment = _comment;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    @Override
    public String toString() {
        return "ListImei{" +
                "_comment='" + _comment + '\'' +
                ", imei='" + imei + '\'' +
                '}';
    }
}
