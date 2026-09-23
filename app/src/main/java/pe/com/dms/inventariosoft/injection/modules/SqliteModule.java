package pe.com.dms.inventariosoft.injection.modules;

import androidx.room.Room;
import android.content.Context;

import dagger.Module;
import dagger.Provides;
import pe.com.dms.inventariosoft.BuildConfig;
import pe.com.dms.inventariosoft.data.source.local.AppDatabase;
import pe.com.dms.inventariosoft.injection.annotations.ApplicationScope;

@Module()
public class SqliteModule {

    @ApplicationScope
    @Provides
    public AppDatabase appDatabase(@ApplicationScope Context context) {
        return Room.databaseBuilder(context, AppDatabase.class, BuildConfig.DB_NAME)
                .allowMainThreadQueries()  // TODO: Remove for production
                .fallbackToDestructiveMigration() // CANELAZA _ quitar despues de pruebaz
                .build();
    }
}
