package pe.com.dms.inventariosoft.features.queries;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import com.google.android.material.textfield.TextInputLayout;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.google.android.gms.vision.barcode.Barcode;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import pe.com.dms.inventariosoft.R;
import pe.com.dms.inventariosoft.data.pojos.HijoConsulta;
import pe.com.dms.inventariosoft.features.main.MainActivity;
import pe.com.dms.inventariosoft.features.shared.BaseFragment;
import pe.com.dms.inventariosoft.utils.Constants;
import pe.com.dms.inventariosoft.utils.SimpleDividerItemDecoration;
import pe.com.dms.inventariosoft.utils.UtilMethods;
import pe.com.dms.inventariosoft.utils.dialogs.CustomDialog;
import pe.com.dms.inventariosoft.utils.interfaces.SpinnerListener;
import pe.com.dms.inventariosoft.utils.scanner.CodeCaptureActivity;
import timber.log.Timber;

public class QueryFragment extends BaseFragment implements QueryFragmentContract.View {

    public static final String TAG = QueryFragment.class.getSimpleName();
    public ItemConsultaAdapter adapter;

    @BindView(R.id.ll_filter)
    LinearLayout llFilter;

    @BindView(R.id.bt_toggle_filters)
    ImageButton btToggleFilters;

    @BindView(R.id.ac_criteria)
    AutoCompleteTextView acCriteria;
    @BindView(R.id.til_value)
    TextInputLayout tilValue;
    @BindView(R.id.rv_data)
    RecyclerView rvData;
    @Inject
    QueryFragmentPresenter presenter;
    Unbinder unbinder;
    @BindView(R.id.bt_scan_value)
    AppCompatImageView btScanValue;
    private int selectedCriteriaPosition = 0;
//    private MenuItem actionMore;
//    private MenuItem actionLess;
    private BroadcastReceiver mScanReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String barcode = intent.getStringExtra(Constants.SCANNER_ACTION);
            Log.e(TAG, "mScanReceiver SCANNER_ACTION barcode: " + barcode);

            if (TextUtils.isEmpty(barcode)) {
                barcode = intent.getStringExtra(Constants.SCANNER_STRING);
                Log.e(TAG, "mScanReceiver SCANNER_STRING barcode: " + barcode);
            }

