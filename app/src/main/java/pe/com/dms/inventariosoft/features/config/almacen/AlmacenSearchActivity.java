package pe.com.dms.inventariosoft.features.config.almacen;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.google.gson.Gson;
import java.util.List;
import pe.com.dms.inventariosoft.R;
import pe.com.dms.inventariosoft.data.models.Almacen;
import pe.com.dms.inventariosoft.data.pojos.ListAlmacen;
import pe.com.dms.inventariosoft.databinding.AppBarMainBinding;
import pe.com.dms.inventariosoft.features.shared.BaseActivity;
import pe.com.dms.inventariosoft.utils.Constants;

public class AlmacenSearchActivity extends BaseActivity {


    public static Intent newInstance(Context context, List<Almacen> almacenList) {
        Intent intent = new Intent(context, AlmacenSearchActivity.class);
        ListAlmacen listAlmacen = new ListAlmacen(almacenList);
        intent.putExtra(Constants.EXTRA_LIST_ALMACEN, new Gson().toJson(listAlmacen));
        return intent;
    }

    private AppBarMainBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = AppBarMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        setupNavigation();

        setTitle("Seleccionar almacén");

        String listAlmacen = getIntent().getStringExtra(Constants.EXTRA_LIST_ALMACEN);
        replaceFragment(AlmacenSearchFragment.newInstance(listAlmacen),
                R.id.fl_main,
                AlmacenSearchFragment.TAG);
    }

    private void setupNavigation() {
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
        super.onBackPressed();
    }
}
