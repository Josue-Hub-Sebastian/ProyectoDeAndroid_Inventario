package pe.com.dms.inventariosoft.injection;

import android.content.Context;

import dagger.Component;
import pe.com.dms.inventariosoft.data.PreferenceManager;
import pe.com.dms.inventariosoft.data.source.DataSourceRepository;
import pe.com.dms.inventariosoft.data.source.local.AppDatabase;
import pe.com.dms.inventariosoft.data.source.remote.WebServices;
import pe.com.dms.inventariosoft.injection.annotations.ApplicationScope;
import pe.com.dms.inventariosoft.injection.modules.ContextModule;
import pe.com.dms.inventariosoft.injection.modules.SqliteModule;
import pe.com.dms.inventariosoft.injection.modules.WebServicesModule;

@Component(modules = {ContextModule.class, WebServicesModule.class, SqliteModule.class})
@ApplicationScope
public interface AppComponent {

    WebServices webServices();

    PreferenceManager preferenceManager();

    DataSourceRepository dataSourceRepository();

    AppDatabase appDatabase();

    Context context();

}
