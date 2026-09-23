package pe.com.dms.inventariosoft.features.lecture.list;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import pe.com.dms.inventariosoft.data.PreferenceManager;
import pe.com.dms.inventariosoft.data.models.Lectura;
import pe.com.dms.inventariosoft.data.models.LecturaEpc;
import pe.com.dms.inventariosoft.data.models.Producto;
import pe.com.dms.inventariosoft.data.pojos.LecturaEpcDes;
import pe.com.dms.inventariosoft.data.pojos.LecturaRFID;
import pe.com.dms.inventariosoft.data.pojos.LecturaWS;
import pe.com.dms.inventariosoft.data.source.DataSourceRepository;
import pe.com.dms.inventariosoft.data.source.remote.ApiError;
import pe.com.dms.inventariosoft.features.shared.BasePresenter;
import retrofit2.HttpException;
import timber.log.Timber;

public class LectureListPresenter extends BasePresenter<LectureListContract.View>
        implements LectureListContract.Presenter {
    String TAG = LectureListPresenter.class.getSimpleName();
    private static final int ITEMS = 999999;
    private List<Lectura> todasLasLecturas = new ArrayList<>();
    private List<LecturaRFID> todasLasLecturasRFID = new ArrayList<>();
    private int paginaActual = 0;
    private String searchQuery = "";
    private static final int ITEMS_POR_PAGINA = 10;// checkpoint
    private final PreferenceManager preferenceManager;
    private final DataSourceRepository dataSourceRepository;
    private final CompositeDisposable disposables = new CompositeDisposable();



    @Inject
    public LectureListPresenter(PreferenceManager preferenceManager, DataSourceRepository dataSourceRepository) {
        this.preferenceManager = preferenceManager;
        this.dataSourceRepository = dataSourceRepository;
    }

    @Override
    public void attachView(LectureListContract.View mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
        disposables.clear();
    }

    @Override
    public boolean isRFID() {
        return preferenceManager.getConfig().isRfd();
    }

    @Override
    public void loadLastLectures(Boolean checkedFiltro) {
        // SEGURIDAD: Si no hay almacén, no cargamos lecturas (evita crash al cerrar inventario)
        if (preferenceManager.getConfig().getAlmacen() == null) {
            Log.w(TAG, "loadLastLectures: Almacén nulo, abortando carga.");
            return;
        }

        if (preferenceManager.getConfig().isRfd()) {
            disposables.add(dataSourceRepository.listLastLecturasEPC(
                    preferenceManager.getConfig().getAlmacen().getIdInventario(),
                    preferenceManager.getConfig().getAlmacen().getConteo())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(lecturaRFIDS -> {
                        List<LecturaRFID> newListFiltra = new ArrayList<>();

                        Log.e(TAG, "displayLecturesRFID lecturas: " + lecturaRFIDS);
                        for (LecturaRFID l: lecturaRFIDS) {
                            Producto producto = dataSourceRepository.findProductoByCode(l.getCodigo());

                            try {
                                l.setDescripcion((producto == null) ? "" : producto.getDescripcion());
                                Log.e(TAG, "displayLecturesRFID lectura: " + l);

                                String caracter = "";
                                if (l.getEpc().length() >= 11 && checkedFiltro && !l.getDescripcion().isEmpty()) {
                                    caracter = String.valueOf(l.getEpc().charAt(10));
                                    if (caracter.contains("A")) {
                                        newListFiltra.add(l);
                                    }
                                }
                                if (checkedFiltro == false) {
                                    newListFiltra.add(l);
                                }

                            } catch (IndexOutOfBoundsException e) {
                                Log.e(TAG,"thow loadLastLecture: " + e);
                            }

                        }
                        Log.e(TAG,"loadLastLectures lecturaRFIDS: "+newListFiltra);
                        todasLasLecturasRFID = newListFiltra;
                        paginaActual = 0;
                        mostrarPaginaActual();
                    }, throwable -> {
                        Log.e(TAG,"throwable " +  throwable);
                    }));
        } else {
            disposables.add(dataSourceRepository.listLastLecturas(preferenceManager.getConfig().getAlmacen().getIdInventario(),
                    preferenceManager.getConfig().getAlmacen().getUsername(), ITEMS)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    //.subscribe(lecturas -> getView().displayLectures(lecturas), throwable -> {
                    .subscribe(lecturas -> {
                        todasLasLecturas = lecturas;
                        paginaActual = 0;
                        mostrarPaginaActual();
                    }, throwable -> {}));
        }
    }

    @Override
    public void deleteLecture(int id) {
        disposables.add(dataSourceRepository.deleteLecturaById(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> getView().lectureDeleted(),
                        throwable -> getView().displayError(throwable.getMessage())));
    }

    @Override
    public void deleteLectureRFID(String epc) {
        Log.e(TAG,"deleteLectureRFID epc: "+epc);
        disposables.add(dataSourceRepository.deleteLecturaRFIDById(epc)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                }, throwable -> {
                })
        );
    }
