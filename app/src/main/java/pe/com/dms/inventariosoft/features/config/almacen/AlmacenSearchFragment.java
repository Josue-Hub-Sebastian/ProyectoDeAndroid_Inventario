package pe.com.dms.inventariosoft.features.config.almacen;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.gson.Gson;
import java.util.List;
import pe.com.dms.inventariosoft.R;
import pe.com.dms.inventariosoft.data.models.Almacen;
import pe.com.dms.inventariosoft.data.pojos.ListAlmacen;
import pe.com.dms.inventariosoft.databinding.FragmentAlmacenSearchBinding;
import pe.com.dms.inventariosoft.features.shared.BaseFragment;
import pe.com.dms.inventariosoft.utils.Constants;
import pe.com.dms.inventariosoft.utils.SimpleDividerItemDecoration;
import pe.com.dms.inventariosoft.utils.interfaces.TextWatcher;

public class AlmacenSearchFragment extends BaseFragment implements AlmacenSearchAdapter.ISelection {

    public static final String TAG = AlmacenSearchFragment.class.getSimpleName();

    private AlmacenSearchAdapter adapter;
    private List<Almacen> almacenList;

    public AlmacenSearchFragment() {
        // Required empty public constructor
    }

    public static AlmacenSearchFragment newInstance(String list) {
        AlmacenSearchFragment fragment = new AlmacenSearchFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.EXTRA_LIST_ALMACEN, list);
        fragment.setArguments(bundle);
        return fragment;
    }

    private FragmentAlmacenSearchBinding binding;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentAlmacenSearchBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        return view;
    }

    @Override
    protected void setupVariables() {
        super.setupVariables();
        almacenList = new Gson().fromJson(
                getArguments().getString(Constants.EXTRA_LIST_ALMACEN),
                ListAlmacen.class).getList();
    }

    @Override
    protected void setupViews(View view) {
        super.setupViews(view);

        binding.rvData.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        binding.rvData.addItemDecoration(new SimpleDividerItemDecoration(getContext(), R.drawable.line_divider_black));
        binding.rvData.setLayoutManager(layoutManager);
        adapter = new AlmacenSearchAdapter(almacenList, this);

        binding.rvData.setAdapter(adapter);

        binding.etSearchbox.addTextChangedListener((TextWatcher) filter -> adapter.updateList(almacenList, filter));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onSelect(Almacen almacen) {
        Intent intent = new Intent();
        intent.putExtra(Constants.EXTRA_LIST_ALMACEN_OBJ, new Gson().toJson(almacen));
        getActivity().setResult(Activity.RESULT_OK, intent);
        getActivity().finish();
    }
}
