package pe.com.dms.inventariosoft.features.config;

import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import pe.com.dms.inventariosoft.data.PreferenceManager;
import pe.com.dms.inventariosoft.data.models.Almacen;
import pe.com.dms.inventariosoft.data.models.Usuario;
import pe.com.dms.inventariosoft.data.pojos.Configuracion;
import pe.com.dms.inventariosoft.data.source.DataSourceRepository;
import pe.com.dms.inventariosoft.data.source.remote.ApiError;
import pe.com.dms.inventariosoft.features.shared.BasePresenter;
import pe.com.dms.inventariosoft.utils.Constants;
import pe.com.dms.inventariosoft.utils.ErrorObserver;
import retrofit2.HttpException;

class ConfigFragmentPresenter extends BasePresenter<ConfigFragmentContract.View>
        implements ConfigFragmentContract.Presenter {

    private final PreferenceManager preferenceManager;
    private final DataSourceRepository dataSourceRepository;
    private final Configuracion configuracion;
    private final CompositeDisposable disposables = new CompositeDisposable();

    @Inject
    public ConfigFragmentPresenter(PreferenceManager preferenceManager, DataSourceRepository dataSourceRepository) {
        this.preferenceManager = preferenceManager;
        this.dataSourceRepository = dataSourceRepository;
        this.configuracion = preferenceManager.getConfig();
    }
/*presenter getView()*/
    @Override
    public void attachView(ConfigFragmentContract.View mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
        disposables.clear();
    }

    @Override
    public void onViewCreated() {
        getView().initListeners();
        getView().display(configuracion);
        if (configuracion.isRfd()) {
            saveRFID(true);
        }
    }

    @Override
    public void getListAlmacen() {
        disposables.add(dataSourceRepository.listAlmacenesByUser(preferenceManager.getUserInfo().getUsername())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(almacenList -> getView().selectAlmacen(almacenList), throwable -> {
                })
        );
    }

    @Override
    public void saveModo(int modo) {
        configuracion.setModo(modo);
        preferenceManager.saveConfig(configuracion);
    }

    @Override
    public void saveRFID(boolean flag) {
        configuracion.setRfd(flag);
        configuracion.setModo(Constants.MODE_BARRIDO);
        configuracion.setLote(false);
        configuracion.setSerie(false);
        configuracion.setCameraScan(false);
        configuracion.setSolicitarConfirmacion(false);
        configuracion.setRegistrar(false);
        preferenceManager.saveConfig(configuracion);
        getView().display(configuracion);
        getView().showRFID(!flag);
    }


    public void savePDA(){
        configuracion.setRfd(false);
        configuracion.setModo(Constants.MODE_MANUAL);
        configuracion.setCameraScan(false);
        configuracion.setCameraScan(false);
        preferenceManager.saveConfig(configuracion);
        getView().showRFID(true);


    }

    @Override
    public void saveAlmacen(Almacen almacen) {
        configuracion.setAlmacen(almacen);
        preferenceManager.saveConfig(configuracion);
    }

    //por que estos metodos no se usan si tienen override en dataSourceRepository
    @Override
    public void saveUbicacion(String ubicacion) {
        configuracion.setUbicacion(ubicacion);
        preferenceManager.saveConfig(configuracion);
    }

    @Override
    public void saveLote(boolean flag) {
        configuracion.setLote(flag);
        preferenceManager.saveConfig(configuracion);
    }

    @Override
    public void saveSerie(boolean flag) {
        configuracion.setSerie(flag);
        configuracion.setRfd(false);

        preferenceManager.saveConfig(configuracion);
    }

    @Override
    public void saveCamaraScan(boolean flag) {
        configuracion.setCameraScan(flag);
        configuracion.setRfd(false);
        configuracion.setModo(Constants.MODE_MANUAL);
        preferenceManager.saveConfig(configuracion);
        getView().showRFID(true);
//        getView().display(configuracion);

    }

    @Override
    public void saveBatch(boolean flag) {
        configuracion.setBatch(flag);
        preferenceManager.saveConfig(configuracion);
    }

    @Override
    public void saveSolicitarConfirmacion(boolean flag) {
        configuracion.setSolicitarConfirmacion(flag);
        preferenceManager.saveConfig(configuracion);
    }

    @Override
    public void saveSizeNumber(int sizeNumber) {
        configuracion.setSizeNumber(sizeNumber);
        preferenceManager.saveConfig(configuracion);
    }

    @Override
    public void saveDecimalNumber(boolean flag) {
        configuracion.setNumberDecimal(flag);
        preferenceManager.saveConfig(configuracion);
    }

    @Override
    public void saveCalculadora(boolean flag) {
        configuracion.setCalculadora(flag);
        preferenceManager.saveConfig(configuracion);
    }



    @Override
    public Usuario getUserInfo() {
        return preferenceManager.getUserInfo();
    }

    public void saveRegistrar(boolean flag) {
        configuracion.setRegistrar(flag);
        preferenceManager.saveConfig(configuracion);
    }

    public void logout() {
        Usuario usuario = preferenceManager.getUserInfo();
        disposables.add(dataSourceRepository.logoutUsuario(usuario)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mensajeResponse -> {
                    if (mensajeResponse.getCod() == 1) {
                        preferenceManager.removeUser();
                        preferenceManager.removeUserConfig();
                        getView().logoutSuccess();
                    } else {
                        getView().showMessage(mensajeResponse.getMsg());
                        getView().loading(false);
                    }
                }, throwable -> {
                    String errorMsg = "Error al cerrar sesión en el servidor";
                    if (throwable instanceof HttpException) {
                        ApiError apiError = ApiError.parse((HttpException) throwable);
                        if (apiError.getMessage() != null) errorMsg = apiError.getMessage();
                    }
                    getView().showMessage(errorMsg);
                    getView().loading(false);
                }));
    }

    @Override
    public void downLoadProducts() {

      /*  disposables.add(dataSourceRepository.listAllProductosSync()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnComplete(() -> {

                        })
                       e -> {

                        })*/

        List<Observable> observableList = new ArrayList<>(Arrays.asList(
                dataSourceRepository.listAllProductosSync()

        ));
        Observable.fromIterable(observableList)
                .flatMap(observable -> observable
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnComplete(() -> {
                            Log.d("aqui", "termino");
                            Thread.sleep(1500);
                            getView().loading(false);
                        }))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((ErrorObserver<Object>) e -> {});

    }
}