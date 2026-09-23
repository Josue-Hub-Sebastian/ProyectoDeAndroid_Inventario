package pe.com.dms.inventariosoft.features.lecture;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;

import com.google.gson.Gson;

import butterknife.BindView;
import butterknife.ButterKnife;
import pe.com.dms.inventariosoft.R;
import pe.com.dms.inventariosoft.data.models.Lectura;
import pe.com.dms.inventariosoft.features.shared.BaseActivity;
import pe.com.dms.inventariosoft.utils.Constants;

public class QuickLectureActivity extends BaseActivity {



    public static Intent newInstance(Context context, Lectura lectura) {
        Intent intent = new Intent(context, QuickLectureActivity.class);
        intent.putExtra(Constants.EXTRA_LECTURE, new Gson().toJson(lectura));
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_bar_main);
        ButterKnife.bind(this);

        setupNavigation();

        setTitle("Registrar Lectura");

        String stringLectura = getIntent().getStringExtra(Constants.EXTRA_LECTURE);
        replaceFragment(LectureFragment.newInstanceQuick(stringLectura),
                R.id.fl_main,
                LectureFragment.TAG);
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
