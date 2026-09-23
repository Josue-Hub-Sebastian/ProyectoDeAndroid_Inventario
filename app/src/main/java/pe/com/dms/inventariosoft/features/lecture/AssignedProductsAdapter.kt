package pe.com.dms.inventariosoft.features.lecture

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import pe.com.dms.inventariosoft.R
import pe.com.dms.inventariosoft.data.models.EstadoAsignacion
import pe.com.dms.inventariosoft.data.models.ProductoAsignado

class AssignedProductsAdapter(
    private var items: List<ProductoAsignado>,
    private val onItemClick: (ProductoAsignado) -> Unit
) : RecyclerView.Adapter<AssignedProductsAdapter.ViewHolder>() {

    fun updateData(newItems: List<ProductoAsignado>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.item_home_assigned,
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
        private val tvDescripcion: TextView = view.findViewById(R.id.tv_descripcion)
        private val tvCodigo: TextView = view.findViewById(R.id.tv_codigo)
        private val tvUbicacion: TextView = view.findViewById(R.id.tv_ubicacion)
        private val ivStatus: ImageView = view.findViewById(R.id.iv_status)

        fun bind(item: ProductoAsignado) {
            tvDescripcion.text = item.descripcion
            tvCodigo.text = "Código: ${item.codProducto}"
            tvUbicacion.text = "Ubicación: ${item.ubicacionBase}"
            
            when (item.estado) {
                EstadoAsignacion.COMPLETADA -> {
                    ivStatus.setImageResource(android.R.drawable.checkbox_on_background)
                    ivStatus.visibility = View.VISIBLE
                }
                EstadoAsignacion.EN_PROCESO -> {
                    ivStatus.setImageResource(android.R.drawable.ic_menu_edit)
                    ivStatus.visibility = View.VISIBLE
                }
                else -> {
                    ivStatus.visibility = View.GONE
                }
            }

            // Segun requerimiento: Las cards son solo informativas, no reactivas/seleccionables.
            itemView.setOnClickListener(null)
            itemView.isClickable = false
        }
    }
}
