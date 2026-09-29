package pe.com.dms.inventariosoft.features.lecture;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import androidx.core.content.ContextCompat;
import androidx.transition.Fade;
import androidx.transition.TransitionManager;
import androidx.transition.TransitionSet;
import androidx.interpolator.view.animation.LinearOutSlowInInterpolator;
import androidx.appcompat.widget.AppCompatImageView;

import android.os.SystemClock;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.UHF.scanlable.UfhData;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.gson.Gson;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.common.InputImage;
import com.ubx.usdk.USDKManager;
import com.ubx.usdk.rfid.RfidManager;
import com.ubx.usdk.rfid.aidl.IRfidCallback;
import com.ubx.usdk.rfid.aidl.RfidDate;
import com.ubx.usdk.util.SoundTool;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import pe.com.dms.inventariosoft.R;
import pe.com.dms.inventariosoft.data.models.Lectura;
import pe.com.dms.inventariosoft.data.models.Producto;
import pe.com.dms.inventariosoft.data.pojos.TagScan;
import pe.com.dms.inventariosoft.databinding.FragmentSearchBinding;
import pe.com.dms.inventariosoft.features.lecture.list.LectureListFragment;
import pe.com.dms.inventariosoft.features.main.MainActivity;
import pe.com.dms.inventariosoft.features.shared.BaseFragment;
import pe.com.dms.inventariosoft.utils.TecladoCalculadoraHelper;
import pe.com.dms.inventariosoft.utils.Constants;
import pe.com.dms.inventariosoft.utils.UtilMethods;
import pe.com.dms.inventariosoft.utils.dialogs.CustomDialog;
import pe.com.dms.inventariosoft.utils.dialogs.ModalKeyGuard;
import pe.com.dms.inventariosoft.utils.interfaces.TextWatcher;
import pe.com.dms.inventariosoft.utils.scanner.CodeCaptureActivity;
import timber.log.Timber;

public class LectureFragment extends BaseFragment implements LectureContract.View, IBottomSheet {
    public static final String TAG = LectureFragment.class.getSimpleName();
    private FragmentSearchBinding binding;
    private static final int SCAN_INTERVAL = 100;
    private static final int MSG_UPDATE_LISTVIEW = 0;
    private static final int MSG_SHOW_PROPERTIES = 1;
    private boolean isLibraryLoaded = false;

    private boolean actualizandoProducto = false;
// showProductoInfo
    private boolean productoMatchedByCode = true;

    @BindView(R.id.tv_conteo)
    TextView tvConteo;
    @BindView(R.id.tv_lectura)
    TextView tvLectura;
    @BindView(R.id.til_ubicacion)
    TextInputLayout tilUbicacion;

    @BindView(R.id.tEdit_Ubicacion)
    TextInputEditText tEditUbicacion;
    @BindView(R.id.til_producto)
    TextInputLayout tilProducto;
    @BindView(R.id.til_descripcion)
    TextInputLayout tilDescripcion;
    @BindView(R.id.tEdit_descripcion)
    TextInputEditText tEditDescripcion;
    @BindView(R.id.til_poder)
    TextInputLayout tilPoder;
    @BindView(R.id.til_lote)
    TextInputLayout tilLote;
    @BindView(R.id.til_serie)
    TextInputLayout tilSerie;
    @BindView(R.id.til_cantidad)
    TextInputLayout tilCantidad;

    @BindView(R.id.tEdit_cantidad)
    TextInputEditText tEditCantidad;

    @BindView(R.id.tEdit_um)
    TextInputEditText tEditUm;

    @BindView(R.id.linearLayout3)
    LinearLayout linearLayout3;
    @BindView(R.id.bt_send)
    Button btSend;
    @BindView(R.id.bt_savePoder)
    Button btSavePoder;
    @BindView(R.id.bt_scan)
    Button btScan;
    @BindView(R.id.bottom_sheet)
    LinearLayout bottomSheet;
    BottomSheetBehavior bottomSheetBehavior;
    @BindView(R.id.iv_expandable)
    AppCompatImageView ivExpandable;
    @BindView(R.id.bt_scan_ubicacion)
    AppCompatImageView btScanUbicacion;

//    @BindView(R.id.bt_scan_producto)
//    AppCompatImageView btScanProducto; tEditProducto
    @BindView(R.id.bt_scan_lote)
    AppCompatImageView btScanLote;
    @BindView(R.id.bt_scan_serie)
    AppCompatImageView btScanSerie;
    @BindView(R.id.cl_config)
    ConstraintLayout clConfig;
    @BindView(R.id.viewFinder)
    PreviewView viewFinder;

    @BindView(R.id.card_view)
    MaterialCardView cardView;

    @BindView(R.id.viewScannerFrame)
    View viewScannerFrame;


    @Inject
    LecturePresenter presenter;
    Unbinder unbinder;
    private boolean quick = false;
    private Timer timer;
    private int sizeData;
    private boolean Scanflag = false;
    //private boolean scanned = false;
    private boolean isCanceled = true;
    private Map<String, Long> data;
    private LectureListFragment sheetFragment;
    private MenuItem actionMore;
    private MenuItem actionLess;
    private CustomDialog dialogError;
    private CustomDialog dialogParticipacion;
    private CustomDialog dialogWaiting;
    //DT50
    public RfidManager mRfidManager;
    private boolean isInv =  false;



    private int nextInv;
    private HashMap<String, TagScan> mapData;
    private List<TagScan> dat2;
    public List<String> mDataParents;
    private long time = 0l;
    public  boolean RFID_INIT_STATUS = false;
    public RfidDate mRfidDate;
    private Handler handler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(  Message msg) {
            super.handleMessage(msg);
            showFirmware();
        }
    };

    private String lastValue = "";
    private long lastReadTime = 0;
    private Boolean isShowConfirmation = false;

