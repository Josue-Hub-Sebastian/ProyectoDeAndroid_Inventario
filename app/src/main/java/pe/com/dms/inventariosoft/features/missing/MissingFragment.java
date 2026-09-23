package pe.com.dms.inventariosoft.features.missing;

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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.google.android.gms.vision.barcode.Barcode;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import pe.com.dms.inventariosoft.R;
import pe.com.dms.inventariosoft.data.models.Lectura;
import pe.com.dms.inventariosoft.features.lecture.QuickLectureActivity;
import pe.com.dms.inventariosoft.features.shared.BaseFragment;
import pe.com.dms.inventariosoft.utils.Constants;
import pe.com.dms.inventariosoft.utils.SimpleDividerItemDecoration;
import pe.com.dms.inventariosoft.utils.UtilMethods;
import pe.com.dms.inventariosoft.utils.dialogs.CustomDialog;
import pe.com.dms.inventariosoft.utils.dialogs.ProgressDialog;
import pe.com.dms.inventariosoft.utils.interfaces.SpinnerListener;
import pe.com.dms.inventariosoft.utils.scanner.CodeCaptureActivity;

public class MissingFragment extends BaseFragment implements MissingFragmentContract.View, MissingAdapter.ISelection {

    public static final String TAG = MissingFragment.class.getSimpleName();
    @BindView(R.id.ll_filter)
    LinearLayout llFilter;
    @BindView(R.id.sp_criteria)
    Spinner spCriteria;
    @BindView(R.id.til_value)
    TextInputLayout tilValue;
    @BindView(R.id.rv_data)
    RecyclerView rvData;
    @BindView(R.id.bt_scan_value)
    AppCompatImageView btScanValue;
    @Inject
    MissingFragmentPresenter presenter;
    Unbinder unbinder;
    private MenuItem actionMore;
    private MenuItem actionLess;
    private BroadcastReceiver mScanReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String barcode = intent.getStringExtra(Constants.SCANNER_STRING);
            if (tilValue.hasFocus()) {
                tilValue.getEditText().setText(barcode);
            }
        }
    };

    public MissingFragment() {
    }

    public static MissingFragment newInstance() {
        return new MissingFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        getActivityComponent().inject(this);
        presenter.attachView(this);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle avedInstanceState) {
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.fragment_missing, container, false);
        unbinder = ButterKnife.bind(this, view);
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.SCANNER_ACTION);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Para Android 14+ añadimos Context.RECEIVER_NOT_EXPORTED
            getContext().registerReceiver(mScanReceiver, filter, Context.RECEIVER_NOT_EXPORTED);
        } else {
            // Para versiones anteriores se queda como estaba
            getContext().registerReceiver(mScanReceiver, filter);
        }
        return view;
    }

    @Override
    protected void setupViews(View view) {
        spCriteria.setEnabled(false);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(spCriteria.getContext(),
                R.array.query, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCriteria.setAdapter(adapter);

        spCriteria.setOnItemSelectedListener((SpinnerListener) pos -> presenter.setCriteria(pos));
        spCriteria.setSelection(0);
    }

    @Override
    public void setupValue(String defaultUbicacion, boolean isCamaraScan) {
        btScanValue.setVisibility(UtilMethods.getVisibility(isCamaraScan));
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constants.INTENT_SCAN_VALOR) {
                if (data != null) {
                    Barcode barcode = data.getParcelableExtra(CodeCaptureActivity.BarcodeObject);
                    String code = barcode.displayValue;
                    tilValue.getEditText().setText(code);
                }
            }
            if (requestCode == Constants.INTENT_LECTURA) {
                onViewClicked();
            }
        }
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
        llFilter.setVisibility(View.GONE);
        actionMore.setVisible(true);
        actionLess.setVisible(false);
        if (UtilMethods.isNetworkConnected(getContext())) {
            ProgressDialog.show(getContext(), R.string.app_cargando);
            presenter.showMissing(value);
        } else {
            UtilMethods.showToast(getString(R.string.sinInternet));
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.missing, menu);
        actionMore = menu.findItem(R.id.action_more);
        actionMore.setVisible(false);
        actionLess = menu.findItem(R.id.action_less);
        actionLess.setVisible(true);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_more:
                llFilter.setVisibility(View.VISIBLE);
                actionMore.setVisible(false);
                actionLess.setVisible(true);
                return true;
            case R.id.action_less:
                llFilter.setVisibility(View.GONE);
                actionMore.setVisible(true);
                actionLess.setVisible(false);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        presenter.detachView();
        getActivity().unregisterReceiver(this.mScanReceiver);
    }

    @Override
    public void showMissing(List<Lectura> missings) {
        ProgressDialog.dismiss();
        UtilMethods.hideKeyboard(tilValue, getContext());

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        rvData.setLayoutManager(layoutManager);
        rvData.addItemDecoration(new SimpleDividerItemDecoration(getContext(), R.drawable.line_divider_white));

        MissingAdapter adapter = new MissingAdapter(missings, this);
        rvData.setAdapter(adapter);
    }

    @Override
    public void showError() {
        ProgressDialog.dismiss();
    }

    @Override
    public void onSelect(Lectura lectura) {
        startActivityForResult(QuickLectureActivity.newInstance(getContext(), lectura), Constants.INTENT_LECTURA);
    }
}