package pe.com.dms.inventariosoft.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.google.gson.Gson;

import javax.inject.Inject;

import pe.com.dms.inventariosoft.BuildConfig;
import pe.com.dms.inventariosoft.data.models.Usuario;
import pe.com.dms.inventariosoft.data.pojos.Configuracion;
import pe.com.dms.inventariosoft.injection.annotations.ApplicationScope;
import pe.com.dms.inventariosoft.utils.Constants;

@ApplicationScope
public class PreferenceManager {

    private final SharedPreferences mPreferences;

    @Inject
    public PreferenceManager(@ApplicationScope Context context) {
        mPreferences = context.getSharedPreferences(BuildConfig.PREF_NAME, Context.MODE_PRIVATE);
    }

    public String getBaseUrl() {
        return String.format("%s/api/v1/", getConfig().getServidor());
    }

    public void saveUser(Usuario user) {
        String userString = new Gson().toJson(user, Usuario.class);
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString(Constants.PREF_USER, userString);
        editor.apply();
    }

    public void removeUser() {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.remove(Constants.PREF_USER);
        editor.apply();
    }

    public void removeUserConfig() {
        Configuracion config = getConfig();
        config.setUbicacion("");
        config.setRegistrar(false);
        config.setModo(0);
        config.setLote(false);
        config.setSerie(false);
        config.setSolicitarConfirmacion(false);
        config.setAlmacen(null);
        saveConfig(config);
    }

    public void saveConfig(Configuracion configuracion) {
        String configString = new Gson().toJson(configuracion, Configuracion.class);
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString(Constants.PREF_CONFIG, configString);
        editor.apply();
    }

    public Configuracion getConfig() {
        String configString = mPreferences.getString(Constants.PREF_CONFIG, "");

        if (TextUtils.isEmpty(configString)) {
            Configuracion config = new Configuracion();
            saveConfig(config);
            return config;
        } else {
            return new Gson().fromJson(configString, Configuracion.class);
        }

    }

    public Usuario getUserInfo() {
        String userString = mPreferences.getString(Constants.PREF_USER, "");
        return new Gson().fromJson(userString, Usuario.class);
    }

    public boolean isUserLoged() {
        return (getUserInfo() != null);
    }

    public void removeFlags() {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.remove(Constants.PREF_FLAG_FIRST);
        editor.remove(Constants.PREF_FLAG_BD_SYNCED);
        editor.apply();
    }

    public void saveFlag(String flagName) {
        saveFlag(flagName, true);
    }

    public void saveFlag(String flagName, boolean flag) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putBoolean(flagName, flag);
        editor.apply();
    }

    public boolean getFlag(String flagName) {
        return mPreferences.getBoolean(flagName, false);
    }

    public boolean getIsRegistered() {
        return mPreferences.getBoolean(Constants.PREF_IS_REGISTERED, false);
    }

    public void setIsRegistered(boolean isRegistered) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putBoolean(Constants.PREF_IS_REGISTERED, isRegistered);
        editor.commit();
        editor.apply();
    }

}
