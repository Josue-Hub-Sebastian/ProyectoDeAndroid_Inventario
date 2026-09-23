package pe.com.dms.inventariosoft.features.config;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;

import javax.inject.Inject;

import pe.com.dms.inventariosoft.R;
import pe.com.dms.inventariosoft.data.PreferenceManager;
import pe.com.dms.inventariosoft.data.models.Almacen;
import pe.com.dms.inventariosoft.data.pojos.Configuracion;
import pe.com.dms.inventariosoft.databinding.FragmentConfigurationBinding;
import pe.com.dms.inventariosoft.features.config.almacen.AlmacenSearchActivity;
import pe.com.dms.inventariosoft.features.login.LoginActivity;
import pe.com.dms.inventariosoft.features.main.MainActivity;
import pe.com.dms.inventariosoft.features.shared.BaseFragment;
import pe.com.dms.inventariosoft.features.sync.SyncDialog;
import pe.com.dms.inventariosoft.utils.Constants;
import pe.com.dms.inventariosoft.utils.UtilMethods;
import pe.com.dms.inventariosoft.utils.dialogs.CustomDialog;
import pe.com.dms.inventariosoft.utils.interfaces.TextWatcher;
import timber.log.Timber;

public class ConfigFragment extends BaseFragment implements ConfigFragmentContract.View {

    public static final String TAG = ConfigFragment.class.getSimpleName();
    private FragmentConfigurationBinding binding;

    @Inject
    ConfigFragmentPresenter presenter;



    private BroadcastReceiver mScanReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            // Intentamos obtener el dato de las llaves conocidas
            String barcode = intent.getStringExtra(Constants.SCANNER_STRING); // "barcode_string"

            // Si es null, intentamos con la llave de Honeywell ("data")
            if (TextUtils.isEmpty(barcode)) {
                barcode = intent.getStringExtra(Constants.SCANNER_STRING_HONEY);
            }

