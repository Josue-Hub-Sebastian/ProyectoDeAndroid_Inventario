package pe.com.dms.inventariosoft.features.config.ip;

import javax.inject.Inject;

import io.reactivex.disposables.Disposable;
import pe.com.dms.inventariosoft.data.PreferenceManager;
import pe.com.dms.inventariosoft.data.pojos.Configuracion;
import pe.com.dms.inventariosoft.data.source.DataSourceRepository;
import pe.com.dms.inventariosoft.features.shared.BasePresenter;

class ConfigIpPresenter extends BasePresenter<ConfigIpContract.View> implements ConfigIpContract.Presenter {

    private final PreferenceManager preferenceManager;
    private final DataSourceRepository dataSourceRepository;
    private final Configuracion configuracion;
    private Disposable mDisposable;

    @Inject
    public ConfigIpPresenter(PreferenceManager preferenceManager, DataSourceRepository dataSourceRepository) {
        this.preferenceManager = preferenceManager;
        this.dataSourceRepository = dataSourceRepository;
        this.configuracion = preferenceManager.getConfig();
    }

    @Override
    public void attachView(ConfigIpContract.View mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
        if (mDisposable != null) mDisposable.dispose();
    }

    @Override
    public void onViewCreated() {
        getView().displayIp(configuracion.getServidor());
    }

    @Override
    public void updateIp(String ip) {
        configuracion.setServidor(ip);
        preferenceManager.saveConfig(configuracion);
    }
}