// displayLectures
    @Override
    public void saveRFI(String codUbicacion , Boolean filterChecked) {
        if (preferenceManager.getConfig().isBatch()) {
            Log.d("AQUI", "ENTRO BATCH");
            if (dataSourceRepository.findUbicacionByCode(codUbicacion) == null) {
                getView().displayError("La ubicación ingresada no existe");
            } else {
                disposables.add(dataSourceRepository.searchEPCDes(
                        preferenceManager.getConfig().getAlmacen().getIdInventario(),
                        preferenceManager.getConfig().getAlmacen().getConteo())
                        .subscribeOn(Schedulers.io())
                        .observeOn(Schedulers.io())
                        .flatMap(lecturaEpcDes -> getEPCtoLectura(lecturaEpcDes, codUbicacion))
                        .flatMap(Observable::fromIterable)
                        .flatMap(dataSourceRepository::registerLectura)
                        .ignoreElements()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(this::updateEPC, throwable -> getView().displayError(throwable.getMessage())));
            }
        } else {
            Log.d("AQUI", "ENTRO REMOTO");
            disposables.add(dataSourceRepository.searchEPCWS(
                    preferenceManager.getConfig().getAlmacen().getIdInventario(),
                    preferenceManager.getConfig().getAlmacen().getConteo())
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.io())
                    .flatMap(list -> {
                                Log.d(TAG, "searchEPCWS " + list);
                                List<LecturaEpc> newList = new ArrayList<>();

                                for (LecturaEpc l : list) {
                                    if (l.getEpc().length() >= 11) {
                                        String caracter = String.valueOf(l.getEpc().charAt(10));
                                        if (caracter.contains("A")) {
                                            newList.add(l);
                                        }
                                    } else {
                                        newList.add(l);
                                    }

                                }
                                Log.d("AQUI", "ENTRO REMOTO envio lect epc -> " + newList);
                             return getEPCtoWS(newList, codUbicacion);
                    }
                    )
                    .flatMap(dataSourceRepository::registerLecturaRFID)
                    .flatMap(mensajeResponse -> {
                        Log.d("AQUI", "registerLecturaRFID-> " + mensajeResponse);

                        Observable<String> a = new Observable<String>() {
                            @Override
                            protected void subscribeActual(Observer<? super String> observer) {

                            }
                        };

                        if (filterChecked) {
                            a = dataSourceRepository.updateEPC(
                                    preferenceManager.getConfig().getAlmacen().getIdInventario(),
                                    mensajeResponse.getCod(), 2, preferenceManager.getConfig().getAlmacen().getConteo());
                        }

                        return a;
                    } )
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(s -> {
                        Log.d("AQUI", "RESPONSE -> " + s);
                        if ("0".equalsIgnoreCase(s)) {
                            getView().displayMessage("Se terminó con el envió");
                        } else {
                            getView().displayError("Error al enviar los datos.");
                        }
                    }, throwable -> {
                        if (throwable instanceof HttpException) {
                            ApiError apiError = ApiError.parse(((HttpException) throwable));
                            Log.e(TAG,"throwable: "+apiError.getMessage());
                            Timber.e(throwable);
                            getView().displayError(apiError.getMessage());
                        } else {
                            Timber.e(throwable);
                            getView().displayError("Error al enviar los datos.");
                        }
                    }));
        }
    }

    private void updateEPC() {
        disposables.add(dataSourceRepository.updateEPC(
                preferenceManager.getConfig().getAlmacen().getIdInventario(), 0, 1,
                preferenceManager.getConfig().getAlmacen().getConteo())
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(s -> {
                    if ("0".equalsIgnoreCase(s)) {
                        getView().displayMessage("Se terminó con el envió");
                    } else {

                    }
                }, throwable -> getView().displayError(throwable.getMessage())));
    }

    private Observable<List<LecturaWS>> getEPCtoWS(List<LecturaEpc> list, String codUbicacion) {
        Log.e(TAG, "getEPCtoWS: " + list + ", codUbicacion: " + codUbicacion);
        return Observable.create(emitter -> {
            try {

                List<LecturaWS> list1 = new ArrayList<>();
                for (LecturaEpc lecturaRFID : list) {
                    LecturaWS lecturaWS = new LecturaWS();
                    lecturaWS.setCodUbicacion(codUbicacion);
                    if (lecturaRFID.getEpc().length() >= 10 ){
                        lecturaWS.setCodProducto(lecturaRFID.getEpc().substring(0, 10));
                        Producto producto = dataSourceRepository.findProductoByCode(lecturaWS.getCodProducto());
                        if (producto == null) continue;
                    }
                    lecturaWS.setCodAlmacen(preferenceManager.getConfig().getAlmacen().getCodigo());
                    lecturaWS.setCodUsuario(preferenceManager.getUserInfo().getUsername());
                    lecturaWS.setLote("");
                    lecturaWS.setSerie("");
                    lecturaWS.setCantidad(1);
                    lecturaWS.setFlgProducto(0);
                    lecturaWS.setEPC(lecturaRFID.getEpc());
                    list1.add(lecturaWS);
                    Log.e(TAG, "getEPCtoWS list1: " + list1.size());
                }
                emitter.onNext(list1);
                emitter.onComplete();
            } catch (Exception e) {
                emitter.onError(new Throwable("Error al crear el objeto del WS" + " | " + e.toString()) );
            }
        });
    }

    private Observable<List<Lectura>> getEPCtoLectura(List<LecturaEpcDes> list, String codUbicacion) {
        return Observable.create(emitter -> {
            try {
                List<Lectura> list1 = new ArrayList<>();
                for (LecturaEpcDes lecturaEpc : list) {
                    Lectura lectura = new Lectura();
                    lectura.setIdInventario(preferenceManager.getConfig().getAlmacen().getIdInventario());
                    lectura.setLote("");
                    lectura.setSerie("");
                    lectura.setCantidad(1);
                    lectura.setCodigoProducto(lecturaEpc.getEpc().substring(0, 10));
                    lectura.setCodigoUbicacion(codUbicacion);
                    lectura.setCodigoAlmacen(preferenceManager.getConfig().getAlmacen().getCodigo());
                    lectura.setCodigoUsuario(preferenceManager.getUserInfo().getUsername());
                    lectura.setDescProducto(lecturaEpc.getDescripcion());
                    lectura.setFlagNuevoProducto(0);
                    lectura.setLocal(0);
                    lectura.setEpc(lecturaEpc.getEpc());
                    list1.add(lectura);
                }
                emitter.onNext(list1);
                emitter.onComplete();
            } catch (Exception e) {
                emitter.onError(new Throwable("Error al crear el objeto del WS"));
            }
        });
    }

    private void mostrarPaginaActual() {
        int inicio = paginaActual * ITEMS_POR_PAGINA;

        if (isRFID()) {
            List<LecturaRFID> listaFiltrada = new ArrayList<>();
            if (searchQuery.isEmpty()) {
                listaFiltrada = todasLasLecturasRFID;
            } else {
                for (LecturaRFID item : todasLasLecturasRFID) {
                    if (item.getCodigo().toLowerCase().contains(searchQuery.toLowerCase()) ||
                            (item.getDescripcion() != null && item.getDescripcion().toLowerCase().contains(searchQuery.toLowerCase()))) {
                        listaFiltrada.add(item);
                    }
                }
            }

            int totalItems = listaFiltrada.size();
            int totalPaginas = (int) Math.ceil((double) totalItems / ITEMS_POR_PAGINA);
            if (totalPaginas == 0) totalPaginas = 1;

            getView().actualizarTextoPagina("Página " + (paginaActual + 1) + " de " + totalPaginas);

            if (listaFiltrada.isEmpty()) {
                getView().displayLecturesRFID(new ArrayList<>());
                return;
            }

            int fin = Math.min(inicio + ITEMS_POR_PAGINA, totalItems);
            if (inicio >= totalItems) {
                paginaActual = Math.max(0, totalPaginas - 1);
                inicio = paginaActual * ITEMS_POR_PAGINA;
                fin = Math.min(inicio + ITEMS_POR_PAGINA, totalItems);
            }

            List<LecturaRFID> pagina = new ArrayList<>(listaFiltrada.subList(inicio, fin));
            getView().displayLecturesRFID(pagina);

        } else {
            List<Lectura> listaFiltrada = new ArrayList<>();
            if (searchQuery.isEmpty()) {
                listaFiltrada = todasLasLecturas;
            } else {
                for (Lectura item : todasLasLecturas) {
                    if (item.getCodigoProducto().toLowerCase().contains(searchQuery.toLowerCase()) ||
                            (item.getDescProducto() != null && item.getDescProducto().toLowerCase().contains(searchQuery.toLowerCase()))) {
                        listaFiltrada.add(item);
                    }
                }
            }

            int totalItems = listaFiltrada.size();
            int totalPaginas = (int) Math.ceil((double) totalItems / ITEMS_POR_PAGINA);
            if (totalPaginas == 0) totalPaginas = 1;

            getView().actualizarTextoPagina("Página " + (paginaActual + 1) + " de " + totalPaginas);

            if (listaFiltrada.isEmpty()) {
                getView().displayLectures(new ArrayList<>());
                return;
            }

            int fin = Math.min(inicio + ITEMS_POR_PAGINA, totalItems);
            if (inicio >= totalItems) {
                paginaActual = Math.max(0, totalPaginas - 1);
                inicio = paginaActual * ITEMS_POR_PAGINA;
                fin = Math.min(inicio + ITEMS_POR_PAGINA, totalItems);
            }

            List<Lectura> pagina = new ArrayList<>(listaFiltrada.subList(inicio, fin));
            getView().displayLectures(pagina);
        }
    }

    @Override
    public void loadNextPage() {
        int totalItems;
        if (isRFID()) {
            if (searchQuery.isEmpty()) {
                totalItems = todasLasLecturasRFID.size();
            } else {
                int count = 0;
                for (LecturaRFID item : todasLasLecturasRFID) {
                    if (item.getCodigo().toLowerCase().contains(searchQuery.toLowerCase()) ||
                            (item.getDescripcion() != null && item.getDescripcion().toLowerCase().contains(searchQuery.toLowerCase()))) {
                        count++;
                    }
                }
                totalItems = count;
            }
        } else {
            if (searchQuery.isEmpty()) {
                totalItems = todasLasLecturas.size();
            } else {
                int count = 0;
                for (Lectura item : todasLasLecturas) {
                    if (item.getCodigoProducto().toLowerCase().contains(searchQuery.toLowerCase()) ||
                            (item.getDescProducto() != null && item.getDescProducto().toLowerCase().contains(searchQuery.toLowerCase()))) {
                        count++;
                    }
                }
                totalItems = count;
            }
        }

        if ((paginaActual + 1) * ITEMS_POR_PAGINA < totalItems) {
            paginaActual++;
            mostrarPaginaActual();
        }
    }

    @Override
    public void loadPreviousPage() {
        if (paginaActual > 0) {
            paginaActual--;
            mostrarPaginaActual();
        }
    }

    @Override
    public void search(String query) {
        this.searchQuery = query;
        this.paginaActual = 0;
        mostrarPaginaActual();
    }



}
