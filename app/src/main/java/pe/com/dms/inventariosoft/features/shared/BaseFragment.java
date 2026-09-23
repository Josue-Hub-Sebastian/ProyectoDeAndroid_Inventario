package pe.com.dms.inventariosoft.features.shared;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.View;

import pe.com.dms.inventariosoft.injection.ActivityComponent;

public abstract class BaseFragment extends Fragment {

    public BaseFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupVariables();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupViews(view);
    }

    protected void setupVariables() {

    }

    protected void setupViews(View view) {

    }

    protected BaseActivity getBaseActivity() {
        return (BaseActivity) getActivity();
    }

    protected ActivityComponent getActivityComponent() {
        return getBaseActivity().getActivityComponent();
    }

    /**
     * Si el valor devuelto es TRUE, se continua con la ejecucion del onBackPressed del Activity
     *
     * @return
     */
    public boolean onBackPressed() {
        return true;
    }
}
