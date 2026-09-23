package pe.com.dms.inventariosoft.features.splash;

import android.os.Build;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import pe.com.dms.inventariosoft.R;
import pe.com.dms.inventariosoft.data.PreferenceManager;
import pe.com.dms.inventariosoft.data.models.ListImei;
import pe.com.dms.inventariosoft.data.models.Usuario;
import pe.com.dms.inventariosoft.data.source.DataSourceRepository;
import pe.com.dms.inventariosoft.features.shared.BasePresenter;

class SplashPresenter extends BasePresenter<SplashContract.View>
        implements SplashContract.Presenter {
    private String TAG = SplashPresenter.class.getSimpleName();

    @Inject
    PreferenceManager preferenceManager;

    private String mAndroidId;
    private FirebaseRemoteConfig firebaseRemoteConfig;

    @Inject
    public SplashPresenter(PreferenceManager preferenceManager, DataSourceRepository dataSourceRepository) {
        this.preferenceManager = preferenceManager;
        dataSourceRepository.setOnlineMode(!preferenceManager.getConfig().isBatch());
        firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
    }

    @Override
    public void attachView(SplashContract.View mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
    }

    @Override
    public void onSplashDone() {
        Usuario user = preferenceManager.getUserInfo();
        if (user != null) {
            getView().goToMainActivity();
        } else {
            getView().goToLogin();
        }
    }

    @Override
    public void setAndroidId(String android) {
        mAndroidId = android;
        Log.d(TAG, "setImei imei: " + android + ", mImei: " + mAndroidId);
        Log.d(TAG, "setImei getIsRegistered(): " + preferenceManager.getIsRegistered());
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT_WATCH){
            //para versiones con android 4.3 o INFERIOR.
            new Handler().postDelayed(() -> {
                onSplashDone();
            }, 3000);
        } else{
            if (!preferenceManager.getIsRegistered()) {
                obtainConfigFirebase();
            } else {
                new Handler().postDelayed(() -> {
                    onSplashDone();
                }, 3000);
            }
        }

    }

    private void obtainConfigFirebase() {
        try {
            Log.d(TAG, "obtainConfigFirebase");
            firebaseRemoteConfig.setDefaultsAsync(R.xml.remote_config);
            FirebaseRemoteConfigSettings remoteConfigSettings = new FirebaseRemoteConfigSettings.Builder()
                    .setMinimumFetchIntervalInSeconds(0)
                    .build();
            firebaseRemoteConfig.setConfigSettingsAsync(remoteConfigSettings);
            fetchRemoteConfigValues(firebaseRemoteConfig);
        } catch (Error | Exception e) {
            e.printStackTrace();
        }
    }

    private void fetchRemoteConfigValues(FirebaseRemoteConfig remoteConfig) {
        Log.e(TAG, "fetchRemoteConfigValues remoteConfig: " + remoteConfig.toString());
        remoteConfig.fetchAndActivate()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        processRemoteConfig(remoteConfig);
                    } else {
                        getView().viewMessage("Ocurrio un problema al licenciar su dispositivo.\n" +
                                "Reinicie su dispositivo.\n" +
                                "Vuela a intentarlo");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "fetchRemoteConfigValues addOnFailureListener: " + e.getMessage());
                });
    }

    private void processRemoteConfig(FirebaseRemoteConfig remoteConfig) {
        Log.e(TAG, "processRemoteConfig remoteConfig: " + remoteConfig.toString());
        String listImei = remoteConfig.getString("listImei");
        Log.e(TAG, "processRemoteConfig listImei: " + listImei);
        if (TextUtils.isEmpty(listImei)) {
            obtainConfigFirebase();
        } else {
            ArrayList<ListImei> jsonArray = new Gson().fromJson(listImei, new TypeToken<List<ListImei>>() {
            }.getType());
            Log.e(TAG, "processRemoteConfig jsonArray: " + jsonArray);
            boolean isRegistered = isRegistered(jsonArray);
            Log.e(TAG, "processRemoteConfig isRegistered: " + isRegistered);
            if (isRegistered) {
                onSplashDone();
            } else {
                getView().viewMessage("Su dispositivo no se encuenta habilitado para el uso de este aplicativo.\n" +
                        "Comuniquese con DMS para brindarle una solución e indique el sgte número " + mAndroidId + ", para poder validar su dispositivo.");
            }
        }
    }

    private boolean isRegistered(ArrayList<ListImei> jsonArray) {
        return true;
                /*
        if (jsonArray != null && jsonArray.size() > 0) {
            for (ListImei imei : jsonArray) {
                Log.e(TAG, "isRegistered imei: " + imei);
                if (!TextUtils.isEmpty(imei.getImei())) {
                    if (mAndroidId.equalsIgnoreCase(imei.getImei())) {
                        Log.e(TAG, "isRegistered registro exitoso del imei: " + imei.getImei());
                        preferenceManager.setIsRegistered(true);
                        return true;
                    }
                }
            }
        }
        return false;
    */
    }

}