package pe.com.dms.inventariosoft.features.queries;

import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.List;

import pe.com.dms.inventariosoft.R;
import pe.com.dms.inventariosoft.data.pojos.GroupConsulta;
import pe.com.dms.inventariosoft.data.pojos.HijoConsulta;
import pe.com.dms.inventariosoft.utils.expandablerecyclerview.src.main.java.com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;
import pe.com.dms.inventariosoft.utils.expandablerecyclerview.src.main.java.com.thoughtbot.expandablerecyclerview.viewholders.GroupViewHolder;

/**
 * Created by cwramirezg on 22/11/2017.
 */

public class HeaderConsultaViewHolder extends GroupViewHolder {

    private OnRemove listener = null;
    private TextView tvProducto;
    private TextView tvTotal;
    private int position;
    private String txt;
    private List<HijoConsulta> items;

    public HeaderConsultaViewHolder(View itemView, OnRemove listener) {
        super(itemView);
        this.listener = listener;
        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                listener.onRemovePadre(items);
                return true;
            }
        });
        tvProducto = itemView.findViewById(R.id.tv_producto);
        tvTotal = itemView.findViewById(R.id.tv_total);
    }

    public void setGenreTitle(ExpandableGroup group, int position) {
        String data = group.getTitle();
        GroupConsulta groupConsulta = new Gson().fromJson(data, GroupConsulta.class);
        items = group.getItems();
        tvProducto.setText(groupConsulta.getTvProducto());
        tvTotal.setText(groupConsulta.getTvtotal());
        txt = groupConsulta.getTvProducto();
        this.position = position;
    }

    public interface OnRemove {
        void onRemovePadre(List<HijoConsulta> items);
    }
}
