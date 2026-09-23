package pe.com.dms.inventariosoft.features.config.ip;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import com.bumptech.glide.Glide;
import com.google.android.gms.vision.barcode.Barcode;
import javax.inject.Inject;
import pe.com.dms.inventariosoft.R;
import pe.com.dms.inventariosoft.databinding.ActivityIpConfigBinding;
import pe.com.dms.inventariosoft.features.shared.BaseActivity;
import pe.com.dms.inventariosoft.utils.Constants;
import pe.com.dms.inventariosoft.utils.UtilMethods;
import pe.com.dms.inventariosoft.utils.interfaces.TextWatcher;
import pe.com.dms.inventariosoft.utils.scanner.CodeCaptureActivity;

public class ConfigIpActivity extends BaseActivity implements ConfigIpContract.View {

    String TAG = ConfigIpActivity.class.getSimpleName();
    @Inject
    ConfigIpPresenter presenter;

    private BroadcastReceiver mScanReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String ipRead = intent.getStringExtra(Constants.SCANNER_STRING);
            binding.etIp.setText(ipRead);
        }
    };

    public static Intent newInstance(Context context) {
        Intent intent = new Intent(context, ConfigIpActivity.class);
        return intent;
    }

    private ActivityIpConfigBinding binding;

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivityComponent().inject(this);
        binding = ActivityIpConfigBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        presenter.attachView(this);

        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.SCANNER_ACTION);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            getContext().registerReceiver(this.mScanReceiver, filter, Context.RECEIVER_NOT_EXPORTED);
        } else {
            registerReceiver(this.mScanReceiver, filter);
        }

        setupNavigation();
        setTitle("Configuración de IP");

        setupViews();
        setListeners();
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(this.mScanReceiver);
        super.onDestroy();
    }

    private void setupNavigation() {
//        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        super.onBackPressed();
    }

    private void setupViews() {
        presenter.onViewCreated();

        Glide.with(getContext())
                .load(R.drawable.scanning)
                .into(binding.ivScanning);

        binding.etIp.addTextChangedListener((TextWatcher) s -> presenter.updateIp(s));

        binding.etIp.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                binding.etIp.setEnabled(false);
                UtilMethods.hideKeyboard(binding.etIp, getContext());
                return true;
            }
            return false;
        });
    }

    public void setListeners() {
        binding.btScan.setOnClickListener(view -> {
            onScanClicked();
        });

//        binding.btManual.setOnClickListener(view -> {
//            onManualClicked();
//        });
        binding.btnFinishConfig.setOnClickListener(view -> {
            onBackPressed();
        });
    }

    public void onScanClicked() {
        //Todo para el dispositivo V5000S esta deshabilitado
        PackageManager packageManager = this.getPackageManager();
        if (UtilMethods.checkCameraHardware(packageManager)) {
            Intent intent = CodeCaptureActivity.newInstanceQr(getContext());
            startActivityForResult(intent, Constants.INTENT_SCAN);
        } else {
            UtilMethods.showToast("Dispositivo no cuenta con camara");
        }
    }

    public void onManualClicked() {
        binding.etIp.setEnabled(true);
        binding.etIp.requestFocus();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constants.INTENT_SCAN && data != null) {
                Barcode barcode = data.getParcelableExtra(CodeCaptureActivity.BarcodeObject);
                String ipString = barcode.displayValue;
                binding.etIp.setText(ipString);
            }
        }
    }

    @Override
    public void displayIp(String ip) {
        binding.etIp.setText(ip);
    }
}
