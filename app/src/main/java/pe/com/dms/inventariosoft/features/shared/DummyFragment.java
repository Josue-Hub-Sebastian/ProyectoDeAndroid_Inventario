package pe.com.dms.inventariosoft.features.shared;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import pe.com.dms.inventariosoft.R;

public class DummyFragment extends BaseFragment {

    private static final String LABEL_NAME = "Nombre";

    @BindView(R.id.et_dummy)
    TextView etDummy;

    Unbinder unbinder;

    private String dummyLabel;

    public DummyFragment() {
        // Required empty public constructor
    }

    public static DummyFragment newInstance(String param1) {
        DummyFragment fragment = new DummyFragment();
        Bundle args = new Bundle();
        args.putString(LABEL_NAME, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            dummyLabel = getArguments().getString(LABEL_NAME);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dummy, container, false);
        unbinder = ButterKnife.bind(this, view);
        etDummy.setText(dummyLabel);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
