package pe.com.dms.inventariosoft.utils.dialogs;

import android.content.Context;
import android.os.Build;

public class ProgressDialog {
    private static android.app.ProgressDialog progressDialog;

    public static void show(Context context, int messageResourceId) {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
            progressDialog = null;
        }
        int style;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            style = android.R.style.Theme_Material_Light_Dialog;
        } else {
            style = android.app.ProgressDialog.THEME_HOLO_LIGHT;
        }
        progressDialog = new android.app.ProgressDialog(context, style);
        progressDialog.setMessage(context.getResources().getString(messageResourceId));
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    public static void dismiss() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }
}
