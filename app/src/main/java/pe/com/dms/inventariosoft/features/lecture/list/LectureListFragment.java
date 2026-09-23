package pe.com.dms.inventariosoft.features.lecture.list;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import pe.com.dms.inventariosoft.R;
import pe.com.dms.inventariosoft.data.models.Lectura;
import pe.com.dms.inventariosoft.data.pojos.LecturaRFID;
import pe.com.dms.inventariosoft.features.lecture.IBottomSheet;
import pe.com.dms.inventariosoft.features.lecture.LectureFragment;
import pe.com.dms.inventariosoft.features.shared.BaseFragment;
import pe.com.dms.inventariosoft.utils.SimpleDividerItemDecoration;
import pe.com.dms.inventariosoft.utils.UtilMethods;
import pe.com.dms.inventariosoft.utils.dialogs.CustomDialog;



public class LectureListFragment extends BaseFragment implements LectureListContract.View {
    String TAG = LectureListFragment.class.getSimpleName();

    @BindView(R.id.rv_data)
    RecyclerView rvData;
    @BindView(R.id.enviar)
    Button enviar;
    @BindView(R.id.sw_filtro)
    public SwitchMaterial swFiltro;
    @BindView(R.id.et_search)
    EditText etSearch;
    @BindView(R.id.txt_pagina)
    TextView txtPagina;

    @BindView(R.id.layout_empty)
    ConstraintLayout layoutEmpty;

    @Inject
    LectureListPresenter presenter;
    private Unbinder unbinder;
    private String idInventario;
    private Boolean optionOnMenu = false;
    private List<Lectura> listLecturas = new ArrayList<>();
    private List<LecturaRFID> listLecturasRFID = new ArrayList<>();

    public LectureListFragment() {
        // Required empty public constructor
    }

    public static LectureListFragment newInstance(Boolean optionOnMenu) {
        LectureListFragment fragment = new LectureListFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean("optionOnMenu", optionOnMenu);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            optionOnMenu = getArguments().getBoolean("optionOnMenu");
        }
        getActivityComponent().inject(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                swFiltro.setChecked(true);            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_bottom_sheet, container, false);
        unbinder = ButterKnife.bind(this, view);
        presenter.attachView(this);
        return view;
    }



    @Override
    protected void setupViews(View view) {
        super.setupViews(view);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        rvData.setLayoutManager(layoutManager);
        rvData.addItemDecoration(new SimpleDividerItemDecoration(getContext(), R.drawable.line_divider_white));
        swFiltro.setChecked(true);
        refreshList(true);
        if (!presenter.isRFID() ||  optionOnMenu) {
            swFiltro.setVisibility(View.GONE);
            enviar.setVisibility(View.GONE);
        }

        swFiltro.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // do something, the isChecked will be
                // true if the switch is in the On position
                refreshList(isChecked);

            }
        });

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                presenter.search(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    @OnClick({R.id.btn_anterior, R.id.btn_siguiente})
    public void onPaginationClick(View view) {
        int id = view.getId();
        if (id == R.id.btn_anterior) {
            presenter.loadPreviousPage();
        } else if (id == R.id.btn_siguiente) {
            presenter.loadNextPage();
        }
    }

    @OnClick(R.id.enviar)
    public void onViewClicked() {
        if (idInventario == null || idInventario.isEmpty()) {
            UtilMethods.showToast("Campo ubicación es obligatoria");
        } else {
            presenter.saveRFI(idInventario, swFiltro.isChecked());
        }
    }

    @Override
    public void refreshList(Boolean checkedFiltro) {
        Log.d("aqui", "oye");
        presenter.loadLastLectures(checkedFiltro);
    }

    @Override
    public void getIdInventario(String idInventario) {
        this.idInventario = idInventario;
    }

    @Override
    public void displayLectures(List<Lectura> lecturas) {
        this.listLecturas = new ArrayList<>(lecturas);
        layoutEmpty.setVisibility(lecturas.isEmpty() ? View.VISIBLE : View.GONE);
        LectureListAdapter adapter = new LectureListAdapter(
                lecturas,
                lectura -> new CustomDialog.Builder(getContext())
                        .setTitle("Alerta")
                        .setMessage("¿Estas seguro de eliminar el registro?")
                        .setPositiveButtonLabel(getString(R.string.label_yes))
                        .setNegativeButtonLabel(getString(R.string.label_no))
                        .setIcon(R.drawable.ic_alert)
                        .setTheme(R.style.AppTheme_Dialog_Warning)
                        .setPositiveButtonlistener(() -> {
                            presenter.deleteLecture(lectura.getId());
                        })
                        .build()
                        .show()
        );
        rvData.setAdapter(adapter);
    }

    @Override
    public void displayLecturesRFID(List<LecturaRFID> lecturas) {
        this.listLecturasRFID = new ArrayList<>(lecturas);
        updateAdapterRFID(lecturas);
    }

    private void updateAdapterRFID(List<LecturaRFID> lecturas) {
        layoutEmpty.setVisibility(lecturas.isEmpty() ? View.VISIBLE : View.GONE);
        LectureEpcListAdapter adapter = new LectureEpcListAdapter(lecturas,
                (lecturaRFID) -> {
                    Log.e(TAG, "displayLecturesRFID lecturaRFID: " + lecturaRFID);
                    presenter.deleteLectureRFID(lecturaRFID.getCodigo());
                }
        );
        rvData.setAdapter(adapter);
    }

    @Override
    public void actualizarTextoPagina(String texto) {
        if (txtPagina != null) {
            txtPagina.setText(texto);
        }
    }

    // suscribe
    @Override
    public void lectureDeleted() {
        refreshList(true);
        if (getParentFragment() instanceof IBottomSheet) {
            ((LectureFragment) getParentFragment()).lecturaDeleted();
        }
    }

    @Override
    public void displayError(String message) {
        new CustomDialog.Builder(getContext())
                .setMessage(message)
                .setTheme(R.style.AppTheme_Dialog_Error)
                .setIcon(R.drawable.ic_close)
                .setPositiveButtonLabel(getString(R.string.label_ok))
                .build().show();
    }

    @Override
    public void displayMessage(String message) {
        UtilMethods.showToast(message);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

}

