package pe.com.dms.inventariosoft.data.source.remote;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import retrofit2.HttpException;

public class ApiError {

    @SerializedName("message")
    private String message;
    @SerializedName("exit_code")
    private int exitCode;
    @SerializedName("next_inv")
    private int nextInv;

    public static ApiError parse(HttpException httpExepction) {
        try {
            return new Gson().fromJson(httpExepction.response().errorBody().string(), ApiError.class);
        } catch (Exception e) {
            return new ApiError();
        }
    }

    public String getMessage() {
        return message;
    }

    public int getExitCode() {
        return exitCode;
    }

    public boolean isInventarioDone() {
        return exitCode == -1;
    }

    public int getNextInv() {
        return nextInv;
    }

    public void setNextInv(int nextInv) {
        this.nextInv = nextInv;
    }

    @Override
    public String toString() {
        return "ApiError{" +
                "message='" + message + '\'' +
                ", exitCode=" + exitCode +
                ", nextInv=" + nextInv +
                '}';
    }
}