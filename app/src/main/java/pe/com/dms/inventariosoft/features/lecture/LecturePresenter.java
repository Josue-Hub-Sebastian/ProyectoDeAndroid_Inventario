package pe.com.dms.inventariosoft.features.lecture;

import android.util.Log;
import android.util.Pair;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import pe.com.dms.inventariosoft.data.PreferenceManager;
import pe.com.dms.inventariosoft.data.models.Almacen;
import pe.com.dms.inventariosoft.data.models.EstadoInventarioResponse;
import pe.com.dms.inventariosoft.data.models.EstadoParticipacion;
import pe.com.dms.inventariosoft.data.models.Lectura;
import pe.com.dms.inventariosoft.data.models.Producto;
import pe.com.dms.inventariosoft.data.models.ProductoAsignado;
import pe.com.dms.inventariosoft.data.models.Ubicacion;
import pe.com.dms.inventariosoft.data.pojos.Configuracion;
import pe.com.dms.inventariosoft.data.pojos.MensajeResponse;
import pe.com.dms.inventariosoft.data.source.DataSourceRepository;
import pe.com.dms.inventariosoft.data.source.remote.ApiError;
import pe.com.dms.inventariosoft.features.shared.BasePresenter;
import pe.com.dms.inventariosoft.utils.Constants;
import pe.com.dms.inventariosoft.utils.UtilMethods;
import retrofit2.HttpException;
import timber.log.Timber;

