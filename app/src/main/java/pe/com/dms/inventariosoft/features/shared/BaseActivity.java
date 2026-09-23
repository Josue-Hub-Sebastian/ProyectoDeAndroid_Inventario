package pe.com.dms.inventariosoft.features.shared;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.IdRes;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import pe.com.dms.inventariosoft.App;
import pe.com.dms.inventariosoft.injection.ActivityComponent;
import pe.com.dms.inventariosoft.injection.DaggerActivityComponent;

public abstract class BaseActivity extends AppCompatActivity {
    String TAG = BaseActivity.class.getSimpleName();

    protected View lyProgress;
    private ActivityComponent activityComponent;

    protected void replaceFragment(Fragment fragment, @IdRes int layoutId,
                                   String tag) {
        Log.e(TAG, "replaceFragment tag: " + tag);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(layoutId, fragment, tag)
                .commit();
    }

    public void showLoading() {
        if (lyProgress == null) return;
        lyProgress.setVisibility(View.VISIBLE);
    }

    public void hideLoading() {
        if (lyProgress == null) return;
        lyProgress.setVisibility(View.GONE);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        buildComponent();
        setupVariables();
    }

    protected void buildComponent() {
        Log.e(TAG, "buildComponent: ");
        activityComponent = DaggerActivityComponent.builder()
                .appComponent(App.get(this).getAppComponent())
                .build();
    }

    public ActivityComponent getActivityComponent() {
        if (activityComponent == null)
            buildComponent();
        return activityComponent;
    }

    protected void setupVariables() {
    }

    protected Context getContext() {
        return this;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            Log.e(TAG, "onActivityResult requestCode: " + requestCode + ", resultCode: " + resultCode + ", data: " + data);
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }
}
