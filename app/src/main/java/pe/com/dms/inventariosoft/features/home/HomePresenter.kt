package pe.com.dms.inventariosoft.features.home

import android.util.Log
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import pe.com.dms.inventariosoft.data.PreferenceManager
import pe.com.dms.inventariosoft.data.models.EstadoInventarioResponse
import pe.com.dms.inventariosoft.data.models.EstadoParticipacion
import pe.com.dms.inventariosoft.data.source.DataSourceRepository
import pe.com.dms.inventariosoft.features.shared.BasePresenter
import pe.com.dms.inventariosoft.utils.Constants
import pe.com.dms.inventariosoft.utils.UtilMethods
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class HomePresenter @Inject constructor(
    val preferenceManager: PreferenceManager,
    private val dataSourceRepository: DataSourceRepository
) : BasePresenter<HomeContract.View>(), HomeContract.Presenter {

    private val disposables = CompositeDisposable()
    private val timerDisposable = CompositeDisposable()
    private var currentFchLimite: String? = null
    private val TAG = "HomePresenter"

    override fun onViewCreated() {
        startStatusPolling()
    }

    private fun startStatusPolling() {
        disposables.add(
            Observable.interval(0, 15, TimeUnit.SECONDS)
                .flatMap {
                    val configActual = preferenceManager.config
                    val almacen = configActual.almacen
                    
                    if (almacen == null) {
                        Observable.empty<EstadoInventarioResponse>()
                    } else {
                        dataSourceRepository.getEstadoInventario(
                            almacen.idInventario,
                            almacen.username
                        ).subscribeOn(Schedulers.io())
                            .onErrorResumeNext(Observable.empty<EstadoInventarioResponse>())
                    }
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ response ->
                    val configActual = preferenceManager.config
                    
                    // Si el inventario termino, limpiamos todo y refrescamos menu
                    if (response.inventarioFinalizado == 1 && configActual.almacen != null) {
                        configActual.almacen = null
                        configActual.ubicacion = ""
                        preferenceManager.saveConfig(configActual)
                        view?.refreshMenu()
                        view?.showError("El inventario ha finalizado por completo.")
                        view?.showDefaultInfo()
                        return@subscribe
                    }

                    if (response.idInventario != 0 && response.idInventario != configActual.almacen?.idInventario) {
                        configActual.almacen.idInventario = response.idInventario
                        preferenceManager.saveConfig(configActual)
                    }

                    when (response.estadoParticipacion) {
                        EstadoParticipacion.PENDIENTE_RESPUESTA -> {
                            view?.setRegistrarAccess(false) // Bloqueamos mientras decide
                            //view?.showInvitation(response.nroConteo)
                            view?.showInvitation(response.nroConteo, response.tipoConteo)
                            startTimer(response.fchLimiteRespuesta)
                        }
                        EstadoParticipacion.PARTICIPA -> {
                            if (response.todosRespondieron) {
                                view?.setRegistrarAccess(true) // Habilitamos acceso
                                updateAssignedProductsReactive()
                            } else {
                                view?.setRegistrarAccess(false) // Bloqueamos mientras otros responden
                                view?.showWaiting()
                                fetchOperatorStatus()
                                startTimer(response.fchLimiteRespuesta)
                            }
                        }
                        EstadoParticipacion.NO_PARTICIPA -> {
                            // SEGURIDAD: Si marco que no participa, lo sacamos del inventario
                            if (configActual.almacen != null) {
                                configActual.almacen = null
                                configActual.ubicacion = ""
                                preferenceManager.saveConfig(configActual)
                                view?.refreshMenu()
                                view?.showError("Has marcado que NO participas en este conteo diferencial.")
                                view?.showDefaultInfo()
                            }
                        }
                        else -> {
                            // En conteo 1 o estados iniciales, habilitamos por defecto
                            val configActual = preferenceManager.config
                            if (configActual.almacen != null && configActual.almacen.conteo == 1) {
                                view?.setRegistrarAccess(true)
                            }
                            view?.showDefaultInfo()
                        }
                    }
                }, { e ->
                    Log.e(TAG, "Error polling status", e)
                })
        )
    }

    /**
     * Consulta quiénes están participando y quiénes faltan responder.
     * Se ejecuta de forma asíncrona cada vez que el polling detecta que estamos esperando.
     */
    private fun fetchOperatorStatus() {
        val configActual = preferenceManager.config
        disposables.add(
            dataSourceRepository.listEstadoOperadores(configActual.almacen.idInventario)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ operators ->
                    view?.updateOperatorStatus(operators)
                }, { e ->
                    Log.e(TAG, "Error obteniendo estado de operadores", e)
                })
        )
    }

    /**
     * Carga los productos y aplica el filtro para ocultar los completados
     * manteniendo el conteo de progreso actualizado.
     */
    private fun updateAssignedProductsReactive() {
        val configActual = preferenceManager.config
        disposables.add(
            dataSourceRepository.listProductosAsignados(
                configActual.almacen.idInventario,
                configActual.almacen.username
            ).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ allProducts ->
                    val total = allProducts.size
                    val completed = allProducts.count { it.estado == "COMPLETADA" }
                    
                    // FILTRO: Solo enviamos a la vista los productos que NO estén completados
                    val pendingProducts = allProducts.filter { it.estado != "COMPLETADA" }
                    
                    val progress = String.format("%02d/%02d", completed, total)
                    
                    Log.d(TAG, "Reactivo - Pendientes: ${pendingProducts.size}, Progreso: $progress")
                    view?.showAssignedList(pendingProducts, progress)
                }, { e ->
                    Log.e(TAG, "Error actualizando lista reactiva", e)
                })
        )
    }

    private fun fetchAssignedProducts() {
        // Redirigimos a la nueva lógica reactiva
        updateAssignedProductsReactive()
    }

    private fun startTimer(fchLimite: String?) {
        if (fchLimite == null) {
            timerDisposable.clear()
            currentFchLimite = null
            return
        }

        //if(fchLimite == currentFchLimite uwuwuwuwu)

        if (fchLimite == currentFchLimite) return
        currentFchLimite = fchLimite

        timerDisposable.clear()

        val calLimite = UtilMethods.stringToCalendar(fchLimite, Constants.BD_DATETIME_FORMAT)
        val limitTime = calLimite.timeInMillis

        timerDisposable.add(
            Observable.interval(0, 1, TimeUnit.SECONDS)
                .map { (limitTime - System.currentTimeMillis()) / 1000 }
                .takeUntil { it <= 0 }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ seconds ->
                    if (seconds <= 0) {
                        view?.updateTimer("00:00")
                        // Quitamos el auto-rechazo desde el cliente. 
                        // El servidor lo resolverá en el proximo polling (on-the-fly).
                    } else {
                        val min = seconds / 60
                        val sec = seconds % 60
                        view?.updateTimer(String.format(Locale.getDefault(), "%02d:%02d", min, sec))
                    }
                }, { e -> Log.e(TAG, "Timer error", e) })
        )
    }

    override fun responderParticipacion(participa: Boolean) {
        val configActual = preferenceManager.config
        disposables.add(
            dataSourceRepository.responderParticipacion(
                configActual.almacen.idInventario,
                configActual.almacen.username,
                participa
            ).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    if (participa) {
                        view?.showWaiting()
                    } else {
                        // Limpiamos la configuracion local para "desloguear" del inventario
                        val config = preferenceManager.config
                        config.almacen = null
                        config.ubicacion = ""
                        preferenceManager.saveConfig(config)
                        view?.refreshMenu()
                        view?.showDefaultInfo()
                    }
                }, { e ->
                    view?.showError(e.message ?: "Error al responder")
                })
        )
    }

    override fun onDetach() {
        disposables.clear()
        timerDisposable.clear()
        currentFchLimite = null
    }
}
