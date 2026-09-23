package pe.com.dms.inventariosoft.features.queries;

import android.view.View;
import android.widget.TextView;


import butterknife.BindView;
import butterknife.ButterKnife;
import pe.com.dms.inventariosoft.R;
import pe.com.dms.inventariosoft.data.pojos.HijoConsulta;
import pe.com.dms.inventariosoft.utils.UtilMethods;
import pe.com.dms.inventariosoft.utils.expandablerecyclerview.src.main.java.com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder;

/**
 * Created by cwramirezg on 22/11/2017.
 */

public class ChildConsultaViewHolder extends ChildViewHolder {

    @BindView(R.id.tv_codigo)
    TextView tvCodigo;
    @BindView(R.id.tv_descripcion)
    TextView tvDescripcion;
    @BindView(R.id.ly_delete)
    View lyDelete;
    @BindView(R.id.tv_cantidad)
    TextView tvCantidad;
    @BindView(R.id.ubicacion)
    TextView ubicacion;
    @BindView(R.id.lote)
    TextView lblLote;
    @BindView(R.id.serie)
    TextView lblSerie;
    @BindView(R.id.tv_ubicacion)
    TextView tvUbicacion;
    @BindView(R.id.tv_lote)
    TextView tvLote;
    @BindView(R.id.tv_serie)
    TextView tvSerie;
    private OnRemove listener = null;


    public ChildConsultaViewHolder(View itemView, OnRemove listener) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        this.listener = listener;
    }

    public void onBind(HijoConsulta obj) {

        tvCodigo.setText(obj.getCodigoProducto());
        tvDescripcion.setText(obj.getDescProducto());

        tvCantidad.setText(obj.getCantidadString());

        tvUbicacion.setText(obj.getCodigoUbicacion());

        String lote = obj.getSerie();
        lblLote.setVisibility(UtilMethods.getVisibility(!UtilMethods.isEmpty(lote)));
        tvLote.setVisibility(UtilMethods.getVisibility(!UtilMethods.isEmpty(lote)));
        tvLote.setText(lote);

        String serie = obj.getSerie();
        lblSerie.setVisibility(UtilMethods.getVisibility(!UtilMethods.isEmpty(serie)));
        tvSerie.setVisibility(UtilMethods.getVisibility(!UtilMethods.isEmpty(serie)));
        tvSerie.setText(serie);

        lyDelete.setOnClickListener(view -> {
            listener.onRemoveHijo(obj.getId(), obj.getPosition());
        });

    }

    public interface OnRemove {
        void onRemoveHijo(int id, int positiom);
    }
}