class LecturePresenter extends BasePresenter<LectureContract.View>
        implements LectureContract.Presenter {
    String TAG = LecturePresenter.class.getSimpleName();

    private final PreferenceManager preferenceManager;
    private final DataSourceRepository dataSourceRepository;
    private final CompositeDisposable disposables = new CompositeDisposable();
    private final CompositeDisposable timerDisposable = new CompositeDisposable();

    private int nextInv = -1;
    private Pattern mPattern;

    @Inject
    public LecturePresenter(PreferenceManager preferenceManager, DataSourceRepository dataSourceRepository) {
        this.preferenceManager = preferenceManager;
        this.dataSourceRepository = dataSourceRepository;
        Configuracion config = preferenceManager.getConfig();
        mPattern = Pattern.compile("(([0-9]{1})([0-9]{0," + (config.getSizeNumber() - 1) + "})?)(\\.[0-9]{0," + 2 + "})?");
    }

    @Override
    public void detachView() {
        super.detachView();
        disposables.clear();
        timerDisposable.clear();
    }
// searchProducto
    @Override
    public void onViewCreated() {
        Log.i(TAG, "onViewCreated: ");
        
        // Vigilante dinamico: usa siempre la configuración actualizada
        disposables.add(
                Observable.interval(0, 20, TimeUnit.SECONDS)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(tick -> {
                            Configuracion configActual = preferenceManager.getConfig();
                            if (configActual.getAlmacen() == null) return;
                            getLectura();

                            disposables.add(dataSourceRepository.getEstadoInventario(
                                            configActual.getAlmacen().getIdInventario(), 
                                            configActual.getAlmacen().getUsername())
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(estado -> {
                                        // asi se identificara el cambio de conteo por medio del ID
                                        if ((estado.getIdInventario() != 0 && estado.getIdInventario() != configActual.getAlmacen().getIdInventario()) ||
                                            (estado.getNroConteo() > configActual.getAlmacen().getConteo())) {
                                            
                                            if (estado.getIdInventario() != 0) {
                                                configActual.getAlmacen().setIdInventario(estado.getIdInventario());
                                                preferenceManager.saveConfig(configActual);
                                            }
                                            if (isViewAttached()) getView().goToHome();
                                            return;
                                        }

                                        if (EstadoParticipacion.NO_PARTICIPA.equals(estado.getEstadoParticipacion())) {
                                            if (isViewAttached()) {
                                                getView().showError("Ya no estás habilitado para este conteo.");
                                                getView().goToHome();
                                            }
                                            return;
                                        }

                                        if (EstadoParticipacion.PENDIENTE_RESPUESTA.equals(estado.getEstadoParticipacion())) {
                                            if (isViewAttached()) getView().goToHome();
                                            return;
                                        }

                                        // Inventario finalizado por completo
                                        if (estado.getInventarioFinalizado() == 1) {
                                            nextInv = -1;
                                            String msg = (estado.getMessage() != null && !estado.getMessage().isEmpty())
                                                    ? estado.getMessage() : "El inventario ha finalizado por completo.";
                                            if (isViewAttached()) getView().displayInventarioDoneError(msg);
                                        }
                                    }, e -> {
                                        if (e instanceof HttpException && ((HttpException) e).code() == 404) {
                                            if (configActual.getAlmacen().getConteo() > 1) {
                                                if (isViewAttached()) {
                                                    getView().showError("No estás habilitado para este conteo diferencial.");
                                                    getView().goToHome();
                                                }
                                            }
                                        }
                                    }));
                        }, e -> Log.e(TAG, "Vigilante fatal error", e))
        );

        Configuracion configActual = preferenceManager.getConfig();
        if (isViewAttached() && configActual.getAlmacen() != null) {
            getView().setupConteo(configActual.getAlmacen().getConteo());
            getView().setupCameraScan(configActual.isCameraScan());
            getView().setupUbicacion(configActual.getUbicacion());
            getView().setupProducto(configActual.getModo() == Constants.MODE_MANUAL);
            getView().setupLote(configActual.isLote(), configActual.isCameraScan());
            getView().setupModoConteo(configActual.getModo());
            getView().setupSerie(configActual.isSerie(), configActual.isCameraScan());
            getView().setupCantidad(configActual.getModo() == Constants.MODE_BARRIDO);
            if(configActual.isSolicitarConfirmacion()) getView().setupConfirmation();
        }
    }

    @Override
    public boolean isBarrido() {
        return preferenceManager.getConfig().getModo() == Constants.MODE_BARRIDO;
    }

    @Override
    public void getLectura() {
        Configuracion configActual = preferenceManager.getConfig();
        if (configActual.getAlmacen() == null) return;

        disposables.add(
                dataSourceRepository.contarLecturas(configActual.getAlmacen().getIdInventario(), configActual.getAlmacen().getUsername())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .map(MensajeResponse::getCod)
                        .subscribe(cantidad -> {
                            if (isViewAttached()) getView().setupLecturas(cantidad);
                        }, e -> {
                            ApiError apiError = ApiError.parse(((HttpException) e));
                            if (apiError != null && apiError.isInventarioDone()) {
                                nextInv = apiError.getNextInv();
                                if (isViewAttached()) getView().displayInventarioDoneError(apiError.getMessage());
                            }
                        })
        );
    }

    @Override
    public void requestNextInventario() {
        Configuracion configActual = preferenceManager.getConfig();
        
        // CASO: Inventario culminado por completo
        if (nextInv == -1) {
            configActual.setAlmacen(null);
            configActual.setUbicacion("");
            preferenceManager.saveConfig(configActual);
            
            if (isViewAttached()) {
                getView().showError("El inventario ha finalizado por completo.");
                getView().goToHome(); 
                getView().goToConfig(); // Refresco del menu | MainActivity |
            }
            return;
        }

        if (configActual.getAlmacen() == null) {
            if (isViewAttached()) getView().goToHome();
            return;
        }

        disposables.add(
                dataSourceRepository.getNextInventario(configActual.getAlmacen().getUsername(), nextInv)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(almacen -> {
                            configActual.setAlmacen(almacen);
                            preferenceManager.saveConfig(configActual);
                            if (isViewAttached()) getView().goToHome();
                        }, e -> {
                            if (isViewAttached()) getView().goToHome();
                        })
        );
    }

    @Override
    public void createUbicacion(String codUbicacion) {
        // XD oe aca no hay nada como asi? jajaja
    }

    private boolean validate(Lectura lectura) {
        Configuracion configActual = preferenceManager.getConfig();
        String message = "Se encontraron los siguientes errores:";

        if (UtilMethods.isEmpty(lectura.getCodigoUbicacion())) message += "\n- Debe ingresar la ubicación";
        if (dataSourceRepository.findUbicacionByCode(lectura.getCodigoUbicacion()) == null) message += "\n- La ubicación ingresada no existe";
        if (UtilMethods.isEmpty(lectura.getCodigoProducto())) message += "\n- Debe ingresar el código de producto";
        if (!configActual.isRegistrar()) {
            String codigoProd = lectura.getCodigoProducto();
            Producto prod = dataSourceRepository.findProductoByCode(codigoProd);
            if (prod == null) {
                prod = dataSourceRepository.findProductoByDesc(codigoProd);
            }
            if (prod == null) {
                message += "\n- El producto ingresado no existe";
            } else {
                lectura.setCodigoProducto(prod.getCodigo());
            }
        }
        if (configActual.isLote() && UtilMethods.isEmpty(lectura.getLote())) message += "\n- Debe ingresar el lote";
        if (configActual.isSerie() && UtilMethods.isEmpty(lectura.getSerie())) message += "\n- Debe ingresar la serie";

        // aca hay tanta gente antes que yo , gente que aprendio sin ia los verdaderos cracks

        // Development : Sting Lucana
        // Date: 04/03/2019
        // Reason: No se puede enciar un valor de más de 6 cifras como cantidad.
        /* if(lectura.getCantidad() > 999999){
                message += "\n- La cantidad no puede ser mayor a 6 cifras";
        }*/

        if (configActual.isSerie() && dataSourceRepository.findLecturaBySerie(configActual.getAlmacen().getIdInventario(), lectura.getSerie()) != null) {
            message += "\n- La serie ya ha sido registrada anteriormente";
        }
        
        if (lectura.getCantidad() == 0) message += "\n- Debe ingresar una cantidad mayor a 0";

        Matcher matcher = getPattern().matcher(String.valueOf(lectura.getCantidad()));
        if (!matcher.matches()) message += "\n- La cantidad no tiene el formato correcto";

        if (message.contains("\n")) {
            if (isViewAttached()) getView().showValidationError(message);
            return false;
        }
        return true;
    }

    @Override
    public void sendLecture(String ubicacion, String producto, String lote, String serie, String cantidad) {
        Configuracion configActual = preferenceManager.getConfig();
        if (configActual.getAlmacen() == null) return;

        Lectura lectura = new Lectura();
        lectura.setCodigoAlmacen(configActual.getAlmacen().getCodigo());
        lectura.setIdInventario(configActual.getAlmacen().getIdInventario());
        lectura.setCodigoUsuario(configActual.getAlmacen().getUsername());
        lectura.setCodigoUbicacion(ubicacion);
        lectura.setCodigoProducto(producto);
        lectura.setLote(lote);
        lectura.setSerie(serie);
        lectura.setCantidad(UtilMethods.parseDouble(cantidad));

        if (!validate(lectura)) return;

        if (configActual.isSolicitarConfirmacion()) {
            if (isViewAttached()) getView().showConfirmation(lectura);
        } else {
            executeRegistration(lectura);
        }
    }

    private void executeRegistration(Lectura lectura) {
        Configuracion configActual = preferenceManager.getConfig();
        disposables.add(
                dataSourceRepository.registerLectura(lectura)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(mensajeResponse -> {
                            if (isViewAttached()) getView().lecturaCreated();
                        }, e -> {
                            ApiError apiError = ApiError.parse(((HttpException) e));
                            if (apiError != null) {
                                if (configActual.isRegistrar()) {
                                    crearProducto(lectura);
                                } else if (apiError.isInventarioDone()) {
                                    nextInv = apiError.getNextInv();
                                    if (isViewAttached()) getView().displayInventarioDoneError(apiError.getMessage());
                                } else {
                                    if (isViewAttached()) getView().showErrorDialog(apiError.getMessage());
                                }
                            }
                        })
        );
    }

    @Override
    public void onConfirmationAccepted(Lectura lecture) {
        executeRegistration(lecture);
    }

    @Override
    public void validateUbicacion(String codUbicacion) {
        if (dataSourceRepository.findUbicacionByCode(codUbicacion) == null) {
            if (isViewAttached()) getView().showValidationError("La ubicación ingresada no existe");
        } else {
            Configuracion configActual = preferenceManager.getConfig();
            configActual.setUbicacion(codUbicacion);
            preferenceManager.saveConfig(configActual);
        }
    }

    @Override
    public void updateUbicacion(String codUbicacion) {
        Configuracion configActual = preferenceManager.getConfig();
        configActual.setUbicacion(codUbicacion);
        preferenceManager.saveConfig(configActual);
    }

    /*
    @Override
    public void searchProducto(String code) {
        if (UtilMethods.isEmpty(code)) {
            if (isViewAttached()) getView().showProductoInfo("", "");
            return;
        }
        disposables.add(Observable.fromCallable(() -> dataSourceRepository.findProductoByCode(code.trim()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(producto -> {
                    if (isViewAttached()) {
                        if (producto != null) getView().showProductoInfo(producto.getDescripcion(), producto.getUnidadMedida());
                        else getView().showProductoInfo("", "");
                    }
                }, throwable -> {
                    if (isViewAttached()) getView().showProductoInfo("", "");
                }));
    }*/


    @Override
    public void searchProducto(String valor) {

        if (UtilMethods.isEmpty(valor)) {
            if (isViewAttached()) {
                getView().showProductoInfo(null, true);
            }
            return;
        }

        disposables.add(
                Observable.fromCallable(() -> {
                            String texto = valor.trim();
                            Producto producto = dataSourceRepository.findProductoByCode(texto);
                            boolean matchedByCode = producto != null;
                            if (producto == null) {
                                producto = dataSourceRepository.findProductoByDesc(texto);
                            }

                            return new Pair<Producto, Boolean>(producto,matchedByCode);

                        })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                resultado -> {
                                    if (isViewAttached()) {
                                        getView().showProductoInfo(resultado.first, resultado.second);
                                    }
                                },
                                throwable -> {
                                    if (isViewAttached()) {
                                        getView().showProductoInfo(null,true);
                                    }
                                }
                        )
        );
    }



    @Override
    public void verifarFoco() {
        if (!preferenceManager.getConfig().getUbicacion().isEmpty()) {
            if (isViewAttached()) getView().setupFocoProducto();
        } else {
            if (isViewAttached()) getView().setupFocoUbicacion();
        }
    }

    @Override
    public void saveModoLote(boolean isLote) {
        Configuracion configActual = preferenceManager.getConfig();
        configActual.setLote(isLote);
        preferenceManager.saveConfig(configActual);
        if (isViewAttached()) getView().setupLote(isLote, configActual.isCameraScan());
    }

    @Override
    public void saveModoLecture(boolean isBarrido) {
        Configuracion configActual = preferenceManager.getConfig();
        configActual.setModo(isBarrido ? Constants.MODE_BARRIDO : Constants.MODE_MANUAL);
        preferenceManager.saveConfig(configActual);
        if (isViewAttached()) {
            getView().setupProducto(!isBarrido);
            getView().setupCantidad(isBarrido);
        }
    }

    @Override
    public Pattern getPattern() {
        return mPattern;
    }

    @Override
    public boolean getRFID() { return preferenceManager.getConfig().isRfd(); }

    @Override
    public boolean getCameraScan()  { return preferenceManager.getConfig().isCameraScan(); }

    @Override
    public boolean getNumberDecimal() { return preferenceManager.getConfig().isNumberDecimal(); }

    @Override
    public boolean isCalculadora() { return preferenceManager.getConfig().isCalculadora(); }

    @Override
    public synchronized void saveRFID(List<String> rfid, String cod) {
        Configuracion configActual = preferenceManager.getConfig();
        if (configActual.getAlmacen() == null) return;

        disposables.add(
                Observable.just(rfid)
                        .subscribeOn(Schedulers.io())
                        .observeOn(Schedulers.io())
                        .flatMap(Observable::fromIterable)
                        .map(UtilMethods::hexToString)
                        .filter(s -> cod.isEmpty() || s.contains(cod))
                        .flatMap(s -> dataSourceRepository.saveRFID(s, configActual.getAlmacen().getIdInventario(), configActual.getAlmacen().getConteo()))
                        .subscribe(aLong -> Timber.d("RFID Saved"), throwable -> {
                            if (isViewAttached()) getView().showError(throwable.getMessage());
                        })
        );
    }

    private void crearProducto(Lectura lectura) {
        HashMap<String, String> body = new HashMap<>();
        body.put("codigo", lectura.getCodigoProducto());
        disposables.add(
                dataSourceRepository.crearProducto(body)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(response -> executeRegistration(lectura), e -> {
                            if (isViewAttached()) getView().showError("Error al crear producto");
                        })
        );
    }

    @Override
    public void removeAllLecturas() {
        Almacen almacen = preferenceManager.getConfig().getAlmacen();
        if (almacen == null) return;
        disposables.add(dataSourceRepository.deleteAllLecturas(almacen.getIdInventario(), almacen.getUsername())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    disposables.add(dataSourceRepository.deleteAllLecturasLocal(almacen.getIdInventario(), almacen.getUsername())
                            .subscribeOn(Schedulers.io())
                            .subscribe());
                }, throwable -> {
                    if (isViewAttached()) getView().showError(throwable.getMessage());
                }));
    }

    @Override
    public void responderParticipacion(boolean participa) {
        Configuracion configActual = preferenceManager.getConfig();
        disposables.add(dataSourceRepository.responderParticipacion(configActual.getAlmacen().getIdInventario(), configActual.getAlmacen().getUsername(), participa)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    if (participa && isViewAttached()) getView().showWaitingForOthers();
                }, e -> {
                    if (isViewAttached()) getView().showError(e.getMessage());
                }));
    }

    private void fetchAssignedProducts() {
        Configuracion configActual = preferenceManager.getConfig();
        disposables.add(dataSourceRepository.listProductosAsignados(configActual.getAlmacen().getIdInventario(), configActual.getAlmacen().getUsername())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(assignedProducts -> {
                    if (isViewAttached()) getView().showAssignedProductsList(assignedProducts);
                }, e -> {
                    if (isViewAttached()) getView().showError(e.getMessage());
                }));
    }

    private void startTimer(String fchLimiteRespuesta) {
        if (fchLimiteRespuesta == null) return;
        timerDisposable.clear();
        Calendar calLimite = UtilMethods.stringToCalendar(fchLimiteRespuesta, Constants.BD_DATETIME_FORMAT);
        long limitTime = calLimite.getTimeInMillis();

        timerDisposable.add(Observable.interval(0, 1, TimeUnit.SECONDS)
                .map(tick -> (limitTime - System.currentTimeMillis()) / 1000)
                .takeUntil(seconds -> seconds <= 0)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(seconds -> {
                    if (isViewAttached()) {
                        if (seconds <= 0) getView().updateCountdown("00:00");
                        else getView().updateCountdown(String.format(Locale.getDefault(), "%02d:%02d", seconds / 60, seconds % 60));
                    }
                }, e -> Log.e(TAG, "Timer error", e)));
    }
}