//submit
    private void showFirmware(){
        try {
            if (mRfidDate!=null) {
                RFID_INIT_STATUS = true;
                String firmware = String.valueOf(mRfidDate.getbtMajor() & 0xFF) + "." + String.valueOf(mRfidDate.getbtMinor() & 0xFF);
                Log.v(TAG, "refreshSetting()  固件版本：" + firmware);
                Log.v(TAG,"固件：v"+firmware);
            }else {
                Log.v(TAG,"mActivty.mRfidDate == null");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private BroadcastReceiver mScanReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String barcode = intent.getStringExtra(Constants.SCANNER_ACTION);
            Log.e(TAG, "mScanReceiver SCANNER_ACTION barcode: " + barcode);

            if (TextUtils.isEmpty(barcode)) {
                barcode = intent.getStringExtra(Constants.SCANNER_STRING);
                Log.e(TAG, "mScanReceiver SCANNER_STRING barcode: " + barcode);
            }

            if (TextUtils.isEmpty(barcode)) return;

            // Si ningún campo tiene el foco, determinar a cuál darle el foco automáticamente
            if (!tilUbicacion.hasFocus() && !tilProducto.hasFocus() &&
                !tilLote.hasFocus() && !tilSerie.hasFocus() && !tilCantidad.hasFocus()) {
                if (TextUtils.isEmpty(tilUbicacion.getEditText().getText().toString().trim())) {
                    tilUbicacion.requestFocus();
                } else {
                    tilProducto.requestFocus();
                }
            }

            if (tilUbicacion.hasFocus() && tilUbicacion.isEnabled()) {
                Log.d(TAG, "mScanReceiver tilUbicacion: " + barcode.trim());
                tilUbicacion.getEditText().setText("");
                tilUbicacion.getEditText().setText(barcode.trim());
                tilProducto.requestFocus();
                tilUbicacion.clearFocus();
            } else if (tilProducto.hasFocus()) {
                Log.d(TAG, "mScanReceiver tilProducto: " + barcode.trim());
                tilProducto.getEditText().setText(barcode.trim());
                presenter.searchProducto(barcode.trim());
                if (tilLote.getVisibility() == View.VISIBLE) {
                    tilLote.requestFocus();
                    tilProducto.clearFocus();
                } else if (tilSerie.getVisibility() == View.VISIBLE) {
                    tilSerie.requestFocus();
                    tilProducto.clearFocus();
                } else {
                    if (presenter.isBarrido()) {
                        submit();
                        tilProducto.requestFocus();
                    } else {
                        tilCantidad.requestFocus();
                        tilProducto.clearFocus();
                    }
                }
            } else if (tilLote.hasFocus() && tilLote.getVisibility() == View.VISIBLE) {
                Log.d(TAG, "mScanReceiver tilLote: " + barcode.trim());
                tilLote.getEditText().setText("");
                tilLote.getEditText().setText(barcode.trim());
                if (tilSerie.getVisibility() == View.VISIBLE) {
                    tilSerie.requestFocus();
                } else {
                    if (presenter.isBarrido()) {
                        submit();
                        tilProducto.requestFocus();
                    } else {
                        tilCantidad.requestFocus();
                    }
                }
                tilLote.clearFocus();
            } else if (tilSerie.hasFocus() && tilSerie.getVisibility() == View.VISIBLE) {
                Log.d(TAG, "mScanReceiver tilSerie: " + barcode.trim());
                tilSerie.getEditText().setText("");
                tilSerie.getEditText().setText(barcode.trim());
                if (presenter.isBarrido()) {
                    Log.d(TAG, "isbarrido ");
                    submit();
                    tilProducto.requestFocus();
                } else {
                    tilCantidad.requestFocus();
                }
                tilSerie.clearFocus();
            } else if (tilCantidad.hasFocus()) {
                Log.d(TAG, "mScanReceiver tilCantidad: " + barcode.trim());
                Matcher matcher = presenter.getPattern().matcher(barcode.trim());
                Log.d(TAG, "setupUbicacion matcher: " + matcher.matches());
                if (!matcher.matches()) {
                    showValidationError("La cantidad no tiene el formato correcto");
                    tilCantidad.requestFocus();
                } else {
                    tilCantidad.getEditText().setText("");
                    tilCantidad.getEditText().setText(barcode.trim());
                }
            }
        }
    };

    private Handler mHandler;
    private Handler mHandler1;

    InputFilter filter = (CharSequence source, int start, int end,
                          Spanned dest, int dstart, int dend) -> {
        if (source.equals("")) return null;
        StringBuilder builder = new StringBuilder(dest);
        builder.replace(dstart, dend, source.subSequence(start, end).toString());
        Matcher matcher = presenter.getPattern().matcher(builder);
        if (!matcher.matches())
            return "";
        return null;
    };

    public LectureFragment() {
        // Required empty public constructor
    }

    public static LectureFragment newInstance() {
        LectureFragment fragment = new LectureFragment();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }

    public static LectureFragment newInstanceQuick(String stringLectura) {
        LectureFragment fragment = new LectureFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.EXTRA_LECTURE, stringLectura);
        bundle.putBoolean(Constants.EXTRA_QUICK, true);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivityComponent().inject(this);
        checkLibraryIsload();
        Log.d(TAG, "onCreate: ");
    }

    private void checkLibraryIsload() {
        try {
            System.loadLibrary("Uhf_jni");
            isLibraryLoaded = true;
        } catch (UnsatisfiedLinkError e) {
            Log.e(TAG, "Native RFID library not found for this architecture");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        binding = FragmentSearchBinding.inflate(inflater, container, false);
        unbinder = ButterKnife.bind(this, binding.getRoot());
        presenter.attachView(this);
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.SCANNER_ACTION);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requireContext().registerReceiver(mScanReceiver, filter, Context.RECEIVER_EXPORTED);
        } else {
            ContextCompat.registerReceiver(getContext(), mScanReceiver, filter, ContextCompat.RECEIVER_NOT_EXPORTED);
        }
        sizeData = 0;
        mHandler = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                if (isCanceled) return;
                switch (msg.what) {
                    case MSG_UPDATE_LISTVIEW:
                        try {
                            //Log.e(TAG, "onCreateView UfhData.Result6c.size(): " + UfhData.Result6c.size());
                            if (sizeData != UfhData.Result6c.size()) {
                                sizeData = UfhData.Result6c.size();
                                data = UfhData.scanResult6c;
                                Log.e(TAG, "onCreateView data: " + data);
                                List<String> keys = new ArrayList(data.keySet());
                                Log.e(TAG, "onCreateView keys: " + keys);
                                Log.e(TAG, "onCreateView tilProducto.getEditText().getText().toString(): " + tilProducto.getEditText().getText().toString());
                                presenter.saveRFID(keys, tilProducto.getEditText().getText().toString());
                            }
                        } catch (Exception e) {
                        }
                        break;
                    default:
                        break;
                }
                super.handleMessage(msg);
            }

        };
        mHandler1 = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                // TODO Auto-generated method stub
                super.handleMessage(msg);
                switch (msg.what) {
                    case MSG_SHOW_PROPERTIES:
                        showResult(UfhData.UhfGetData.getUhfdBm()[0]);
                        btScan.setEnabled(true);
                        btSavePoder.setEnabled(true);
                        setLoading(View.GONE);
                        break;
                    default:
                        break;
                }
            }
        };
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (presenter.isCalculadora()) {
            TecladoCalculadoraHelper.attach(tEditCantidad, presenter.getNumberDecimal());
        } else {
            // Si no esta habilitada la calculadora, restauramos teclado numerico estandar
            tEditCantidad.setShowSoftInputOnFocus(true);
            if (presenter.getNumberDecimal()) {
                tEditCantidad.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            } else {
                tEditCantidad.setInputType(InputType.TYPE_CLASS_NUMBER);
            }
        }

        SoundTool.getInstance(getContext());// dt50

    }

    @Override
    public void onStart() {
        super.onStart();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mRfidManager!=null) {
                    Log.v(TAG,"--- getFirmwareVersion()   ----");
                    mRfidManager.getFirmwareVersion(mRfidManager.getReadId());
                    try {
                        Thread.sleep(3000);
                        btScan.setEnabled(true);
                        btSavePoder.setEnabled(true);
                        setLoading(View.GONE);
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                    }

                }else {
                    Log.v(TAG,"onStart()  --- getFirmwareVersion()   ----  mActivity.mRfidManager == null");
                }
            }
        }, 5000);
    }

    @Override
    public void onResume() {
        super.onResume();
        showFirmware();
        if (!presenter.getCameraScan()) {
            stopCamera();
        }

        // LUEGO REGISTRAMOS EL ESCANER
        IntentFilter filter = new IntentFilter(Constants.SCANNER_ACTION);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requireContext().registerReceiver(mScanReceiver, filter, Context.RECEIVER_EXPORTED);
        } else {
            ContextCompat.registerReceiver(requireContext(), mScanReceiver, filter, ContextCompat.RECEIVER_NOT_EXPORTED);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (presenter.getRFID()) {
            inflater.inflate(R.menu.rfid, menu);
            actionMore = menu.findItem(R.id.action_more);
            actionMore.setVisible(false);
            actionLess = menu.findItem(R.id.action_less);
            actionLess.setVisible(true);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_more:
                clConfig.setVisibility(View.VISIBLE);
                actionMore.setVisible(false);
                actionLess.setVisible(true);
                return true;
            case R.id.action_less:
                clConfig.setVisibility(View.GONE);
                actionMore.setVisible(true);
                actionLess.setVisible(false);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void setupViews(View view) {
        super.setupViews(view);
        boolean isSmartphoneMode = presenter.getCameraScan();

        if (isSmartphoneMode) {
            // Solo si es modo Smartphone pedimos permiso y arrancamos cámara
            checkCameraPermission();
            cardView.setVisibility(View.VISIBLE); // Mostramos el PreviewView del layout
        } else {
            // SI ES MODO PDA: Nos aseguramos de NO tocar la camara
            cardView.setVisibility(View.GONE);
            stopCamera(); // Método para liberar el driver
            Log.d(TAG, "Modo PDA detectado: Láser liberado.");
        }


        String stringLectura = getArguments().getString(Constants.EXTRA_LECTURE, "");
        quick = getArguments().getBoolean(Constants.EXTRA_QUICK, false);
        Lectura lectura = new Gson().fromJson(stringLectura, Lectura.class);

        initSheet();
        presenter.onViewCreated();
        if (presenter.getRFID()) {
            bottomSheet.setVisibility(View.VISIBLE);
            setLoading(View.VISIBLE);
            openRFID();
            tilCantidad.setVisibility(View.GONE);
            btSend.setVisibility(View.GONE);
            if (lectura != null) {
                tilProducto.getEditText().setText(lectura.getCodigoProducto()); // checkpoint
            } else {
                tilProducto.setVisibility(View.GONE);
            }
        } else {
            clConfig.setVisibility(View.GONE);
            if (lectura != null) {
                tilUbicacion.getEditText().setText(lectura.getCodigoUbicacion());
                tilProducto.getEditText().setText(lectura.getCodigoProducto());
                tilLote.getEditText().setText(lectura.getLote());
                tilSerie.getEditText().setText(lectura.getSerie());
            }
            /*if(presenter.getNumberDecimal()){
                tEditCantidad.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            }else {
                tEditCantidad.setInputType(InputType.TYPE_CLASS_NUMBER);
            }*/
            // El tipo de entrada se gestiona en TecladoCalculadoraHelper para permitir signos
            tEditCantidad.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        }
        btSend.setOnClickListener(v -> submit());
        tilUbicacion.setEndIconOnClickListener(v -> {
            EditText et = tilUbicacion.getEditText();

            if (et != null) {
                if (et.isEnabled()) {
                    String texto = et.getText().toString().trim();
                    if (TextUtils.isEmpty(texto)) {
                        showValidationError("Debe ingresar una ubicación antes de bloquear");
                        return;
                    }
                    et.setEnabled(false);
                    tilUbicacion.setEndIconDrawable(R.drawable.ic_lock);
                    tilUbicacion.clearFocus();
                    presenter.updateUbicacion(texto);
                    presenter.validateUbicacion(texto);
                } else {
                    et.setEnabled(true);
                    et.setText("");
                    tilUbicacion.setEndIconDrawable(R.drawable.ic_unlock);
                    et.requestFocus();
                    presenter.updateUbicacion("");
                }
            }
        });



        tEditUbicacion.setOnEditorActionListener((textView, i, keyEvent) -> {
            if(i == EditorInfo.IME_ACTION_DONE || i == EditorInfo.IME_ACTION_NEXT){
                presenter.validateUbicacion(tilUbicacion.getEditText().getText().toString());
                return false;
            }
            return false;

        });

        tEditUbicacion.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                String texto = tEditUbicacion.getText().toString().trim();
                if (!texto.isEmpty()) {
                    presenter.validateUbicacion(texto);
                }
            }
        });

        presenter.verifarFoco();

        binding.swLote.setOnCheckedChangeListener(((buttonView, isChecked) -> {
            presenter.saveModoLote(isChecked);
        }));

        binding.scModoConteo.setOnCheckedChangeListener(((buttonView, isChecked) -> {
            presenter.saveModoLecture(isChecked);
        }));

        binding.tilProducto.getEditText().setOnEditorActionListener((textView, i, keyEvent) -> {
//            submit();
            return true;
        });

        /*binding.tilCantidad.getEditText().addTextChangedListener(new android.text.TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().contains(".")) {
                    tilCantidad.setError("decimal no permitido");
                    binding.btSend.setEnabled(false);
                } else {
                    binding.btSend.setEnabled(true);
                }

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });*/

        binding.tilProducto.getEditText().setOnEditorActionListener((textView, actionId, keyEvent) -> {
            if (actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_ACTION_DONE ||
                    (keyEvent != null && keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {

                if (tilLote.getVisibility() == View.VISIBLE) {
                    tilLote.requestFocus();
                } else if (tilSerie.getVisibility() == View.VISIBLE) {
                    tilSerie.requestFocus();
                } else {
                    if (presenter.isBarrido()) submit();
                    else tilCantidad.requestFocus();
                }
                return true;
            }
            return false;
        });
    }

    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            // Ya tenemos permiso
            startCameraPreview();
        } else {
            // No tenemos permiso, lo pedimos
            requestPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        presenter.detachView();
        getActivity().unregisterReceiver(this.mScanReceiver);
        closeRFID();

        /*SoundTool.getInstance(getContext()).release();
        RFID_INIT_STATUS = false;
        if (mRfidManager != null) {
            mRfidManager.disConnect();
            mRfidManager.release();
            Log.d(TAG, "onDestroyView: rfid服务关闭");
//            System.exit(0);
        }*/
    }

    @Override
    public void setupConteo(int conteo) {
        Log.d(TAG, "setupConteo conteo: " + conteo);
        tvConteo.setText(String.format(Locale.US, "%d", conteo));
    }

    @Override
    public void setupCameraScan(boolean active) {
        Log.d(TAG, "setupCameraScan active: " + active);
//        btScanUbicacion.setVisibility(UtilMethods.getVisibility(active));
//        btScanProducto.setVisibility(UtilMethods.getVisibility(active));
        cardView.setVisibility(UtilMethods.getVisibility(active));

        if (!active) return;


//        btScanUbicacion.setOnClickListener(v -> openCameraScanActivty(Constants.INTENT_SCAN_UBICACION));
//        btScanProducto.setOnClickListener(v -> openCameraScanActivty(Constants.INTENT_SCAN_PRODUCTO));
//        btScanLote.setOnClickListener(v -> openCameraScanActivty(Constants.INTENT_SCAN_LOTE));
//        btScanSerie.setOnClickListener(v -> openCameraScanActivty(Constants.INTENT_SCAN_SERIE));
    }

    @Override
    public void openRFID() {
        if(!isLibraryLoaded){
            UtilMethods.showToast("RFID no disponible en este dispositivo");
            return;
        }
        try {
            new Thread(() -> {
                int result = UfhData.UhfGetData.OpenUhf(57600, (byte) 0xff, 9, 0, null);
                if (result == 0) {
                    UfhData.UhfGetData.GetUhfInfo();
                    mHandler1.removeMessages(MSG_SHOW_PROPERTIES);
                    mHandler1.sendEmptyMessage(MSG_SHOW_PROPERTIES);
                } else {
                    UtilMethods.showToast("Error al abrir el RFID");
                }

            }).start();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void closeRFID() {
        isCanceled = true;
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (UfhData.isDeviceOpen()) {
            UfhData.UhfGetData.CloseUhf();
            UfhData.FirstOpen = false;
        }
    }

    @Override
    public void showParticipacionDialog(int nroConteo, String fchLimite) {
        if (dialogParticipacion != null && dialogParticipacion.isShowing()) return;

        String msg = "¿Participar en el siguiente conteo?";
        if (fchLimite != null && !fchLimite.isEmpty()) {
            msg += "\n\nTiempo restante: --:--";
        }

        dialogParticipacion = new CustomDialog.Builder(getContext())
                .setTitle("Ha finalizado el CONTEO " + nroConteo)
                .setMessage(msg)
                .setPositiveButtonLabel("SÍ, PARTICIPO")
                .setPositiveButtonlistener(() -> presenter.responderParticipacion(true))
                .setNegativeButtonLabel("NO")
                .setNegativeButtonlistener(() -> presenter.responderParticipacion(false))
                .setCancelable(false)
                .setIcon(R.drawable.ic_alert)
                .build();
        dialogParticipacion.show();
    }

    @Override
    public void updateCountdown(String time) {
        if (dialogParticipacion != null && dialogParticipacion.isShowing()) {
            dialogParticipacion.updateMessage("¿Participar en el siguiente CONTEO?\n\nTiempo restante: " + time);
        }
        if (dialogWaiting != null && dialogWaiting.isShowing()) {
            dialogWaiting.updateMessage("Esperando a que los demás operadores respondan...\n\nTiempo restante: " + time);
        }
    }

    @Override
    public void showWaitingForOthers() {
        if (dialogParticipacion != null && dialogParticipacion.isShowing()) {
            dialogParticipacion.dismiss();
        }
        if (dialogWaiting != null && dialogWaiting.isShowing()) return;

        dialogWaiting = new CustomDialog.Builder(getContext())
                .setTitle("Esperando confirmación")
                .setMessage("Esperando a que los demás operadores respondan...\n\nTiempo restante: --:--")
                .setCancelable(false)
                .setIcon(R.drawable.ic_alert)
                .build();
        dialogWaiting.show();
    }

    @Override
    public void hideWaitingForOthers() {
        if (dialogWaiting != null && dialogWaiting.isShowing()) {
            dialogWaiting.dismiss();
        }
    }

    @Override
    public void showAssignedProductsList(List<pe.com.dms.inventariosoft.data.models.ProductoAsignado> assignedProducts) {
        AssignedProductsAdapter adapter = new AssignedProductsAdapter(assignedProducts, producto -> {
            // Al hacer clic en un producto asignado, lo cargamos en los campos de escaneo
            tilUbicacion.getEditText().setText(producto.getUbicacionBase());
            tilProducto.getEditText().setText(producto.getCodProducto());
            presenter.searchProducto(producto.getCodProducto());

            // Bloqueamos ubicacion si tiene una base
            if (!TextUtils.isEmpty(producto.getUbicacionBase())) {
                tilUbicacion.getEditText().setEnabled(false);
                tilUbicacion.setEndIconDrawable(R.drawable.ic_lock);
            }

            tilCantidad.requestFocus();
            return kotlin.Unit.INSTANCE;
        });

        new CustomDialog.Builder(getContext())
                .setTitle("Productos Asignados")
                .setType(CustomDialog.DIALOG_TYPE.LIST)
                .setAdapter(adapter)
                .setPositiveButtonLabel("CERRAR")
                .build()
                .show();
    }

    void showResult(int poder) {
        tilPoder.getEditText().setText(String.valueOf(poder));
    }

    void openCameraScanActivty(int requestCode) {
        Intent intent = CodeCaptureActivity.newInstanceBarcode(getContext());
        startActivityForResult(intent, requestCode);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult requestCode: " + requestCode + ", resultCode: " + resultCode + ", data: " + data);
        if (resultCode == Activity.RESULT_OK) {
            if (data != null) {
                Barcode barcode = data.getParcelableExtra(CodeCaptureActivity.BarcodeObject);
                if (barcode == null) {
                    Log.e(TAG, "El objeto Barcode regresó nulo desde CodeCaptureActivity");
                    return;
                }
                String code = barcode.displayValue;
                Log.d(TAG, "onActivityResult code: " + code);
                Log.d(TAG, "onActivityResult requestCode: " + requestCode);
                if (requestCode == Constants.INTENT_SCAN_UBICACION) {
                    tilUbicacion.getEditText().setText("");
                    tilUbicacion.getEditText().setText(code);
                    // scanned = true;
                } else if (requestCode == Constants.INTENT_SCAN_PRODUCTO) {
                    tilProducto.getEditText().setText("");
                    tilProducto.getEditText().setText(code);
                    // scanned = true;
                } else if (requestCode == Constants.INTENT_SCAN_LOTE) {
                    tilLote.getEditText().setText("");
                    tilLote.getEditText().setText(code);
                    //  scanned = true;
                } else if (requestCode == Constants.INTENT_SCAN_SERIE) {
                    tilSerie.getEditText().setText("");
                    tilSerie.getEditText().setText(code);
                    // scanned = true;
                }
            }
        }
    }

    @Override
    public void setupLecturas(int lecturas) {
        Log.d(TAG, "setupLecturas lecturas: " + lecturas);
        tvLectura.setVisibility(View.GONE);
        TransitionSet set = new TransitionSet()
//                .addTransition(new Scale(0.7f))
                .addTransition(new Fade())
                .setInterpolator(new LinearOutSlowInInterpolator());

        TransitionManager.beginDelayedTransition(linearLayout3, set);
        tvLectura.setVisibility(View.VISIBLE);
        tvLectura.setText(String.valueOf(lecturas));
    }

    @Override
    public void setupUbicacion(String defaultUbicacion) {
        Log.d(TAG, "setupUbicacion defaultUbicacion: " + defaultUbicacion);
        if (TextUtils.isEmpty(defaultUbicacion)) {
            tilUbicacion.getEditText().setText("");
            tilUbicacion.getEditText().setEnabled(true);
            btScanUbicacion.setEnabled(true);
            tilUbicacion.setEndIconDrawable(R.drawable.ic_unlock);
        } else {
            tilUbicacion.getEditText().setText(defaultUbicacion);
            sheetFragment.getIdInventario(defaultUbicacion);
            tilUbicacion.getEditText().setEnabled(false);
            btScanUbicacion.setEnabled(false);
            tilUbicacion.setEndIconDrawable(R.drawable.ic_lock);
        }

        tilUbicacion.getEditText().addTextChangedListener((TextWatcher) s -> {
            if (s.isEmpty()) {
                tilUbicacion.setHelperText("Campo obligatorio");
                sheetFragment.getIdInventario("");
                enableButton(false);
            } else if (UtilMethods.isEmpty(s)) {
                tilUbicacion.getEditText().setText("");
                sheetFragment.getIdInventario("");
            } else {
                tilUbicacion.setHelperText(" ");
                sheetFragment.getIdInventario(s);
                enableButton(true);
            }
            /*if (scanned) {
                scanned = false;
                //nextFocus(1);
                if (tilLote.getVisibility() == View.VISIBLE) {
                    tilLote.requestFocus();
                } else if (tilSerie.getVisibility() == View.VISIBLE) {
                    tilSerie.requestFocus();
                } else {
                    tilProducto.requestFocus();
                }
            }*/
        });
    }
//displayInventarioDoneError
    @Override
    public void setupProducto(boolean isBarrido) {
        Log.d(TAG, "setupProducto isBarrido: " + isBarrido);
        binding.tEditProducto.setText("");
        binding.tEditDescripcion.setText("");
        binding.tilUbicacion.setHelperText("Campo obligatorio");
        enableButton(false);

        binding.tEditProducto.addTextChangedListener((TextWatcher) s -> {
            if(actualizandoProducto) {
                return;
            }
            if (!UtilMethods.isEmpty(s)) {
                presenter.searchProducto(s.trim());
            } else {
                binding.tEditDescripcion.setText("");
                tEditUm.setText("");
            }
            if (TextUtils.isEmpty(s)) {
                binding.tilProducto.setHelperText("Campo obligatorio");
                enableButton(false);
            } else {
                binding.tilProducto.setHelperText(" ");
                enableButton(true);
            }
        });
    }
// showProductoInfo
    @Override
    public void setupCantidad(boolean isBarrido) {
        Log.d(TAG, "setupCantidad isBarrido: " + isBarrido);
        tilCantidad.getEditText().addTextChangedListener((TextWatcher) s -> {
            Log.d(TAG, "setupCantidad s: " + s);
            Log.d(TAG, "setupCantidad s.isEmpty(): " + s.isEmpty());
            Log.d(TAG, "setupCantidad UtilMethods.isEmpty(s): " + UtilMethods.isEmpty(s));
            Log.d(TAG, "setupCantidad tilCantidad.hasFocus(): " + tilCantidad.hasFocus());
            Log.d(TAG, "setupCantidad isBarrido: " + isBarrido);
            /*if (tilCantidad.hasFocus() && !auto) {
                Log.d(TAG, "tiene el foco: ");
                tilProducto.requestFocus();
                tilCantidad.getEditText().setText("");
                return;
            }*/
            if (s.isEmpty()) {
                tilCantidad.setHelperText("Campo obligatorio");

                enableButton(false);
            } else if (UtilMethods.isEmpty(s)) {
                tilCantidad.getEditText().setText("");
            } else {
                tilCantidad.setHelperText(" ");
                tilCantidad.setErrorEnabled(false);
                enableButton(true);
            }
            /*if (scanned) {
                scanned = false;
                if (!presenter.isBarrido())
                    nextFocus(4);
            }*/
        });

        /*tilCantidad.getEditText().setOnFocusChangeListener((v, hasFocus) -> {
            Log.d(TAG, "setupCantidad hasFocus: " + hasFocus);
            Log.d(TAG, "setupCantidad producto vacio: " + (TextUtils.isEmpty(tilProducto.getEditText().getText().toString())));
            if (hasFocus) {
                if (TextUtils.isEmpty(tilProducto.getEditText().getText().toString())) {
                    tilProducto.getEditText().requestFocus();
                } else {
                    tilCantidad.requestFocus();
                }
            }
        });*/

        if (isBarrido) {
            tilCantidad.getEditText().setText("1");
            tilCantidad.setEnabled(false);
        } else {
            tilCantidad.getEditText().setText("");
            tilCantidad.setEnabled(true);
        }

    }

    @Override
    public void setupLote(boolean visible, boolean config) {
        Log.d(TAG, "setupLote visible: " + visible + ", config: " + config);
        binding.swLote.setChecked(visible);
        tilLote.setVisibility(UtilMethods.getVisibility(visible));
//        btScanLote.setVisibility(UtilMethods.getVisibility(visible && config));
        tilLote.getEditText().setText("");
        tilLote.setHelperText("Campo obligatorio");
        enableButton(false);
        tilLote.getEditText().addTextChangedListener((TextWatcher) s -> {
            if (s.isEmpty()) {
                tilLote.setHelperText("Campo obligatorio");
                enableButton(false);
            } else if (UtilMethods.isEmpty(s)) {
                tilLote.setHelperText(" ");
            } else {
                tilLote.setHelperText(" ");
                enableButton(true);
            }
            /*if (scanned) {
                scanned = false;
                nextFocus(3);
            }*/
        });
    }

    @Override
    public void setupSerie(boolean visible, boolean config) {
        Log.d(TAG, "setupSerie visible: " + visible + ", config: " + config);
        //binding.scSerie.setChecked(visible);
        tilSerie.setVisibility(UtilMethods.getVisibility(visible));
        btScanSerie.setVisibility(UtilMethods.getVisibility(visible && config));
        tilSerie.getEditText().setText("");
        tilSerie.setError("Campo obligatorio");
        tilSerie.setErrorEnabled(true);
        enableButton(false);
        tilSerie.getEditText().addTextChangedListener((TextWatcher) s -> {
            if (s.isEmpty()) {
                tilSerie.setError("Campo obligatorio");
                tilSerie.setErrorEnabled(true);
                enableButton(false);
            } else if (UtilMethods.isEmpty(s)) {
                tilSerie.getEditText().setText("");
            } else {
                tilSerie.setError("");
                tilSerie.setErrorEnabled(false);
                enableButton(true);
            }
            /*if (scanned) {
                scanned = false;
                nextFocus(4);
            }*/
        });

    }

    @Override
    public void setupModoConteo(int modo) {
        binding.scModoConteo.setChecked(modo == Constants.MODE_BARRIDO);
    }

    private void enableButton(boolean enable) {
        btSend.setEnabled(enable);
    }

    @Override
    public void setupFocoProducto() {
        clearedFocus();
        tilProducto.requestFocus();
    }

    @Override
    public void setupFocoUbicacion() {
        clearedFocus();
        if (TextUtils.isEmpty(tilUbicacion.getEditText().getText().toString())) {
            tilUbicacion.requestFocus();
        } else {
            setupFocoProducto();
        }
    }

    @Override
    public void setupConfirmation() {
        isShowConfirmation = true;
    }

    @OnClick({R.id.bt_savePoder, R.id.bt_scan})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bt_savePoder:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P){
                    if (RFID_INIT_STATUS == false) {
                        UtilMethods.showToast("Debe abrir el dispositivo antes de operar");
                    } else {
                        setCallback();
                        String str =  tilPoder.getEditText().getText().toString().trim();
                        if (TextUtils.isEmpty(str)) {
                            Toast.makeText(getActivity(), "Los parámetros de entrada no pueden estar vacíos！", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        int power = Integer.parseInt(str);
                        if (power<0 || power>33) {
                            Toast.makeText(getActivity(), "El valor ingresado no está dentro del rango de potencia (0 - 33)", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        mRfidManager.setOutputPower(mRfidManager.getReadId(), (byte) power);
                        setCallback();
                        int outputPower = mRfidManager.getOutputPower(mRfidManager.getReadId());
                    }
                } else{
                    if (!UfhData.isDeviceOpen()) {
                        UtilMethods.showToast("Debe abrir el dispositivo antes de operar");
                    } else {
                        new Thread(() -> {
                            int minFre = ((1 & 3) << 6) | (0 & 0x3F);
                            int maxFre = ((1 & 0x0c) << 4) | (20 & 0x3F);
                            int power = Integer.valueOf(tilPoder.getEditText().getText().toString());
                            UfhData.UhfGetData.SetUhfInfo((byte) maxFre, (byte) minFre, (byte) power, (byte) 0);
                        }).start();
                    }
                }

                break;
            case R.id.bt_scan:

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P){
                    if (RFID_INIT_STATUS == false) {
                        Log.e(TAG, "dispositivo no abierto API >= P");
                        UtilMethods.showToast("Debe abrir el dispositivo antes de operar");
                        setScanStatus(false);
                    } else {
                        Log.e(TAG, "dispositivo abierto");
                        if (btScan.getText().equals("Escanear")) {
                          //  presenter.removeAllLecturas();
                            setCallback();
                            btScan.setText("Detener");
                            setScanStatus(true);
                        } else {
                            btScan.setText("Escanear");
                            setScanStatus(false);
                        }
                    }
                } else{
                    if (!UfhData.isDeviceOpen()) {
                        Log.e(TAG, "dispositivo no abierto api < P");
                        UtilMethods.showToast("Debe abrir el dispositivo antes de operar");
                    } else {
                        Log.e(TAG, "dispositivo abierto");
                        if (timer == null) {
                            //Inicializacion del interruptor de sonido
                            UfhData.Set_sound(true);
                            UfhData.SoundFlag = false;
                            Scanflag = false;
                            int selectTime = 0;
                            int selectedEd = 1;
                            int TidFlag = 0;
                            UfhData.target = 0;
                            //    if (myAdapter != null) {

                            UfhData.scanResult6c.clear();
                            UfhData.Result6c.clear();
                            mHandler.removeMessages(MSG_UPDATE_LISTVIEW);
                            mHandler.sendEmptyMessage(MSG_UPDATE_LISTVIEW);
                            //      }

                            isCanceled = false;
                            timer = new Timer();
                            //
                            timer.schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    if (Scanflag) return;
                                    Scanflag = true;

                                    UfhData.read6c(selectedEd, TidFlag);
                                    try {
                                        Thread.sleep(selectTime * 100);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    mHandler.removeMessages(MSG_UPDATE_LISTVIEW);
                                    mHandler.sendEmptyMessage(MSG_UPDATE_LISTVIEW);
                                    Scanflag = false;
                                }
                            }, 0, SCAN_INTERVAL);
                            btScan.setText("Detener");
                        } else {
                            //cancelScan();
                            isCanceled = true;
                            UfhData.Set_sound(false);
                            UfhData.SoundFlag = false;
                            if (timer != null) {
                                timer.cancel();
                                timer = null;
                                btScan.setText("Escanear");
                            }
                        }

                    }
                }


                break;
//            case R.id.bt_add_ubicacion:
//                final String codUbicacion = UtilMethods.cleanString(tilUbicacion.getEditText().getText().toString());
//
//                if (TextUtils.isEmpty(codUbicacion)) {
//                    new CustomDialog.Builder(getContext())
//                            .setMessage("La ubicación no puede estar vacia")
//                            .setPositiveButtonLabel(getString(R.string.label_ok))
//                            .setTheme(R.style.AppTheme_Dialog_Error)
//                            .setIcon(R.drawable.ic_close)
//                            .build().show();
//                } else {
//                    new CustomDialog.Builder(getContext())
//                            .setMessage(String.format("¿Esta seguro que deseas crear la ubicación '%s'?", codUbicacion))
//                            .setPositiveButtonLabel(getString(R.string.label_yes))
//                            .setIcon(R.drawable.ic_logout)
//                            .setPositiveButtonlistener(() -> {
//                                presenter.createUbicacion(codUbicacion);
//                            })
//                            .build().show();
//                }
//                break;
        }
    }

    private void setLoading(int isVisible) {
        binding.layoutLoading.setVisibility(isVisible);

    }
    @Override
    public void showErrorDialog(String message) {
        btSend.setEnabled(true);
        CustomDialog dialog = new CustomDialog.Builder(getContext())
                .setMessage(message)
                .setIcon(R.drawable.ic_close)
                .setTheme(R.style.AppTheme_Dialog_Error)
                .setPositiveButtonLabel(getString(R.string.label_ok))
                .build();
        ModalKeyGuard.attach(dialog).setOnDismissListener(dialogInterface -> presenter.verifarFoco());
        dialog.show();
    }

    @Override
    public void showConfirmation(Lectura lectura) {
        new CustomDialog.Builder(getContext())
                .setMessage("¿Está seguro que desea guardar la lectura?")
                .setPositiveButtonLabel(getString(R.string.label_yes))
                .setNegativeButtonLabel(getString(R.string.label_no))

                .setPositiveButtonlistener(() -> {
                    presenter.onConfirmationAccepted(lectura);
                })
                .setNegativeButtonlistener(() -> {
                    btSend.setEnabled(true);

                })
                .build().show();
    }

    @Override
    public void showValidationError(String message) {
        btSend.setEnabled(true);
        CustomDialog dialog = new CustomDialog.Builder(getContext())
                .setMessage(message)
                .setTheme(R.style.AppTheme_Dialog_Warning)
                .setIcon(R.drawable.ic_alert)
                .setPositiveButtonLabel(getString(R.string.label_ok))
                .setPositiveButtonlistener(() -> {
                    if (message.contains("6 cifras")) {
                        //Development: Sting Lucana
                        //Date: 04/03/2019
                        //Reason: Se limpia la el cuadro de texto de cantidad si es que tuvo un valor
                        // de 6 cifras.
                        tilCantidad.getEditText().getText().clear();
                    }
                })
                .build();
        ModalKeyGuard.attach(dialog).setOnDismissListener(dialogInterface -> presenter.verifarFoco());
        dialog.show();
    }

    @Override
    public void showError(String message) {
        UtilMethods.showToast(message);
    }

    @Override
    public void ubicacionCreated(String codUbicacion, String almacen) {
        new CustomDialog.Builder(getContext())
                .setIcon(R.drawable.ic_check)
                .setMessage(String.format("La ubicacion \"%s\" se creó exitosamente en el Almacen \'%s\'", codUbicacion, almacen))
                .setPositiveButtonLabel(getString(R.string.label_ok))
                .build().show();
    }

    @Override
    public void lecturaCreated() {
        Log.d(TAG, "lecturaCreated: ");
        if (quick) {
            getActivity().setResult(Activity.RESULT_OK);
            getActivity().finish();
            return;
        }

        btSend.setEnabled(true);
        int count = UtilMethods.parseInt(tvLectura.getText().toString());

//        Se ignora valor de "cantidad".
//        Segun el analista se muestra el conteo de lecturas independiente del stock
        setupLecturas(count + 1);

        binding.tilProducto.getEditText().setText("");
        binding.tEditDescripcion.setText("");
        tEditUm.setText("");
        tilLote.getEditText().setText("");
        tilSerie.getEditText().setText("");
        if (tilCantidad.isEnabled()) {
            tilCantidad.getEditText().setText("");
        }


        presenter.verifarFoco();
    }
