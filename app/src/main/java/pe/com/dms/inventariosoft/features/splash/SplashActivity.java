package pe.com.dms.inventariosoft.features.splash;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.permissionx.guolindev.PermissionX;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import pe.com.dms.inventariosoft.R;
import pe.com.dms.inventariosoft.features.login.LoginActivity;
import pe.com.dms.inventariosoft.features.main.MainActivity;
import pe.com.dms.inventariosoft.features.shared.BaseActivity;

public class SplashActivity extends BaseActivity implements SplashContract.View {
    private String TAG = SplashActivity.class.getSimpleName();

    @BindView(R.id.version)
    TextView version;

    @Inject
    SplashPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivityComponent().inject(this);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);
        presenter.attachView(this);
        checkPermissionReadState();

        try {
            version.setText(getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            version.setText("0.0");
        }
    }

    @Override
    public void goToLogin() {
        startActivity(LoginActivity.newInstance(getContext()));
        finish();
    }

    @Override
    public void goToMainActivity() {
        startActivity(MainActivity.newInstance(getContext()));
        finish();
    }

    @Override
    public void viewMessage(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setIcon(R.drawable.ic_help_outline)
                .setTitle("Atencion")
                .setMessage(message)
                .setPositiveButton("Cerrar", (dialog, which) -> {
                    dialog.dismiss();
                    finish();
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void checkPermissionReadState() {
        Log.i(TAG, "checkPermissionStorageCamera: ");
        PermissionX.init(this)
                .permissions(Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.ACCESS_WIFI_STATE)
                .onExplainRequestReason((scope, deniedList, beforeRequest) -> {
                    scope.showRequestReasonDialog(deniedList,
                            "Para un buen uso de la aplicación es necesario que habilite los permisos correspodientes",
                            "Aceptar",
                            "Cancelar");
                })

                .onForwardToSettings((scope, deniedList) -> {
                    scope.showForwardToSettingsDialog(deniedList,
                            "Para continuar con el uso de la aplicación es necesario que habilite los permisos de manera manual",
                            "Config. manual",
                            "Cancelar");
                })
                .request((allGranted, grantedList, deniedList) -> {
                    if (allGranted) {
                        presenter.setAndroidId(getAndroidId());
                    }
                });
    }

    @SuppressLint("MissingPermission")
    public String getAndroidId() {

        String androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        return androidId;
    }
}
