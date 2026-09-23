package pe.com.dms.inventariosoft.features.sync;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.StyleRes;
import androidx.appcompat.widget.AppCompatImageView;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Locale;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import pe.com.dms.inventariosoft.App;
import pe.com.dms.inventariosoft.R;
import pe.com.dms.inventariosoft.injection.ActivityComponent;
import pe.com.dms.inventariosoft.injection.DaggerActivityComponent;

public class SyncDialog extends Dialog implements SyncContract.View {

    @BindView(R.id.bt_ok)
    Button btOk;
    @BindView(R.id.iv_icon)
    AppCompatImageView ivIcon;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.tv_percentage)
    TextView tvPercentage;
    @BindView(R.id.tv_quantity)
    TextView tvQuantity;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_message)
    TextView tvMessage;

    @Inject
    SyncPresenter presenter;

    private IButton mButtonlistener;

    public SyncDialog(@NonNull Context context) {
        super(context);
    }

    public SyncDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_progress_bar);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        ButterKnife.bind(this);

        ActivityComponent activityComponent = DaggerActivityComponent.builder()
                .appComponent(App.get(getContext()).getAppComponent())
                .build();
        activityComponent.inject(this);
        presenter.attachView(this);

        setCancelable(false);

        btOk.setVisibility(View.VISIBLE);
        btOk.setText(getContext().getString(R.string.label_ok));

        presenter.setupView();

    }

    @Override
    protected void onStop() {
        presenter.detachView();
        super.onStop();
    }

    @Override
    public void setupDialog(String title, String message, boolean download) {
        progressBar.setVisibility(View.GONE);
        tvPercentage.setVisibility(View.GONE);
        tvQuantity.setVisibility(View.GONE);
        ivIcon.setImageResource(download ? R.drawable.ic_cloud_download : R.drawable.ic_cloud_upload);
        tvTitle.setText(title);
        tvMessage.setText(message);
        btOk.setOnClickListener(view -> presenter.startSync());
    }

    @Override
    public void startSync() {
        progressBar.setVisibility(View.VISIBLE);
        tvPercentage.setVisibility(View.VISIBLE);
        tvQuantity.setVisibility(View.VISIBLE);

        tvMessage.setText("Espere por favor...");
        btOk.setVisibility(View.GONE);
    }

    @Override
    public void updateProgress(int doneItems, int totalItems) {

        if (doneItems == totalItems) {
            presenter.syncSuccess();
        }

        tvQuantity.setText(String.format(Locale.US, "%d/%d", doneItems, totalItems));
        int percentage = (totalItems == 0) ? 100 : (doneItems * 100 / totalItems);
        tvPercentage.setText(String.format(Locale.US, "%d %%", percentage));
        progressBar.setProgress(percentage);

    }

    @Override
    public void syncSuccess() {
        btOk.setVisibility(View.VISIBLE);
        tvMessage.setText("La sincronización finalizó exitosamente");

        btOk.setOnClickListener(view -> {
            SyncDialog.this.dismiss();
            SyncDialog.this.cancel();
            mButtonlistener.onButtonClick(true);
        });
    }

    @Override
    public void syncError(String message) {
        btOk.setVisibility(View.VISIBLE);
        tvMessage.setText("Ocurrio un error!");

        btOk.setOnClickListener(view -> {
            SyncDialog.this.dismiss();
            SyncDialog.this.cancel();
            mButtonlistener.onButtonClick(false);
        });
    }

    public interface IButton {
        void onButtonClick(boolean success);
    }

    public static class Builder {
        private Context context;
        private IButton buttonlistener;
        private int theme = R.style.AppTheme_Dialog;

        public Builder(Context context) {
            this.context = context;
        }

        public SyncDialog build() {
            SyncDialog dialog = new SyncDialog(context, theme);
            dialog.mButtonlistener = buttonlistener;

            return dialog;
        }

        public Builder setButtonlistener(IButton iButton) {
            this.buttonlistener = iButton;
            return this;
        }

        public Builder setTheme(@StyleRes int theme) {
            this.theme = theme;
            return this;
        }
    }
}