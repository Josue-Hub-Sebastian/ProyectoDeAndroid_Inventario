package pe.com.dms.inventariosoft.features.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import pe.com.dms.inventariosoft.R
import pe.com.dms.inventariosoft.data.models.EstadoParticipacion
import pe.com.dms.inventariosoft.data.models.OperadorEstado

class OperatorStatusAdapter(
    private var items: List<OperadorEstado>
) : RecyclerView.Adapter<OperatorStatusAdapter.ViewHolder>() {

    fun updateData(newItems: List<OperadorEstado>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.item_operator_status,
            parent,
            false
        )
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val tvNombre: TextView = view.findViewById(R.id.tv_operador_nombre)// MODIFICACION_EWE TRAERAS AHORA EL CODIGO DE USUARIO Y NO EL NOMBRE DEL OPERADOR SOLO DEBERAS MODIFICAR EL SP COMO ME ENVIA UNICAMENTE  :V
        private val ivStatus: ImageView = view.findViewById(R.id.iv_status_icon)

        fun bind(item: OperadorEstado) {
            tvNombre.text = item.nombre
            
            when (item.estado) {
                EstadoParticipacion.PARTICIPA -> {
                    ivStatus.setImageResource(R.drawable.ic_check)
                    ivStatus.setColorFilter(ContextCompat.getColor(itemView.context, R.color.green))
                    ivStatus.alpha = 1.0f
                }
                EstadoParticipacion.NO_PARTICIPA -> {
                    ivStatus.setImageResource(R.drawable.ic_close)
                    ivStatus.setColorFilter(ContextCompat.getColor(itemView.context, R.color.red))
                    ivStatus.alpha = 1.0f
                }
                EstadoParticipacion.PENDIENTE_RESPUESTA -> {
                    ivStatus.setImageResource(R.drawable.ic_alert)
                    ivStatus.setColorFilter(ContextCompat.getColor(itemView.context, android.R.color.darker_gray))
                    ivStatus.alpha = 0.5f
                }
                else -> {
                    ivStatus.setImageResource(R.drawable.ic_alert)
                    ivStatus.setColorFilter(ContextCompat.getColor(itemView.context, android.R.color.darker_gray))
                    ivStatus.alpha = 0.3f
                }
            }
        }
    }
}
