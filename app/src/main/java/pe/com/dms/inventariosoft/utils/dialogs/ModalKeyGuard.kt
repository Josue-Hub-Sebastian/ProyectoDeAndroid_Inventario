package pe.com.dms.inventariosoft.utils.dialogs

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.ContextWrapper
import android.content.DialogInterface
import android.content.Intent
import android.util.Log
import android.view.KeyEvent
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner

/**
 * ModalKeyGuard
 *
 * Clase reutilizable que bloquea los inputs físicos (botón gatillo del escáner láser Zebra
 * y teclas de volumen) cuando se muestra un diálogo modal (o AlertDialog).
 *
 * Mantiene intacta la funcionalidad del input táctil de la pantalla.
 *
 * Requerimientos cubiertos:
 * 1. Escáner Zebra (DataWedge): Envía broadcast com.symbol.datawedge.api.ACTION
 *    con extra com.symbol.datawedge.api.SCANNER_INPUT_PLUGIN ("DISABLE_PLUGIN" / "ENABLE_PLUGIN").
 * 2. Teclas de volumen / gatillo: Intercepta e ignora (consume, retorna true)
 *    KEYCODE_VOLUME_UP, KEYCODE_VOLUME_DOWN y botones del escáner.
 *    La tecla BACK sigue funcionando normalmente para cerrar el modal si es cancelable.
 * 3. Reactivación robusta: Se ejecuta en OnDismissListener y en el onDestroy de la Activity.
 * 4. Manejo de excepciones: Atrapa errores en sendBroadcast y los registra con Log.w para no
 *    afectar a smartphones o dispositivos que no sean Zebra.
 */
