package pe.com.dms.inventariosoft.features.lecture.list;

import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import pe.com.dms.inventariosoft.R;
import pe.com.dms.inventariosoft.data.pojos.LecturaRFID;

public class LectureEpcListAdapter extends RecyclerView.Adapter<LectureEpcListAdapter.ViewHolder> {
    String TAG = LectureEpcListAdapter.class.getSimpleName();

    private List<LecturaRFID> objList;
    private ISelection iSelection;

    public LectureEpcListAdapter(List<LecturaRFID> objList, ISelection iSelection) {
        this.objList = objList;
        this.iSelection = iSelection;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_lecture_epc_swipe, parent, false);
        ViewHolder vh = new ViewHolder(v);
        vh.getAdapterPosition();
        return vh;
    }

    public List<LecturaRFID> getLecturas() {
        return objList;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        LecturaRFID obj = objList.get(position);
        Log.e(TAG, "onBindViewHolder obj: " + obj);

        holder.tvCodigo.setText(obj.getCodigo());
        holder.tvDescripcion.setText(obj.getDescripcion());
        holder.tvCantidad.setText(String.valueOf(obj.getCantidad()));

        holder.lyDelete.setOnClickListener(view -> {
            iSelection.onSelect(obj);
        });
    }

    @Override
    public int getItemCount() {
        return objList.size();
    }

    public void addItems(List<LecturaRFID> newItems) {
        int startPos = objList.size();
        objList.addAll(newItems);
        notifyItemRangeInserted(startPos, newItems.size());
    }

    interface ISelection {
        void onSelect(LecturaRFID lecturaRFID);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_codigo)
        TextView tvCodigo;

        @BindView(R.id.tv_descripcion)
        TextView tvDescripcion;
        @BindView(R.id.tv_cantidad)
        TextView tvCantidad;
        @BindView(R.id.ly_delete)
        View lyDelete;

        ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }
    }

}


