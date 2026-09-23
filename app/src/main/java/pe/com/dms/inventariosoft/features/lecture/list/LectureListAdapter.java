package pe.com.dms.inventariosoft.features.lecture.list;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.chip.Chip;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import pe.com.dms.inventariosoft.R;
import pe.com.dms.inventariosoft.data.models.Lectura;
import pe.com.dms.inventariosoft.utils.UtilMethods;

public class LectureListAdapter extends RecyclerView.Adapter<LectureListAdapter.ViewHolder> {

    protected List<Lectura> objList;
    private ISelection iSelection;

    public LectureListAdapter(List<Lectura> objList, ISelection iSelection) {
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
            holder.tvCodigo.setText("Codigo: Nuevo Producto");
        } else {
            holder.tvDescripcion.setText(obj.getDescProducto());
            holder.tvCodigo.setText("Codigo: "+obj.getCodigoProducto());
        }

        holder.tvCantidad.setText(obj.getCantidadString());

        holder.tvUbicacion.setText("Ubicación: "+obj.getCodigoUbicacion());

        String lote = obj.getSerie();
//        holder.lote.setVisibility(UtilMethods.getVisibility(!UtilMethods.isEmpty(lote)));
//        holder.tvLote.setVisibility(UtilMethods.getVisibility(!UtilMethods.isEmpty(lote)));
//        holder.tvLote.setText(lote);

        String serie = obj.getSerie();
//        holder.serie.setVisibility(UtilMethods.getVisibility(!UtilMethods.isEmpty(serie)));
//        holder.tvSerie.setVisibility(UtilMethods.getVisibility(!UtilMethods.isEmpty(serie)));
//        holder.tvSerie.setText(serie);

        holder.btnDelete.setOnClickListener(view -> {
            iSelection.onSelect(obj);
        });
    }

    @Override
    public int getItemCount() {
        return objList.size();
    }

    public void addItems(List<Lectura> newItems) {
        int startPos = objList.size();
        objList.addAll(newItems);
        notifyItemRangeInserted(startPos, newItems.size());
    }

    interface ISelection {
        void onSelect(Lectura almacen);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_codigo)
        Chip tvCodigo;
        @BindView(R.id.tv_descripcion)
        TextView tvDescripcion;
//        @BindView(R.id.ly_delete)
//        View lyDelete;
        @BindView(R.id.tv_cantidad)
        TextView tvCantidad;

        @BindView(R.id.btn_delete)
        AppCompatImageView btnDelete;

//        @BindView(R.id.ubicacion)
//        TextView ubicacion;
//        @BindView(R.id.lote)
//        TextView lote;
//        @BindView(R.id.serie)
//        TextView serie;
        @BindView(R.id.tv_ubicacion)
        Chip tvUbicacion;
//        @BindView(R.id.tv_lote)
//        TextView tvLote;
//        @BindView(R.id.tv_serie)
//        TextView tvSerie;

        ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }
    }

}


