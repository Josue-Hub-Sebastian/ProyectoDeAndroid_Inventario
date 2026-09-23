package pe.com.dms.inventariosoft.features.queries;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import pe.com.dms.inventariosoft.data.pojos.HijoConsulta;
import pe.com.dms.inventariosoft.databinding.ItemQueryRebuildBinding;

class ItemConsultaViewHolder extends RecyclerView.ViewHolder {

    private final ItemQueryRebuildBinding binding;

    public ItemConsultaViewHolder(@NonNull ItemQueryRebuildBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public void bind(HijoConsulta item) {

        binding.tvDescripcion.setText(item.getDescProducto());
        binding.tvCodigo.setText("Código: " + item.getCodigoProducto());
        binding.tvUbicacion.setText("Ubicación: " + item.getCodigoUbicacion());
        binding.tvCantidad.setText(item.getCantidadString());
        binding.tvUsuario.setText("Leido por: " + item.getCodigoUsuario());
    }
}
