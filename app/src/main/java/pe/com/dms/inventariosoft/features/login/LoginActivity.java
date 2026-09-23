package pe.com.dms.inventariosoft.features.login;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.textfield.TextInputLayout;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SwitchCompat;

import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.analytics.FirebaseAnalytics;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pe.com.dms.inventariosoft.App;
import pe.com.dms.inventariosoft.BuildConfig;
import pe.com.dms.inventariosoft.R;
import pe.com.dms.inventariosoft.data.models.Usuario;
import pe.com.dms.inventariosoft.features.config.ip.ConfigIpActivity;
import pe.com.dms.inventariosoft.features.main.MainActivity;
import pe.com.dms.inventariosoft.features.shared.BaseActivity;
import pe.com.dms.inventariosoft.features.sync.SyncDialog;
import pe.com.dms.inventariosoft.utils.DecimalDigitsInputFilter;
import pe.com.dms.inventariosoft.utils.UtilMethods;
import pe.com.dms.inventariosoft.utils.dialogs.ProgressDialog;

public class LoginActivity extends BaseActivity implements LoginContract.View {

    @BindView(R.id.til_user)
    TextInputLayout tilUser;
    @BindView(R.id.til_password)
    TextInputLayout tilPassword;
    @BindView(R.id.tv_version_apk)
    TextView tvVersionApk;
    @BindView(R.id.sc_Modo)
    SwitchCompat scModo;

    @Inject
        LoginPresenter presenter;

    public static Intent newInstance(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivityComponent().inject(this);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        presenter.attachView(this);

        presenter.setupView();
        tvVersionApk.setText("v"+ BuildConfig.VERSION_NAME);
        Glide.with(getContext()).load(R.drawable.scanning).preload();
    }

    @OnClick({R.id.btn_config, R.id.bt_login})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_config:
                startActivityForResult(ConfigIpActivity.newInstance(getContext()), 1);
                break;
            case R.id.bt_login:
                if (tilUser.getEditText().getText().toString().isEmpty()) {
                    showLoginErrorDialog("El usuario no puede estar vacío");
                } else if (tilPassword.getEditText().getText().toString().isEmpty()) {
                    showLoginErrorDialog("La contraseña no puede estar vacía");
                } else {
                    if (presenter.getBatchMode()) {
                        ProgressDialog.show(getContext(), R.string.app_cargando);
                        presenter.onAttemptLogin(tilUser.getEditText().getText().toString(),
                                tilPassword.getEditText().getText().toString());
                    } else {
                        if (UtilMethods.isNetworkConnected(getContext())) {
                            ProgressDialog.show(getContext(), R.string.app_cargando);
                            presenter.onAttemptLogin(tilUser.getEditText().getText().toString(),
                                    tilPassword.getEditText().getText().toString());
                        } else {
                            showLoginErrorDialog(getString(R.string.sinInternet));
                        }
                    }
                }
                break;
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 1) {
                App.get(getContext()).buildDependecyInjection();
                buildComponent();
                getActivityComponent().inject(this);
                presenter.attachView(this);
            }
        }
    }

    @Override
    public void setupBatchModeSwitch(boolean batch) {
        scModo.setChecked(batch);
        scModo.setOnClickListener(view -> {
            new SyncDialog.Builder(getContext())
                    .setTheme(R.style.AppTheme_Dialog)
                    .setButtonlistener(success -> {
                        if (!success) {
                            scModo.setChecked(!scModo.isChecked());
                        }
                    })
                    .build().show();
        });
    }

    @Override
    public void showLoginErrorDialog(String message) {
        ProgressDialog.dismiss();
        UtilMethods.showToast(message);
    }

    @Override
    public void goToMainActivity() {
        ProgressDialog.dismiss();
        startActivity(MainActivity.newInstance(getContext()));

        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Bundle bundle = new Bundle();
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.LOGIN, bundle);

        finish();
    }

    @Override
    public void showSessionActiveDialog(Usuario usuario) {
        ProgressDialog.dismiss();
        new AlertDialog.Builder(this)
                .setTitle("Sesión Activa")
                .setMessage("El usuario " + usuario.getUsername() + " ya tiene una sesión iniciada. ¿Desea cerrar las otras sesiones para ingresar?")
                .setPositiveButton("Sí", (dialog, which) -> {
                    ProgressDialog.show(getContext(), R.string.app_cargando);
                    presenter.onConfirmCloseSession(usuario);
                })
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show();
    }
}
