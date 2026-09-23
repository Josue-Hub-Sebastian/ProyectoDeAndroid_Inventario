package pe.com.dms.inventariosoft.features.queries;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import pe.com.dms.inventariosoft.data.PreferenceManager;
import pe.com.dms.inventariosoft.data.models.Almacen;
import pe.com.dms.inventariosoft.data.pojos.Configuracion;
import pe.com.dms.inventariosoft.data.pojos.GroupConsulta;
import pe.com.dms.inventariosoft.data.pojos.HijoConsulta;
import pe.com.dms.inventariosoft.data.source.DataSourceRepository;
import pe.com.dms.inventariosoft.data.source.remote.ApiError;
import pe.com.dms.inventariosoft.features.shared.BasePresenter;
import retrofit2.HttpException;
import timber.log.Timber;

class QueryFragmentPresenter extends BasePresenter<QueryFragmentContract.View> implements QueryFragmentContract.Presenter {
    private final PreferenceManager preferenceManager;
    private final DataSourceRepository dataSource;
    private final CompositeDisposable disposables = new CompositeDisposable();
    private int nextInv;

    @Inject
    public QueryFragmentPresenter(PreferenceManager preferenceManager, DataSourceRepository dataSource) {
        this.preferenceManager = preferenceManager;
        this.dataSource = dataSource;
    }

    @Override
    public void attachView(QueryFragmentContract.View mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
        disposables.clear();
    }

    @Override
    public void syncLecture() {
        Almacen almacen = preferenceManager.getConfig().getAlmacen();
        // Sincronizamos con el usuario logueado para asegurar que se vean datos. // almacen.getUsername()
        disposables.add(dataSource.syncLecturaAll(almacen.getIdInventario(), "")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(lecturas -> {
                }, throwable -> {
                    ApiError apiError = ApiError.parse(((HttpException) throwable));
                    if (apiError.isInventarioDone()) {
                        nextInv = apiError.getNextInv();
                        getView().displayInventarioDoneError(apiError.getMessage());
                    }
                }));
    }

    @Override
    public void requestNextInventario() {
        Configuracion config = preferenceManager.getConfig();
        if (nextInv == -1) {
            config.setAlmacen(null);
            preferenceManager.saveConfig(config);
            getView().goToConfig();
            return;
        }

        disposables.add(dataSource.getNextInventario(config.getAlmacen().getUsername(), nextInv)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(almacen -> {
                    config.setAlmacen(almacen);
                    preferenceManager.saveConfig(config);
                }, e -> {

                }));
    }

    @Override
    public void showLecture(int tipo, String filtro) {
        Timber.d("%s , %s", tipo, filtro);
        Almacen almacen = preferenceManager.getConfig().getAlmacen();
        List<HijoConsulta> listHijos = new ArrayList<>();

        String ubicacion = (tipo == 0) ? filtro : "";
        String producto = (tipo == 1) ? filtro : "";
        String lote = (tipo == 2) ? filtro : "";

        disposables.add(dataSource.listLecturasWithFilters(almacen.getIdInventario(), "", ubicacion, producto, lote, "")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnTerminate(() -> {
                    if (tipo == 0) { // Ubicacion
                        disposables.add(dataSource.getLecturaGroupConsultaUbicacion(filtro)
                                .subscribeOn(Schedulers.io())
                                .flatMapIterable(groupConsultas -> groupConsultas)
                                .flatMap(groupConsulta ->
                                        dataSource.getLecturaGroupConsultaUbicacionHijo(groupConsulta.getTvProducto(), filtro)
                                )
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(
                                        hijos -> listHijos.addAll(hijos),
                                        throwable -> Timber.e(throwable.getMessage()),
                                        () -> getView().showLecture(listHijos)
                                ));

                    } else if (tipo == 1) { // Producto
                        disposables.add(dataSource.getLecturaGroupConsultaProducto(filtro)
                                .subscribeOn(Schedulers.io())
                                .flatMapIterable(groupConsultas -> groupConsultas)
                                .flatMap(groupConsulta ->
                                        dataSource.getLecturaGroupConsultaProductoHijo(groupConsulta.getTvProducto(), filtro)
                                )
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(
                                        listHijos::addAll,
                                        throwable -> Timber.e(throwable.getMessage()),
                                        () -> getView().showLecture(listHijos)
                                ));

                    } else if (tipo == 2) { // Lote
                        disposables.add(dataSource.getLecturaGroupConsultaLote(filtro)
                                .subscribeOn(Schedulers.io())
                                .flatMapIterable(groupConsultas -> groupConsultas)
                                .flatMap(groupConsulta ->
                                        dataSource.getLecturaGroupConsultaLoteHijo(groupConsulta.getTvProducto(), filtro)
                                )
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(
                                        listHijos::addAll,
                                        throwable -> Timber.e(throwable.getMessage()),
                                        () -> getView().showLecture(listHijos)
                                ));
                    }
                })
                .subscribe(lecturas -> Timber.d("Sincronización exitosa antes de consulta"),
                        throwable -> Timber.e("Error sincronizando: " + throwable.getMessage())));
    }

    @Override
    public void removeLectureHijo(int id, int position) {
        disposables.add(dataSource.deleteLecturaById(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> getView().removeLectura(),
                        throwable -> Timber.e(throwable.getMessage())));
    }

//    @Override
//    public void removeLecturePadre(List<HijoConsulta> items) {
//        disposables.add(Observable.fromIterable(items)
//                .subscribeOn(Schedulers.io())
//                .flatMap(hijoConsulta -> dataSource.deleteLecturaById(hijoConsulta.getId()).toObservable())
//                .subscribe(o -> {
//                }, throwable -> {
//                }, () -> {
//                    getView().removeLectura();
//                }));
//    }

    @Override
    public void removeAll() {
        Almacen almacen = preferenceManager.getConfig().getAlmacen();
        disposables.add(dataSource.deleteAllLecturas(almacen.getIdInventario(), almacen.getUsername())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> removeLecturas(almacen.getIdInventario(), almacen.getUsername()),
                        throwable -> getView().displayError(throwable.getMessage())));
    }

    private void removeLecturas(int inventarioId, String codUsuario) {
        disposables.add(dataSource.deleteAllLecturasLocal(inventarioId, codUsuario)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> getView().removeAll(), throwable -> getView().displayError(throwable.getMessage())));
    }

    @Override
    public void setCriteria(int pos) {
        getView().setupValue(pos != 0 ? "" : preferenceManager.getConfig().getUbicacion(), preferenceManager.getConfig().isCameraScan());

    }
}