// CANELAZA

  /*  @Override
    public void showProductoInfo(String descripcion, String unidadMedida) {
        Log.d(TAG, "showProductoInfo: " + descripcion + " - " + unidadMedida);
        if (isAdded() && getActivity() != null) {
            getActivity().runOnUiThread(() -> {
                binding.tEditDescripcion.setText(descripcion);
                tEditUm.setText(unidadMedida);
            });
        }
    }
*/
  // checkpoint
  // checkpoint
  @Override
  public void showProductoInfo(Producto producto, boolean matchedByCode) {
      if (!isAdded() || getActivity() == null) {
          return;
      }

      getActivity().runOnUiThread(() -> {
          actualizandoProducto = true;

          if (producto == null) {
              binding.tEditDescripcion.setText("");
              tEditUm.setText("");
              productoMatchedByCode = true;
              actualizandoProducto = false;
              return;
          }
            productoMatchedByCode = matchedByCode;

          if (matchedByCode) {
              binding.tEditDescripcion.setText(producto.getDescripcion());
          } else {
              binding.tEditDescripcion.setText(producto.getCodigo());
          }
          tEditUm.setText(producto.getUnidadMedida());
          actualizandoProducto = false;
      });
  }
// submit
    @Override
    public void lecturaDeleted() {
        Log.d(TAG, "lecturaDeleted: ");
        int count = UtilMethods.parseInt(tvLectura.getText().toString());
        setupLecturas(count - 1);

    }

    @Override
    public void displayInventarioDoneError(String message) {

        Log.d(TAG, "========== DIALOGO INVENTARIO FINALIZADO ==========");
        Log.d(TAG, "Mensaje: " + message);
        Log.d(TAG, "nextInv ACTUAL: " + nextInv);

        dialogError = new CustomDialog.Builder(getContext())
                .setIcon(R.drawable.ic_alert)
                .setTheme(R.style.AppTheme_Dialog_Warning)
                .setMessage(message)
                .setPositiveButtonLabel(getString(R.string.label_ok))
                .setPositiveButtonlistener(() -> {
                    Log.d(TAG, "========== USUARIO PRESIONO OK ==========");
                    presenter.requestNextInventario() ;
                })
                .build();
        ModalKeyGuard.attach(dialogError);
        if (!dialogError.isShowing()) {
            dialogError.show();
        }
    }

    @Override
    public void initSheet() {
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        sheetFragment = LectureListFragment.newInstance(false);
        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.fl_bottom, sheetFragment)
                .commit();

        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    ivExpandable.setImageResource(R.drawable.ic_expand_more);
                    presenter.getLectura();
                    sheetFragment.refreshList(true);
                } else if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    ivExpandable.setImageResource(R.drawable.ic_expand_less);
                    sheetFragment.swFiltro.setChecked(true);
                    sheetFragment.refreshList(true);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                Log.d("aqui", "swipe " + slideOffset);

            }
        });
    }

    @Override
    public void openSheet() {
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    @Override
    public void closeSheet() {
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    @Override
    public void refreshView() {
        initSheet();
        presenter.onViewCreated();
    }

    @Override
    public void goToConfig() {
        ((MainActivity) getActivity()).setupNavigationMenuView();
    }

    @Override
    public void goToHome() {
        if (getActivity() != null && getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).navView.setSelectedItemId(R.id.nav_home);
        }
    }

    private void submit() {
        clearedFocus();
        btSend.setEnabled(false);

        String codigoProductoReal = productoMatchedByCode
                            ? tilProducto.getEditText().getText().toString().trim()
                            : binding.tEditDescripcion.getText().toString().trim();

        presenter.sendLecture(
                tilUbicacion.getEditText().getText().toString(),
                codigoProductoReal,
                tilLote.getEditText().getText().toString(),
                tilSerie.getEditText().getText().toString(),
                tilCantidad.getEditText().getText().toString()
        );
    }

    private void clearedFocus() {
        Timber.d("clearedFocus: ");
        tilUbicacion.clearFocus();
        tilProducto.clearFocus();
        tilLote.clearFocus();
        tilSerie.clearFocus();
        tilCantidad.clearFocus();
    }

    private void initRfidApiLbl28() {
        Log.d(TAG, "initRfidApiLbl28");
        USDKManager.getInstance().init(getContext(),new USDKManager.InitListener() {
            @Override
            public void onStatus(USDKManager.STATUS status) {
                if ( status == USDKManager.STATUS.SUCCESS) {
                    Log.d(TAG, "initRfidService: éxito de estado");
                    mRfidManager =   USDKManager.getInstance().getRfidManager();//获得 RfidManager 实例

                    //设置监听器 chin chen chan chin chen
    /*
    *
    *           なんでみんな日本語とか中国語とか北京語でコメントしてるんだよ、わかんねえよ、もう疲れたよ（笑）
    *           他們為什麼用日文、中文、國語評論？我不知道，老兄，我累了 xd
    *
    * */
//                    if (mRfidManager.connectCom("/dev/ttyHSL0", 115200)) {//连接串口
//                        mRfidManager.registerCallback(callback);//设置数据回调的监听
//
//                    }
                    // 设置波特率
                    if (mRfidManager.connectCom("/dev/ttyHSL0", 115200)) {

                        setCallback();
                        SystemClock.sleep(100);
                        //盘点


                        String mf = mRfidManager.getModuleFirmware();
                        Log.d(TAG, "initRfidService:   mf："+mf);

                        mRfidManager.getFirmwareVersion(mRfidManager.getReadId());

                        mRfidManager.setOutputPower(mRfidManager.getReadId(),(byte) 30);
                    }
                }else {
                    Log.d(TAG, "initRfidService: estado fallido。");
                }
            }
        });
    }

    public void setCallback(){

        mRfidManager.registerCallback(new ScanCallback());

    }

    class ScanCallback implements IRfidCallback {


        @Override
        public void onInventoryTag(byte b, String s, String s1, String s2, byte b1, String s3, String s4, int i, int i1, String s5) {

        }

        public void onInventory(String EPC, final String TID, final String strRSSI) {

            if (!isInv){
                return;
            }

            final String s2 = EPC.replace(" ","");
            String mapContainStr = null;
            if (!TextUtils.isEmpty(TID)){
                mapContainStr = TID;
            }else {
                mapContainStr = s2;
            }
            final String mapContainStrFinal = mapContainStr;
            Log.d(TAG, "onInventoryTag: EPC: " + s2);
            SoundTool.getInstance(getContext()).playBeep(1);
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    {

                        long nowTime = System.currentTimeMillis();
                        if ((nowTime - time)>1){
                            time = nowTime;
                            List<String> ls = new ArrayList<>();
                            ls.add(s2);

                            Log.d(TAG, "onInventoryTag: data = " + Arrays.toString(ls.toArray()));

                            presenter.saveRFID(ls, "");
                        }


                    }
                }
            });
        }

        /**
         * 盘存结束回调(Inventory Command Operate End)
         *
         * @param i  当前天线ID
         * @param i1 当前指令盘存标签数量
         * @param i2 读取速度
         * @param i3 总共读取次数
         * @param b  指令cmd
         * @
         */

        public void onInventoryTagEnd(int i, int i1, int i2, int i3, byte b)  {
            Log.d(TAG, "onInventoryTag: 当前指令盘存标签数量" + i1);
        }


        public void onOperationTag(String s, String s1, String s2, String s3, int i, byte b, byte b1)  {
            Log.d(TAG, "onInventoryTag: EPC: " + s2);
        }


        public void onOperationTagEnd(int i)  {

        }


        public void refreshSetting(RfidDate rfidDate)  {
            Log.d(TAG, "refreshSetting: test");
            mRfidDate = rfidDate;
            final String power = String.valueOf(rfidDate.getbtAryOutputPower()[0] & 0xFF);
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //                Toast.makeText(getActivity(), s, Toast.LENGTH_SHORT).show();
                    tilPoder.getEditText().setText(power);
                }
            });
