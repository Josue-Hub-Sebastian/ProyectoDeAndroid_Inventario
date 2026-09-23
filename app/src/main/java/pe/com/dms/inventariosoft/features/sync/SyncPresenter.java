package pe.com.dms.inventariosoft.features.sync;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import pe.com.dms.inventariosoft.data.PreferenceManager;
import pe.com.dms.inventariosoft.data.models.LecturaEpc;
import pe.com.dms.inventariosoft.data.pojos.Configuracion;
import pe.com.dms.inventariosoft.data.pojos.EpcWs;
import pe.com.dms.inventariosoft.data.source.DataSourceRepository;
import pe.com.dms.inventariosoft.features.shared.BasePresenter;
import pe.com.dms.inventariosoft.utils.ErrorObserver;

class SyncPresenter extends BasePresenter<SyncContract.View> implements SyncContract.Presenter {

    private final DataSourceRepository dataSource;
    private final PreferenceManager preferenceManager;
    private final CompositeDisposable disposables = new CompositeDisposable();
    private int doneItems;
    private int totalItems;
    private Configuracion config;

    @Inject
    public SyncPresenter(DataSourceRepository dataSource, PreferenceManager preferenceManager) {
        this.dataSource = dataSource;
        this.preferenceManager = preferenceManager;
        this.config = preferenceManager.getConfig();
    }

    @Override
    public void attachView(SyncContract.View mvpView) {
        dataSource.setOnlineMode(true);
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
        dataSource.setOnlineMode(!config.isBatch());
        disposables.clear();
    }

    @Override
    public void setupView() {
        boolean isBatch = preferenceManager.getConfig().isBatch();
        String title, message;
        if (!isBatch) {
            title = "Descarga";
            message = "¿Desea realizar la descarga de información?";
        } else {
            title = "Carga";
//            TODO: Validar con Kathy
            message = "¿Desea cargar lo trabajado?";
        }

        getView().setupDialog(title, message, !isBatch);
    }

    @Override
    public void startSync() {
        boolean isBatch = preferenceManager.getConfig().isBatch();
        getView().startSync();
        if (!isBatch) {
            startDownload();
        } else {
            startUpload();
        }
    }

    private void startDownload() {

        List<Observable> observableList = new ArrayList<>(Arrays.asList(
                dataSource.cleanLocalDatabase().toObservable(),
                dataSource.listAllAlmacenesSync(),
                dataSource.listAllUsuariosSync(),
                dataSource.listAllProductosSync(),
                dataSource.listAllUbicacionesSync()
        ));

        doneItems = 0;
        totalItems = observableList.size();

        getView().updateProgress(doneItems, totalItems);

        Observable.fromIterable(observableList)
                .flatMap(observable -> observable
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnComplete(() -> {
                            doneItems += 1;
                            getView().updateProgress(doneItems, totalItems);
                        }))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((ErrorObserver<Object>) e -> getView().syncError(e.getMessage()));

    }

    @Override
    public void syncSuccess() {
        config.setBatch(!config.isBatch());
        preferenceManager.saveConfig(config);

        dataSource.setOnlineMode(!config.isBatch());
        getView().syncSuccess();
    }

    private void startUpload() {

        doneItems = 0;
        totalItems = dataSource.listAllLecturasLocal().blockingFirst().size();
        totalItems = totalItems + dataSource.listAllLecturasEPCLocal().blockingFirst().size();

        getView().updateProgress(doneItems, totalItems);

        disposables.add(dataSource.listAllLecturasLocal()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .flatMap(Observable::fromIterable)
                .map(lectura -> {
                    lectura.setFlagNuevoProducto(1);
                    return lectura;
                })
                .flatMap(dataSource::registerLectura)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mensajeResponse -> getView().updateProgress(++doneItems, totalItems),
                        throwable -> getView().syncError(throwable.getMessage()),
                        this::startEPC));

    }

    private void startEPC() {
        disposables.add(dataSource.listAllLecturasEPCLocal()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .flatMap(Observable::fromIterable)
                .flatMap(this::getEPCtoLectura)
                .flatMap(dataSource::registerEPC)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mensajeResponse -> getView().updateProgress(++doneItems, totalItems),
                        throwable -> getView().syncError(throwable.getMessage()), this::clearEPC));
    }

    private Observable<EpcWs> getEPCtoLectura(LecturaEpc lecturaEpc) {
        return Observable.create(emitter -> {
            try {
                EpcWs epcWs = new EpcWs();
                epcWs.setId_Inventario(String.valueOf(lecturaEpc.getIdInventario()));
                epcWs.setNroConteo(String.valueOf(lecturaEpc.getNroConteo()));
                epcWs.setEPC(lecturaEpc.getEpc());
                epcWs.setFecha(lecturaEpc.getFecha());
                emitter.onNext(epcWs);
                emitter.onComplete();
            } catch (Exception e) {
                emitter.onError(new Throwable("Error al crear el objeto del WS"));
            }
        });
    }

    private void clearEPC() {
        disposables.add(dataSource.clearEPC()
                .subscribeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .subscribe());
    }
}