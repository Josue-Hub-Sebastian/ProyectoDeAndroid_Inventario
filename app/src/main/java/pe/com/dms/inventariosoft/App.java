package pe.com.dms.inventariosoft;

import android.content.Context;
import android.os.StrictMode;

import androidx.multidex.MultiDexApplication;

import pe.com.dms.inventariosoft.injection.AppComponent;
import pe.com.dms.inventariosoft.injection.DaggerAppComponent;
import pe.com.dms.inventariosoft.injection.modules.ContextModule;
import pe.com.dms.inventariosoft.utils.UtilMethods;
import timber.log.Timber;

public class App extends MultiDexApplication {

    private AppComponent appComponent;

    public static App get(Context context) {
        return (App) context.getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        UtilMethods.initialize(getApplicationContext());
        buildDependecyInjection();

        if (BuildConfig.DEBUG) {
            // Se comenta temporalmente para permitir compilación si Timber falla
            // Timber.plant(new Timber.DebugTree());
            setStrictMode();
        }
    }
    public void buildDependecyInjection() {
        appComponent = DaggerAppComponent.builder()
                .contextModule(new ContextModule(this))
                .build();
    }

    public AppComponent getAppComponent() {
        return appComponent;
    }

    private void setStrictMode() {
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .build());

        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectLeakedSqlLiteObjects()
                .penaltyLog()
                .penaltyDeath()
                .build());
    }
}
