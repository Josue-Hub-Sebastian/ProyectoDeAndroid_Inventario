package pe.com.dms.inventariosoft.data.models
//nuevo_canela kotlin por que? pues por que no?
// en caso de una posible compatibilidad con versiones anteriores regresare a java pero realmente queria usar kotlin
import com.google.gson.annotations.SerializedName

data class EstadoInventarioResponse(
    @SerializedName("id_inventario") val idInventario: Int,
    @SerializedName("nroConteo") val nroConteo: Int,
    @SerializedName("estadoParticipacion") val estadoParticipacion: String,
    @SerializedName("fchLimiteRespuesta") val fchLimiteRespuesta: String?,
    @SerializedName("todosRespondieron") val todosRespondieron: Boolean,
    @SerializedName("inventarioFinalizado") val inventarioFinalizado: Int,
    @SerializedName("message") val message: String?,
    @SerializedName("tipoConteo") val tipoConteo: String?
)

// :c no se usa pero bueno
object TipoConteo {
    const val DIFERENCIAL = "DIFERENCIAL"
    const val REINICIO = "REINICIO"
}

// estados posibles
object EstadoParticipacion {
    const val NO_EVALUADO = "NO_EVALUADO"
    const val NO_HABILITADO = "NO_HABILITADO"
    const val PENDIENTE_RESPUESTA = "PENDIENTE_RESPUESTA"
    const val PARTICIPA = "PARTICIPA"
    const val NO_PARTICIPA = "NO_PARTICIPA"
}