            if (TextUtils.isEmpty(barcode)) {
                barcode = intent.getStringExtra(Constants.SCANNER_STRING_HONEY);
                Log.e(TAG, "mScanReceiver SCANNER_STRING_HONEY barcode: " + barcode);
            }
            if (tilValue.hasFocus()) {
                tilValue.getEditText().setText(barcode.trim());
            }
        }
    };

    public QueryFragment() {
    }

    public static QueryFragment newInstance() {
        return new QueryFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        getActivityComponent().inject(this);
        presenter.attachView(this);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle avedInstanceState) {
//        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.fragment_query, container, false);
        unbinder = ButterKnife.bind(this, view);
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.SCANNER_ACTION);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requireContext().registerReceiver(
                    mScanReceiver,
                    filter,
                    Context.RECEIVER_EXPORTED
            );
        } else {
            requireContext().registerReceiver(mScanReceiver, filter);
        }

        return view;
    }

    @Override
    protected void setupViews(View view) {
        String[] opciones = getResources().getStringArray(R.array.query);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                opciones
        );

        acCriteria.setAdapter(adapter);

        acCriteria.setOnItemClickListener((parent, v, position, id) -> {
            selectedCriteriaPosition = position;
            presenter.setCriteria(position);
        });

        if (opciones.length > 0) {
            acCriteria.setText(opciones[0], false);
        }
        btToggleFilters.setOnClickListener(v -> {
            if (llFilter.getVisibility() == View.VISIBLE) {
                llFilter.setVisibility(View.GONE);
                btToggleFilters.setImageResource(R.drawable.ic_expand_more);
            } else {
                llFilter.setVisibility(View.VISIBLE);
                btToggleFilters.setImageResource(R.drawable.ic_expand_less);
            }

        });


        presenter.syncLecture();
    }

    @Override
    public void setupValue(String defaultUbicacion, boolean isCamaraScan) {
//        btScanValue.setVisibility(UtilMethods.getVisibility(isCamaraScan));
        btScanValue.setOnClickListener(v -> {
            Intent intent = CodeCaptureActivity.newInstanceBarcode(getContext());
            startActivityForResult(intent, Constants.INTENT_SCAN_VALOR);
        });

        if (!TextUtils.isEmpty(defaultUbicacion)) {
            tilValue.getEditText().setText(defaultUbicacion);
            tilValue.getEditText().setEnabled(false);
        } else {
            tilValue.getEditText().setText("");
            tilValue.getEditText().setEnabled(true);
        }
    }

    @Override
    public void displayInventarioDoneError(String message) {
        CustomDialog dialogError = new CustomDialog.Builder(getContext())
                .setIcon(R.drawable.ic_close)
                .setTheme(R.style.AppTheme_Dialog_Error)
                .setMessage(message)
                .setPositiveButtonLabel(getString(R.string.label_ok))
                .setPositiveButtonlistener(() -> {
//                    TODO: Implementar RETRY!
                    presenter.requestNextInventario();
                })
                .build();
        if (!dialogError.isShowing()) {
            dialogError.show();
        }
    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (resultCode == Activity.RESULT_OK) {
//            if (data != null) {
//                Barcode barcode = data.getParcelableExtra(CodeCaptureActivity.BarcodeObject);
//                String code = barcode.displayValue;
//                if (requestCode == Constants.INTENT_SCAN_VALOR) {
//                    tilValue.getEditText().setText(code);
//                }
//            }
//        }
//    }

    @Override
    public void goToConfig() {
        ((MainActivity) requireActivity()).setupNavigationMenuView();
    }

    @OnClick(R.id.bt_search)
    public void onViewClicked() {

        String value = tilValue.getEditText().getText().toString();
        if (TextUtils.isEmpty(value.trim())) {
            new CustomDialog.Builder(getContext())
                    .setTitle("Alerta")
                    .setMessage("Debe ingresar un valor")
                    .setPositiveButtonLabel(getString(R.string.label_ok))
                    .setIcon(R.drawable.ic_alert)
                    .setTheme(R.style.AppTheme_Dialog_Warning)
                    .setPositiveButtonlistener(super::onBackPressed)
                    .build().show();
            return;
        }
//        llFilter.setVisibility(View.GONE);
//        actionMore.setVisible(true);
//        actionLess.setVisible(false);
        presenter.showLecture(selectedCriteriaPosition, value);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        inflater.inflate(R.menu.query, menu);
//        actionMore = menu.findItem(R.id.action_more);
//        actionMore.setVisible(false);
//        actionLess = menu.findItem(R.id.action_less);
//        actionLess.setVisible(true);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.action_more:
////                llFilter.setVisibility(View.VISIBLE);
////                actionMore.setVisible(false);
////                actionLess.setVisible(true);
//                return true;
//            case R.id.action_less:
////                llFilter.setVisibility(View.GONE);
////                actionMore.setVisible(true);
////                actionLess.setVisible(false);
//                return true;
//            case R.id.action_delete:
//                new CustomDialog.Builder(getContext())
//                        .setTitle("Alerta")
//                        .setMessage("¿Desea eliminar todas las lecturas?")
//                        .setPositiveButtonLabel(getString(R.string.label_yes))
//                        .setNegativeButtonLabel(getString(R.string.label_no))
//                        .setBooleanPostivieUnique(false)
//                        .setIcon(R.drawable.ic_alert)
//                        .setTheme(R.style.AppTheme_Dialog_Warning)
//                        .setPositiveButtonlistener(() -> presenter.removeAll())
//                        .build().show();
//                return true;
//            default:
//                return super.onOptionsItemSelected(item);
//        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        presenter.detachView();
        try {
            requireActivity().unregisterReceiver(this.mScanReceiver);
        } catch (IllegalArgumentException e) {
            Timber.e("Receiver no estaba registrado");
        }
    }
//
//    @Override
//    public void showLecture(List<HeaderConsulta> headerConsultaList) {
//        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
//        if (headerConsultaList.size() == 0) UtilMethods.showToast("No se encontraron resultados");
//        adapter = new HeaderConsultaAdapter(headerConsultaList);
//        adapter.onRemoveHijo((id, positiom) -> {
//            Timber.d("onLongClick Hijo: %s, %s", id, positiom);
//            new CustomDialog.Builder(getContext())
//                    .setTitle("Alerta")
//                    .setMessage("¿Desea eliminar la lectura?")
//                    .setPositiveButtonLabel(getString(R.string.label_yes))
//                    .setNegativeButtonLabel(getString(R.string.label_no))
//                    .setIcon(R.drawable.ic_alert)
//                    .setTheme(R.style.AppTheme_Dialog_Warning)
//                    .setPositiveButtonlistener(() -> presenter.removeLectureHijo(id, positiom))
//                    .build().show();
//        });
//        adapter.onRemovePadre(items -> {
//            Timber.d("onLongClick Padre: %s", items.size());
//            new CustomDialog.Builder(getContext())
//                    .setTitle("Alerta")
//                    .setMessage("¿Desea eliminar todo el grupo de lectura?")
//                    .setPositiveButtonLabel(getString(R.string.label_yes))
//                    .setNegativeButtonLabel(getString(R.string.label_no))
//                    .setIcon(R.drawable.ic_alert)
//                    .setTheme(R.style.AppTheme_Dialog_Warning)
//                    .setPositiveButtonlistener(() ->
//                            presenter.removeLecturePadre(items))
//                    .build().show();
//        });
//        rvData.setLayoutManager(layoutManager);
//        rvData.addItemDecoration(new SimpleDividerItemDecoration(getContext(), R.drawable.line_divider_white));
//        rvData.setAdapter(adapter);
//    }

    public void showLecturev2(List<HijoConsulta> lista){
        if (lista == null || lista.isEmpty()) {
            UtilMethods.showToast("No se encontraron resultados");
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());

        adapter = new ItemConsultaAdapter();
        adapter.setItems(lista);

        rvData.setLayoutManager(layoutManager);
        rvData.setAdapter(adapter);
    }


    @Override
    public void displayError(String message) {

        new CustomDialog.Builder(getContext())
                .setTitle("Error")
                .setMessage(message)
                .setPositiveButtonLabel(getString(R.string.label_ok))
                .setIcon(R.drawable.ic_close)
                .setTheme(R.style.AppTheme_Dialog_Error)
                .build().show();
    }

    @Override
    public void showLecture(List<HijoConsulta> lista) {
        if (lista == null || lista.isEmpty()) {
            UtilMethods.showToast("No se encontraron resultados");
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());

        adapter = new ItemConsultaAdapter();
        adapter.setItems(lista);

        rvData.setLayoutManager(layoutManager);
        rvData.setAdapter(adapter);

    }

    @Override
    public void removeLectura() {
        onViewClicked();
    }

    @Override
    public void removeAll() {
//        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
//        adapter = new HeaderConsultaAdapter(new ArrayList<>());
//        rvData.setLayoutManager(layoutManager);
//        rvData.addItemDecoration(new SimpleDividerItemDecoration(getContext(), R.drawable.line_divider_white));
//        rvData.setAdapter(adapter);
    }
}