class ModalKeyGuard private constructor(
    private val dialog: Dialog,
    activityParam: Activity?
) {

    private val activity: Activity? = activityParam ?: findActivity(dialog.context)
    private var isScannerDisabled = false
    private var lifecycleObserver: DefaultLifecycleObserver? = null

    private var userDismissListener: DialogInterface.OnDismissListener? = null
    private var userShowListener: DialogInterface.OnShowListener? = null
    private var userKeyListener: DialogInterface.OnKeyListener? = null

    companion object {
        private const val TAG = "ModalKeyGuard"

        // Zebra DataWedge API Intent Constants
        private const val ACTION_DATAWEDGE = "com.symbol.datawedge.api.ACTION"
        private const val EXTRA_SCANNER_INPUT_PLUGIN = "com.symbol.datawedge.api.SCANNER_INPUT_PLUGIN"
        private const val PLUGIN_DISABLE = "DISABLE_PLUGIN"
        private const val PLUGIN_ENABLE = "ENABLE_PLUGIN"

        /**
         * Adjunta el protector de teclas e interacción con DataWedge a cualquier [Dialog].
         *
         * @param dialog Diálogo a proteger.
         * @param activity Actividad asociada opcional (se deduce del contexto si es nula).
         * @return Instancia de [ModalKeyGuard].
         */
        @JvmStatic
        @JvmOverloads
        fun attach(dialog: Dialog, activity: Activity? = null): ModalKeyGuard {
            val keyGuard = ModalKeyGuard(dialog, activity)
            keyGuard.setupGuard()
            return keyGuard
        }

        /**
         * Adjunta el protector de teclas e interacciones y muestra el diálogo.
         *
         * @param dialog Diálogo a proteger y mostrar.
         * @param activity Actividad asociada opcional.
         * @return Instancia de [ModalKeyGuard].
         */
        @JvmStatic
        @JvmOverloads
        fun show(dialog: Dialog, activity: Activity? = null): ModalKeyGuard {
            val guard = attach(dialog, activity)
            if (!dialog.isShowing) {
                dialog.show()
            }
            return guard
        }

        /**
         * Envia broadcast a DataWedge para activar o desactivar el plugin del scanner.
         * Atrapa cualquier excepción para evitar fallas en smartphones o dispositivos sin DataWedge.
         */
        @JvmStatic
        fun setScannerState(context: Context?, enable: Boolean) {
            if (context == null) {
                Log.w(TAG, "setScannerState: Contexto nulo, no se envió comando DataWedge.")
                return
            }

            val command = if (enable) PLUGIN_ENABLE else PLUGIN_DISABLE
            try {
                val intent = Intent(ACTION_DATAWEDGE).apply {
                    putExtra(EXTRA_SCANNER_INPUT_PLUGIN, command)
                }
                context.sendBroadcast(intent)
                Log.d(TAG, "Broadcast enviado a DataWedge: $command")
            } catch (e: Exception) {
                Log.w(TAG, "No se pudo comunicar con DataWedge ($command): ${e.message}", e)
            }
        }

        @JvmStatic
        fun disableScanner(context: Context?) {
            setScannerState(context, false)
        }

        @JvmStatic
        fun enableScanner(context: Context?) {
            setScannerState(context, true)
        }

        private fun findActivity(context: Context?): Activity? {
            var ctx = context
            while (ctx is ContextWrapper) {
                if (ctx is Activity) {
                    return ctx
                }
                ctx = ctx.baseContext
            }
            return null
        }
    }

    /**
     * Define un OnDismissListener personalizado adicional.
     */
    fun setOnDismissListener(listener: DialogInterface.OnDismissListener): ModalKeyGuard {
        this.userDismissListener = listener
        return this
    }

    /**
     * Define un OnShowListener personalizado adicional.
     */
    fun setOnShowListener(listener: DialogInterface.OnShowListener): ModalKeyGuard {
        this.userShowListener = listener
        return this
    }

    /**
     * Define un OnKeyListener personalizado adicional.
     */
    fun setOnKeyListener(listener: DialogInterface.OnKeyListener): ModalKeyGuard {
        this.userKeyListener = listener
        return this
    }

    private fun setupGuard() {
        // 1. Intercepción de teclas físicas en el diálogo
        dialog.setOnKeyListener { dialogInterface, keyCode, event ->
            val isPhysicalInputToBlock = when (keyCode) {
                KeyEvent.KEYCODE_VOLUME_UP,
                KeyEvent.KEYCODE_VOLUME_DOWN,
                KeyEvent.KEYCODE_BUTTON_L1,
                KeyEvent.KEYCODE_BUTTON_R1,
                102, 103, 293, 294 -> true // Códigos de teclas físicas de disparo/scanner en PDAs (293 = KEYCODE_SCANNER)
                else -> false
            }

            if (isPhysicalInputToBlock) {
                Log.d(TAG, "Bloqueado input físico: keyCode=$keyCode")
                true // Consumir evento (no propagar al sistema ni cambiar volumen/escanear)
            } else if (keyCode == KeyEvent.KEYCODE_BACK) {
                // Permitir tecla BACK para cerrar el modal si corresponde
                userKeyListener?.onKey(dialogInterface, keyCode, event) ?: false
            } else {
                userKeyListener?.onKey(dialogInterface, keyCode, event) ?: false
            }
        }

        // 2. Apagar scanner al mostrar el diálogo
        dialog.setOnShowListener { dialogInterface ->
            disableScannerInternal()
            userShowListener?.onShow(dialogInterface)
        }

        // 3. Encender scanner al ocultar / descartar el diálogo
        dialog.setOnDismissListener { dialogInterface ->
            enableScannerInternal()
            userDismissListener?.onDismiss(dialogInterface)
        }

        // 4. Si el diálogo ya está visible en el momento de attach(), desactivar scanner de inmediato
        if (dialog.isShowing) {
            disableScannerInternal()
        }

        // 5. Monitorear el ciclo de vida de la Activity para asegurar la reactivación en onDestroy
        registerLifecycleObserver()
    }

    private fun registerLifecycleObserver() {
        val targetActivity = activity
        if (targetActivity is LifecycleOwner) {
            val observer = object : DefaultLifecycleObserver {
                override fun onDestroy(owner: LifecycleOwner) {
                    Log.d(TAG, "Activity onDestroy detectado. Restaurando scanner por seguridad.")
                    enableScannerInternal()
                    targetActivity.lifecycle.removeObserver(this)
                }
            }
            this.lifecycleObserver = observer
            targetActivity.lifecycle.addObserver(observer)
        }
    }

    private fun disableScannerInternal() {
        if (!isScannerDisabled) {
            setScannerState(dialog.context, false)
            isScannerDisabled = true
        }
    }

    private fun enableScannerInternal() {
        if (isScannerDisabled) {
            setScannerState(dialog.context, true)
            isScannerDisabled = false
        }
        // Limpiar el observador si estaba registrado
        lifecycleObserver?.let { observer ->
            if (activity is LifecycleOwner) {
                activity.lifecycle.removeObserver(observer)
            }
            lifecycleObserver = null
        }
    }
}
