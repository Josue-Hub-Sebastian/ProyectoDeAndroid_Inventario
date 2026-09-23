
package pe.com.dms.inventariosoft.injection;

import dagger.Component;
import pe.com.dms.inventariosoft.features.config.ConfigFragment;
import pe.com.dms.inventariosoft.features.config.ip.ConfigIpActivity;
import pe.com.dms.inventariosoft.features.home.HomeFragment;
import pe.com.dms.inventariosoft.features.lecture.LectureFragment;
import pe.com.dms.inventariosoft.features.lecture.list.LectureListFragment;
import pe.com.dms.inventariosoft.features.login.LoginActivity;
import pe.com.dms.inventariosoft.features.main.MainActivity;
import pe.com.dms.inventariosoft.features.missing.MissingFragment;
import pe.com.dms.inventariosoft.features.queries.QueryFragment;
import pe.com.dms.inventariosoft.features.splash.SplashActivity;
import pe.com.dms.inventariosoft.features.sync.SyncDialog;
import pe.com.dms.inventariosoft.injection.annotations.ActivityScope;
import pe.com.dms.inventariosoft.injection.modules.ActivityModule;

@ActivityScope
@Component(modules = {ActivityModule.class}, dependencies = AppComponent.class)
public interface ActivityComponent {

    void inject(SplashActivity activity);

    void inject(ConfigIpActivity activity);

    void inject(LoginActivity activity);

    void inject(MainActivity activity);

    void inject(SyncDialog dialog);

    void inject(ConfigFragment fragment);

    void inject(QueryFragment fragment);

    void inject(LectureFragment fragment);

    void inject(HomeFragment fragment);

    void inject(MissingFragment fragment);

    void inject(LectureListFragment fragment);
}
