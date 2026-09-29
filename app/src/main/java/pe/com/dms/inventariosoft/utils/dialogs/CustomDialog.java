package pe.com.dms.inventariosoft.utils.dialogs;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.StyleRes;
import androidx.constraintlayout.widget.Group;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import java.security.PublicKey;

import butterknife.BindView;
import butterknife.ButterKnife;
import pe.com.dms.inventariosoft.R;

/**
 * Created by Alvaro Santa Cruz on 20/09/2017.
 */

public class CustomDialog extends Dialog {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_message)
    TextView tvMessage;
    @BindView(R.id.bt_negative)
    Button btNegative;
    @BindView(R.id.bt_positive)
    Button btPositive;
    @BindView(R.id.iv_icon)
    AppCompatImageView ivIcon;
    @BindView(R.id.rv_data)
    RecyclerView rvData;
    @BindView(R.id.ly_icon)
    Group lyIcon;

    private String mTitle;
    private String mMessage;
    private boolean mCancelable;
    private boolean mPositiveButtonFlag;
    private boolean mNegativeButtonFlag;
    private String mPositiveButtonLabel;
    private String mNegativeButtonLabel;
    private IButton mPositiveButtonlistener;
    private IButton mNegativeButtonlistener;
    private RecyclerView.Adapter mAdapter;
    private int mIcon;
    private DIALOG_TYPE mType;
    private Boolean mIsBtPositiveUnique;

    public CustomDialog(@NonNull Context context) {
        super(context);
    }

    public CustomDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_base);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        ButterKnife.bind(this);

        setCancelable(mCancelable);
        if (!TextUtils.isEmpty(mTitle)) {
            tvTitle.setVisibility(View.VISIBLE);
            tvTitle.setText(mTitle);
        }

        if (!TextUtils.isEmpty(mMessage)) {
            tvMessage.setText(mMessage);
        }

        switch (mType) {
            case NORMAL:
                tvMessage.setVisibility(View.VISIBLE);
                break;
            case LIST:
                rvData.setVisibility(View.VISIBLE);
                setListAdapter(mAdapter);
                break;
        }

        if (mIcon == -1) {
            lyIcon.setVisibility(View.GONE);
        } else {
            lyIcon.setVisibility(View.VISIBLE);
            ivIcon.setImageResource(mIcon);
        }
        if (!mIsBtPositiveUnique) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                btPositive.setBackgroundTintList(ContextCompat.getColorStateList(getContext(),R.color.textColorPrimary));
                btPositive.setTextColor(R.color.colorPrimary); // yellow
            }
        }

        setButton(btPositive, mPositiveButtonFlag, mPositiveButtonLabel, mPositiveButtonlistener);
        setButton(btNegative, mNegativeButtonFlag, mNegativeButtonLabel, mNegativeButtonlistener);
    }

    public void updateMessage(String message) {
        if (tvMessage != null) {
            tvMessage.setText(message);
        }
    }


    private void setListAdapter(RecyclerView.Adapter adapter) {
        if (adapter == null) return;

        rvData.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
//        rvData.addItemDecoration(new SimpleDividerItemDecoration(getContext(), R.drawable.line_divider_black));
        rvData.setLayoutManager(layoutManager);
        rvData.setAdapter(adapter);
    }

    private void setButton(TextView button, boolean flag, String label, final IButton iButton) {
        if (!flag) {
            button.setVisibility(View.GONE);
            return;
        }

        button.setVisibility(View.VISIBLE);
        button.setText(label);
        button.setOnClickListener(view -> {
            CustomDialog.this.dismiss();
            CustomDialog.this.cancel();
            if (iButton != null) {
                iButton.onButtonClick();
            }
        });
    }

    public enum DIALOG_TYPE {
        NORMAL, LIST
    }

    public interface IButton {
        void onButtonClick();
    }

    public static class Builder {
        private Context context;
        private String title = "";
        private RecyclerView.Adapter adapter;
        private String message = "";
        private boolean cancelable = true;
        private DIALOG_TYPE type = DIALOG_TYPE.NORMAL;
        private boolean positiveButtonFlag = false;
        private boolean negativeButtonFlag = false;
        private String positiveButtonLabel = "Ok";
        private String negativeButtonLabel = "Cancel";
        private IButton positiveButtonlistener;
        private IButton negativeButtonlistener;
        private int theme = R.style.AppTheme_Dialog;
        private int icon = -1;
        private Boolean isBtPositiveUnique = true;
        private boolean useKeyGuard = true;

        public Builder(Context context) {
            this.context = context;
        }

        public CustomDialog build() {
            CustomDialog dialog = new CustomDialog(context, theme);
            dialog.mTitle = title;
            dialog.mMessage = message;
            dialog.mType = type;
            dialog.mCancelable = cancelable;
            dialog.mPositiveButtonFlag = positiveButtonFlag;
            dialog.mNegativeButtonFlag = negativeButtonFlag;
            dialog.mPositiveButtonLabel = positiveButtonLabel;
            dialog.mNegativeButtonLabel = negativeButtonLabel;
            dialog.mPositiveButtonlistener = positiveButtonlistener;
            dialog.mNegativeButtonlistener = negativeButtonlistener;
            dialog.mAdapter = adapter;
            dialog.mIcon = icon;
            dialog.mIsBtPositiveUnique = isBtPositiveUnique;
            if (useKeyGuard) {
                ModalKeyGuard.attach(dialog);
            }
            return dialog;
        }

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setMessage(String message) {
            this.message = message;
            return this;
        }

        public Builder setPositiveButtonLabel(String label) {
            this.positiveButtonLabel = label;
            positiveButtonFlag = true;
            return this;
        }

        public Builder setPositiveButtonlistener(IButton iButton) {
            this.positiveButtonlistener = iButton;
            positiveButtonFlag = true;
            return this;
        }

        public Builder setNegativeButtonLabel(String label) {
            this.negativeButtonLabel = label;
            negativeButtonFlag = true;
            return this;
        }

        public Builder setNegativeButtonlistener(IButton iButton) {
            this.negativeButtonlistener = iButton;
            negativeButtonFlag = true;
            return this;
        }

        public Builder setCancelable(boolean cancelable) {
            this.cancelable = cancelable;
            return this;
        }

        public Builder setTheme(@StyleRes int theme) {
            this.theme = theme;
            return this;
        }

        public Builder setIcon(@DrawableRes int icon) {
            this.icon = icon;
            return this;
        }

        public Builder setBooleanPostivieUnique(Boolean isUnique) {
            this.isBtPositiveUnique = isUnique;
            return this;
        }

        public Builder setType(DIALOG_TYPE type) {
            this.type = type;
            return this;
        }

        public Builder setAdapter(RecyclerView.Adapter adapter) {
            this.adapter = adapter;
            return this;
        }

        public Builder setUseKeyGuard(boolean useKeyGuard) {
            this.useKeyGuard = useKeyGuard;
            return this;
        }
    }
}