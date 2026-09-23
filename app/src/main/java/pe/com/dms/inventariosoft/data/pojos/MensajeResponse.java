package pe.com.dms.inventariosoft.data.pojos;

import com.google.gson.annotations.SerializedName;

public class MensajeResponse {

    @SerializedName("exit_code")
    private int cod;
    @SerializedName("next_inv")
    private int nextInv;
    @SerializedName("message")
    private String msg;

    public MensajeResponse(int cod, String msg) {
        this.cod = cod;
        this.msg = msg;
    }

    public int getCod() {
        return cod;
    }

    public void setCod(int cod) {
        this.cod = cod;
    }

    public int getNextInv() {
        return nextInv;
    }

    public void setNextInv(int nextInv) {
        this.nextInv = nextInv;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public String toString() {
        return "MensajeResponse{" +
                "cod=" + cod +
                ", nextInv=" + nextInv +
                ", msg='" + msg + '\'' +
                '}';
    }
}
