package pe.com.dms.inventariosoft.data.models

import com.google.gson.annotations.SerializedName
// Segun como respondan los operadores se les asignara un ESTADO de participacion
data class OperadorEstado(
    @SerializedName("cod_Usuario") val codUsuario: String, // codUsuario / cod_Usuario
    @SerializedName("nombre") val nombre: String,
    @SerializedName("estado") val estado: String // PARTICIPA, NO_PARTICIPA, PENDIENTE_RESPUESTA
)
