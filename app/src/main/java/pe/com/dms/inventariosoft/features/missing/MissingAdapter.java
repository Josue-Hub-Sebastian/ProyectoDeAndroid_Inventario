package pe.com.dms.inventariosoft.features.missing;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import pe.com.dms.inventariosoft.R;
import pe.com.dms.inventariosoft.data.models.Lectura;
import pe.com.dms.inventariosoft.utils.UtilMethods;

public class MissingAdapter extends RecyclerView.Adapter<MissingAdapter.ViewHolder> {

    protected List<Lectura> objList;
    private ISelection iSelection;

    public MissingAdapter(List<Lectura> objList, ISelection iSelection) {
        this.objList = objList;
        this.iSelection = iSelection;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_lecture, parent, false);
        ViewHolder vh = new ViewHolder(v);
        vh.getAdapterPosition();
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Lectura obj = objList.get(position);

        if (UtilMethods.isEmpty(obj.getDescProducto())) {
            holder.tvDescripcion.setText(obj.getCodigoProducto());
            holder.tvCodigo.setText("Nuevo Producto");
        } else {
            holder.tvDescripcion.setText(obj.getDescProducto());
            holder.tvCodigo.setText(obj.getCodigoProducto());
        }

        holder.tvUbicacion.setText(obj.getCodigoUbicacion());

        holder.tvCantidad.setText(obj.getCantidadString());

        String lote = obj.getSerie();
        holder.lote.setVisibility(UtilMethods.getVisibility(!UtilMethods.isEmpty(lote)));
        holder.tvLote.setVisibility(UtilMethods.getVisibility(!UtilMethods.isEmpty(lote)));
        holder.tvLote.setText(lote);

        String serie = obj.getSerie();
        holder.serie.setVisibility(UtilMethods.getVisibility(!UtilMethods.isEmpty(serie)));
        holder.tvSerie.setVisibility(UtilMethods.getVisibility(!UtilMethods.isEmpty(serie)));
        holder.tvSerie.setText(serie);

        holder.holder.setOnClickListener(v -> {
            iSelection.onSelect(obj);
        });

    }

    @Override
    public int getItemCount() {
        return objList.size();
    }

    interface ISelection {
        void onSelect(Lectura almacen);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.holder)
        View holder;
        @BindView(R.id.tv_codigo)
        TextView tvCodigo;
        @BindView(R.id.tv_descripcion)
        TextView tvDescripcion;
        @BindView(R.id.tv_cantidad)
        TextView tvCantidad;

        @BindView(R.id.ubicacion)
        TextView ubicacion;
        @BindView(R.id.lote)
        TextView lote;
        @BindView(R.id.serie)
        TextView serie;
        @BindView(R.id.tv_ubicacion)
        TextView tvUbicacion;
        @BindView(R.id.tv_lote)
        TextView tvLote;
        @BindView(R.id.tv_serie)
        TextView tvSerie;

        ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }
    }

}


