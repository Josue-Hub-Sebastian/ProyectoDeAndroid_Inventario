package pe.com.dms.inventariosoft.features.config.almacen;

import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import pe.com.dms.inventariosoft.R;
import pe.com.dms.inventariosoft.data.models.Almacen;

public class AlmacenSearchAdapter extends RecyclerView.Adapter<AlmacenSearchAdapter.ViewHolder> {

    protected List<Almacen> almacenList;
    private ISelection iSelection;

    public AlmacenSearchAdapter(List<Almacen> almacenList, ISelection iSelection) {
        this.almacenList = almacenList;
        this.iSelection = iSelection;
    }

    public void updateList(List<Almacen> almacenList, String filter) {
        filter = filter.toUpperCase();

        if (TextUtils.isEmpty(filter)) {
            this.almacenList = almacenList;
            notifyDataSetChanged();
        } else {
            List<Almacen> temp = new ArrayList<>();
            for (Almacen almacen : almacenList) {
                if (almacen.getCodigo().toUpperCase().contains(filter) ||
                        almacen.getDescripcion().toUpperCase().contains(filter)) {

                    temp.add(almacen);
                }
            }
            this.almacenList = temp;
            notifyDataSetChanged();
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_almacen_search, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Almacen almacen = almacenList.get(position);

        holder.tv_code.setText(almacen.getCodigo());
        holder.tv_name.setText(almacen.getDescripcion());

        holder.holder.setOnClickListener(view -> iSelection.onSelect(almacen));
    }

    @Override
    public int getItemCount() {
        return almacenList.size();
    }

    interface ISelection {
        void onSelect(Almacen almacen);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_code, tv_name;
        View holder;

        ViewHolder(View v) {
            super(v);
            tv_code = v.findViewById(R.id.tv_label_1);
            tv_name = v.findViewById(R.id.tv_label_2);

            holder = v.findViewById(R.id.holder);
        }
    }

}


