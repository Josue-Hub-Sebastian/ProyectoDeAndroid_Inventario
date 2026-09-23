package pe.com.dms.inventariosoft.features.missing;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import pe.com.dms.inventariosoft.data.PreferenceManager;
import pe.com.dms.inventariosoft.data.source.DataSourceRepository;
import pe.com.dms.inventariosoft.features.shared.BasePresenter;
import timber.log.Timber;

class MissingFragmentPresenter extends BasePresenter<MissingFragmentContract.View> implements MissingFragmentContract.Presenter {
    private final PreferenceManager preferenceManager;
    private final DataSourceRepository dataSourceRepository;
    private Disposable mDisposable;

    @Inject
    public MissingFragmentPresenter(PreferenceManager preferenceManager, DataSourceRepository dataSourceRepository) {
        this.preferenceManager = preferenceManager;
        this.dataSourceRepository = dataSourceRepository;
    }

    @Override
    public void attachView(MissingFragmentContract.View mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
    }

    @Override
    public void showMissing(String filtro) {
        dataSourceRepository.getMissing(
                preferenceManager.getConfig().getAlmacen().getIdInventario(),
                preferenceManager.getConfig().getAlmacen().getConteo(),
                filtro)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(missings -> getView().showMissing(missings), e -> {
                    Timber.e(e);
                    getView().showError();
                });
    }

    @Override
    public void setCriteria(int pos) {
        getView().setupValue(pos != 0 ? "" : preferenceManager.getConfig().getUbicacion(), preferenceManager.getConfig().isCameraScan());
    }
}