            // VALIDACIÓN PARA EVITAR CRASH: Verificamos si es nulo antes del trim()
            if (barcode != null) {
                String cleanBarcode = barcode.trim();
                Timber.d("barcode: " + cleanBarcode);
                Log.d(TAG, "mScanReceiver barcode: " + cleanBarcode); 

                if (binding.etUbicacion.hasFocus() && binding.etUbicacion.isEnabled()) {
                    Log.d(TAG, "mScanReceiver etUbicacion: " + cleanBarcode);
                    binding.etUbicacion.setText(cleanBarcode);
                }
            } else {
                Log.e(TAG, "Se recibió broadcast pero el contenido (barcode) es nulo");
            }
        }
    };

    public ConfigFragment() {
        // Required empty public constructor
    }

    public static ConfigFragment newInstance() {
        ConfigFragment fragment = new ConfigFragment();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivityComponent().inject(this);
        presenter.attachView(this);
    }

    @Override
    protected void setupVariables() {
        super.setupVariables();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentConfigurationBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.SCANNER_ACTION);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Para Android 14+
            getContext().registerReceiver(mScanReceiver, filter, Context.RECEIVER_NOT_EXPORTED);
        } else {
            // Para versiones anteriores se queda como estaba
            ContextCompat.registerReceiver(getContext(), mScanReceiver, filter, ContextCompat.RECEIVER_NOT_EXPORTED);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        getContext().unregisterReceiver(mScanReceiver);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void setupViews(View view) {
        super.setupViews(view);
        presenter.onViewCreated();
    }

    @Override
    public void initListeners() {

        binding.toggleDeviceType.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (!isChecked) return;

            switch (checkedId) {
                case R.id.btn_device_smartphone:
                    presenter.saveCamaraScan(true);
                    break;

                case R.id.btn_device_pda:
                    presenter.savePDA();

                    break;
            }
        });

        binding.scConfirmar.setOnCheckedChangeListener((compoundButton, b) ->{
            presenter.saveSolicitarConfirmacion(b);
            binding.scConfirmar.setText( b ? "Si" : "No");
        } );
        binding.scRegProducto.setOnCheckedChangeListener((compoundButton, b) -> {
            presenter.saveRegistrar(b);
            binding.scRegProducto.setText( b ? "Si" : "No");
        });

        binding.scCalculadora.setOnCheckedChangeListener((compoundButton, b) -> {
            presenter.saveCalculadora(b);
            binding.scCalculadora.setText( b ? "Si" : "No");
        });

        binding.buttomAlmacen.setOnClickListener(view -> {
            binding.buttomAlmacen.setEnabled(false);
            loading(true);
            presenter.getListAlmacen();

        });




    }

    @Override
    public void display(Configuracion configuracion) {
        if (presenter.getUserInfo().getNombre().toUpperCase(Locale.ROOT).contains("OPE") || presenter.getUserInfo().getUsername().toUpperCase(Locale.ROOT).contains("OPE")) {
//            binding.lyTipolectura.setVisibility(View.GONE);
//            binding.lyModoConteo.setVisibility(View.GONE);
            binding.lyUbicacion.setVisibility(View.GONE);
//            binding.lyLogitudDigitos.setVisibility(View.GONE);
            binding.lyLoteParent.setVisibility(View.GONE);
            binding.lySerieParent.setVisibility(View.GONE);
            binding.lyModoTrabajo.setVisibility(View.GONE);
            binding.lyConfirmacionRegistrar.setVisibility(View.GONE);
            binding.lyRegistroProducto.setVisibility(View.GONE);
        }

        binding.tvUser.setText(presenter.getUserInfo().getUsername().toUpperCase());;
//        binding.scModoConteo.setChecked(configuracion.getModo() == Constants.MODE_BARRIDO);


        Almacen almacen = configuracion.getAlmacen();
        if (almacen == null) {
            binding.etAlmacen.setText("Selecciona un almacén");

        } else {
            binding.etAlmacen.setText(almacen.getDescripcion());
            binding.etAlmacen.setTextColor(
                    ContextCompat.getColor(requireContext(), R.color.textColorPrimaryInverse)
            );
            binding.imageError.setVisibility(View.GONE);
        }

//        if (almacen == null) binding.etAlmacen.setError("Campo requerido");
        binding.etUbicacion.setText(configuracion.getUbicacion());

        binding.scLote.setChecked(configuracion.isLote());
        binding.scSerie.setChecked(configuracion.isSerie());
        binding.scModoBatch.setChecked(configuracion.isBatch());
        binding.scConfirmar.setChecked(configuracion.isSolicitarConfirmacion());
        binding.scRegProducto.setChecked(configuracion.isRegistrar());
        binding.scCalculadora.setChecked(configuracion.isCalculadora());
        binding.scCalculadora.setText(configuracion.isCalculadora() ? "Si" : "No");
        //TODO CHANGE THIS
        binding.scCamaraScan.setChecked(configuracion.isCameraScan()); //check this tomorrows
//        binding.scRfid.setChecked(configuracion.isRfd());
        binding.toggleButton.check(
                configuracion.isNumberDecimal() ? R.id.btn_decimal_number : R.id.btn_integer
        );





        if (configuracion.isCameraScan()) {
            binding.toggleDeviceType.check(R.id.btn_device_smartphone);

        }
        else {
            binding.toggleDeviceType.check(R.id.btn_device_pda);

        }




//
//        binding.scRfid.setOnCheckedChangeListener(((compoundButton, b) -> presenter.saveRFID(b)));
//        binding.scCamaraScan.setOnCheckedChangeListener((compoundButton, b) -> presenter.saveCamaraScan(b)); //check this tomorrows
//        binding.scModoConteo.setOnCheckedChangeListener((compoundButton, b) -> presenter.saveModo(b ? 1 : 0));


//        binding.inputSizeNumber.setText(String.valueOf(configuracion.getSizeNumber()));
//        binding.inputSizeNumber.addTextChangedListener((TextWatcher) s -> {//longitud de digitos
//            if (!TextUtils.isEmpty(s))
//                presenter.saveSizeNumber(Integer.parseInt(s.trim()));
//        });
        binding.autoCompleteDigits.setText(String.valueOf(configuracion.getSizeNumber()), false);


        binding.autoCompleteDigits.setOnItemClickListener( (parent, view, position, id) -> {
            String item = parent.getItemAtPosition(position).toString();
            presenter.saveSizeNumber( Integer.parseInt(item));
        });

        binding.toggleButton.addOnButtonCheckedListener( (group, checkedId, isChecked) -> {
            if (!isChecked) return;
            switch (checkedId) {
                case R.id.btn_integer:
                    presenter.saveDecimalNumber(false);
                    break;
                case R.id.btn_decimal_number:
                    presenter.saveDecimalNumber(true);
                    break;
            }
        });

        binding.btnCerrarSesion.setOnClickListener(view -> {
            openLogoutDialog();
        });

    }


    @Override
    public void showRFID(boolean flag) {
        uppdateModeConteo(flag);
//        binding.scModoConteo.setEnabled(flag);
        binding.scLote.setEnabled(flag);
        binding.scSerie.setEnabled(flag);
//        binding.scCamaraScan.setEnabled(flag);
        binding.scConfirmar.setEnabled(flag);
        binding.scRegProducto.setEnabled(flag);
    }

    private void uppdateModeConteo(boolean flag) {
//        presenter.saveModo(flag ? 1 : 0);
    }

    @Override
    public void selectAlmacen(List<Almacen> almacenList) {
//        getBaseActivity().startActivityForResult(
//                AlmacenSearchActivity.newInstance(getContext(), almacenList),
//                Constants.INTENT_ALMACEN);
        startActivityForResult(AlmacenSearchActivity.newInstance(getContext(), almacenList), Constants.INTENT_ALMACEN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e(TAG, "onActivityResult requestCode: " + requestCode + ", resultCode: " + resultCode + ", data: " + data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constants.INTENT_ALMACEN) {
                String txtAlmacen = data.getStringExtra(Constants.EXTRA_LIST_ALMACEN_OBJ);
                Almacen almacen = new Gson().fromJson(txtAlmacen, Almacen.class);
                binding.etAlmacen.setText(almacen == null ? "" : almacen.getDescripcion() );
                binding.etAlmacen.setTextColor(ContextCompat.getColor(getContext(),R.color.textColorPrimaryInverse));
                binding.imageError.setVisibility(View.GONE);
                presenter.saveAlmacen(almacen);
                presenter.downLoadProducts();
                ((MainActivity) getActivity()).setupNavigationMenuView();
            }
        }
        loading(false);
        binding.buttomAlmacen.setEnabled(true);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        presenter.detachView();
    }

    @Override
    public void showMessage(String message) {
        UtilMethods.showToast(message);
    }

    @Override
    public void loading(Boolean visible) {
        if (visible) {
            ((MainActivity) getActivity()).showLoading();

        } else {
            ((MainActivity) getActivity()).setupNavigationMenuView();
            ((MainActivity) getActivity()).hideLoading();
        }
    }

    @Override
    public void logoutSuccess() {
        loading(false);
        startActivity(LoginActivity.newInstance(getContext()));
        getActivity().finish();
    }


    private void openLogoutDialog() {
        new CustomDialog.Builder(getContext())
                .setTitle(getString(R.string.dialog_logout_title))
                .setMessage(getString(R.string.dialog_logout_message))
                .setPositiveButtonLabel(getString(R.string.label_yes))
                .setNegativeButtonLabel(getString(R.string.label_no))
                .setPositiveButtonlistener(() -> {
                    if (!UtilMethods.isNetworkConnected(getContext())) {
                        UtilMethods.showToast("Se requiere conexión a internet para cerrar sesión y liberar su usuario.");
                        return;
                    }
                    loading(true);
                    presenter.logout();
                })
                .build().show();
    }
}
