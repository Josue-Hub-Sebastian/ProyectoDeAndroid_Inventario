package pe.com.dms.inventariosoft.injection.modules;

import android.content.Context;

import dagger.Module;
import dagger.Provides;
import pe.com.dms.inventariosoft.injection.annotations.ApplicationScope;

@Module
public class ContextModule {

    private final Context context;

    public ContextModule(Context context) {
        this.context = context.getApplicationContext();
    }

    @Provides
    @ApplicationScope
    public Context context() {
        return context;
    }
}
