package pe.com.dms.inventariosoft.features.queries;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import pe.com.dms.inventariosoft.data.pojos.HijoConsulta;
import pe.com.dms.inventariosoft.databinding.ItemQueryRebuildBinding;

public class ItemConsultaAdapter extends RecyclerView.Adapter<ItemConsultaViewHolder> {

    private List<HijoConsulta> items;

    @NonNull
    @Override
    public ItemConsultaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemQueryRebuildBinding binding =
                ItemQueryRebuildBinding.inflate(inflater, parent, false);

        return new ItemConsultaViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemConsultaViewHolder holder, int position) {
        holder.bind(items.get(position));
    }

    public void setItems(List<HijoConsulta> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }
}
