package pe.com.dms.inventariosoft.data.models
// CANELAZA
import com.google.gson.annotations.SerializedName

data class ProductoAsignado(
    @SerializedName("idProducto") val idProducto: String,
    @SerializedName("codProducto") val codProducto: String,
    @SerializedName("descripcion") val descripcion: String,
    @SerializedName("ubicacionBase") val ubicacionBase: String,
    @SerializedName("estado") val estado: String,
    @SerializedName("cantidadContada") val cantidadContada: Double,
    @SerializedName("unidadMedida") val unidadMedida: String?
)
// nuevo estado de asignacion
object EstadoAsignacion {
    const val PENDIENTE = "PENDIENTE"
    const val EN_PROCESO = "EN_PROCESO"
    const val COMPLETADA = "COMPLETADA"
}