//           showFirmware();
            handler.sendEmptyMessageDelayed(0,1);
        }
//他們為什麼用日文、中文、國語評論？我不知道，老兄，我累了 xd
        /**
         * (指令操作状态回调)Command operate status
         * @param b  指令cmd对应CMDCode.class
         * @param b1 执行状态对应ErrorCode.class
         * @
         */

        public void onExeCMDStatus(byte b, byte b1)  {




//            if (b == CMDCode.WRITE_TAG){
//                if (b1 == ErrorCode.SUCCESS){
//                    //写入成功
//                }else {
//                    //写入失败
//                }
//            }




        }
    }
// getLectura
    private void setScanStatus(boolean isScan) {

        if (isScan) {


            Log.v(TAG,"--- customizedSessionTargetInventory()   ----");

//                readTagOnce();
            isInv = true;
            mRfidManager.customizedSessionTargetInventory(mRfidManager.getReadId(), (byte) 0, (byte) 0, (byte) 1);
        } else {
            Log.v(TAG,"--- stopInventory()   ----");
            isInv =  false;
            mRfidManager.stopInventory();

        }
    }

    private void startCameraPreview() {

        viewFinder.setScaleType(PreviewView.ScaleType.FILL_CENTER);

        ListenableFuture<ProcessCameraProvider> cameraProviderFuture =
                ProcessCameraProvider.getInstance(getContext());

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();

                Preview preview = new Preview.Builder().build();

                preview.setSurfaceProvider(viewFinder.getSurfaceProvider());

                ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build();

                BarcodeScanner scanner =  BarcodeScanning.getClient();

                imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(getContext()), imageProxy -> {
                    processImageProxy(scanner, imageProxy);
                });



                CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;

                cameraProvider.unbindAll();
                cameraProvider.bindToLifecycle(getViewLifecycleOwner(), cameraSelector, preview,imageAnalysis);

            } catch (Exception e) {
                Log.e(TAG, "Error al abrir cámara: " + e.getMessage());
            }
        }, ContextCompat.getMainExecutor(getContext()));
    }


    private void processImageProxy(BarcodeScanner scanner, ImageProxy imageProxy) {
        if (imageProxy.getImage() == null) {
            imageProxy.close();
            return;
        }

        if (viewFinder == null || !isAdded() || getView() == null) {
            imageProxy.close();
            return;
        }

        InputImage image = InputImage.fromMediaImage(
                imageProxy.getImage(),
                imageProxy.getImageInfo().getRotationDegrees()
        );

        int rotationDegrees = imageProxy.getImageInfo().getRotationDegrees();

        scanner.process(image)
                .addOnSuccessListener(barcodes -> {
                    if (viewFinder == null || !isAdded() || getView() == null) return;

                    Rect roi = getRoiFromOverlay(imageProxy, rotationDegrees);
                    for (com.google.mlkit.vision.barcode.common.Barcode barcode : barcodes) {
                        android.graphics.Rect bounds = barcode.getBoundingBox();
                        if (bounds != null && android.graphics.Rect.intersects(roi, bounds)) {
                            String code = barcode.getRawValue();
                            if (code != null && !code.isEmpty()) {
                                onBarcodeDetected(code.trim());
                                break;
                            }
                        }
                    }
                })
                .addOnFailureListener(e -> Log.e(TAG, "Barcode error: " + e.getMessage()))
                .addOnCompleteListener(task -> imageProxy.close());
    }

    private void onBarcodeDetected(String code) {
        long currentTime = System.currentTimeMillis();

        if (code.equals(lastValue) && (currentTime - lastReadTime < 4000)) {
            return;
        }
        lastValue = code;
        lastReadTime = currentTime;
        SoundTool.getInstance(getContext()).playBeep(1);

        if (tilUbicacion.hasFocus() && tilUbicacion.isEnabled()) {
            tilUbicacion.getEditText().setText(code);
            tilProducto.requestFocus();
        } else if (tilProducto.hasFocus()) {
            tilProducto.getEditText().setText(code);
            presenter.searchProducto(code);
            if (tilLote.getVisibility() == View.VISIBLE) {
                tilLote.requestFocus();
            } else if (tilSerie.getVisibility() == View.VISIBLE) {
                tilSerie.requestFocus();
            } else {
                if (presenter.isBarrido()) submit();
                else tilCantidad.requestFocus();
            }
        } else if (tilLote.hasFocus() && tilLote.getVisibility() == View.VISIBLE) {
            tilLote.getEditText().setText(code);
            if (tilSerie.getVisibility() == View.VISIBLE) tilSerie.requestFocus();
            else tilCantidad.requestFocus();
        } else if (tilSerie.hasFocus() && tilSerie.getVisibility() == View.VISIBLE) {
            tilSerie.getEditText().setText(code);
            if (presenter.isBarrido()) submit();
            else tilCantidad.requestFocus();
        }
    }

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    // Si el usuario da permiso, iniciamos la camara
                    startCameraPreview();
                } else {
                    // Si lo deniega, avisamos
                    UtilMethods.showToast("Se necesita el permiso de cámara para escanear.");
                }
            });

    private void stopCamera() {
        try {
            ListenableFuture<ProcessCameraProvider> cameraProviderFuture =
                    ProcessCameraProvider.getInstance(getContext());

            cameraProviderFuture.addListener(() -> {
                try {
                    ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                    cameraProvider.unbindAll(); // LIBERA EL HARDWARE
                    Log.d(TAG, "Cámara liberada correctamente.");
                } catch (Exception e) {
                    Log.e(TAG, "Error al liberar cámara: " + e.getMessage());
                }
            }, ContextCompat.getMainExecutor(getContext()));
        } catch (Exception e) {
            Log.e(TAG, "No se pudo obtener el CameraProvider");
        }
    }

    private Rect getRoiFromOverlay(ImageProxy imageProxy, int rotationDegrees) {
        int imageWidth  = imageProxy.getWidth();
        int imageHeight = imageProxy.getHeight();

        if (rotationDegrees == 90 || rotationDegrees == 270) {
            imageWidth  = imageProxy.getHeight();
            imageHeight = imageProxy.getWidth();
        }

        if (viewFinder == null || viewScannerFrame == null) {
            return new Rect(0, 0, imageWidth, imageHeight);
        }


        int previewWidth  = viewFinder.getWidth();
        int previewHeight = viewFinder.getHeight();


        if (previewWidth == 0 || previewHeight == 0) {
            return new Rect(0, 0, imageWidth, imageHeight);
        }

        float scaleX = (float) imageWidth  / previewWidth;
        float scaleY = (float) imageHeight / previewHeight;
        float scale  = Math.min(scaleX, scaleY);

        float offsetX = (imageWidth  - previewWidth  * scale) / 2f;
        float offsetY = (imageHeight - previewHeight * scale) / 2f;

        int frameWidth  = viewScannerFrame.getWidth();
        int frameHeight = viewScannerFrame.getHeight();
        int frameLeft   = (previewWidth  - frameWidth)  / 2;
        int frameTop    = (previewHeight - frameHeight) / 2;

        int left   = (int) (frameLeft * scale + offsetX);
        int top    = (int) (frameTop  * scale + offsetY);
        int right  = (int) ((frameLeft + frameWidth)  * scale + offsetX);
        int bottom = (int) ((frameTop  + frameHeight) * scale + offsetY);

        return new Rect(left, top, right, bottom);
    }

}

