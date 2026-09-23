package pe.com.dms.inventariosoft.features.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.IdRes;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Locale;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import pe.com.dms.inventariosoft.R;
import pe.com.dms.inventariosoft.data.PreferenceManager;
import pe.com.dms.inventariosoft.data.models.Lectura;
import pe.com.dms.inventariosoft.data.models.Usuario;
import pe.com.dms.inventariosoft.data.models.ProductoAsignado;
import pe.com.dms.inventariosoft.features.config.ConfigFragment;
import pe.com.dms.inventariosoft.features.home.HomeFragment;
import pe.com.dms.inventariosoft.features.lecture.LectureFragment;
import pe.com.dms.inventariosoft.features.lecture.list.LectureListFragment;
import pe.com.dms.inventariosoft.features.login.LoginActivity;
import pe.com.dms.inventariosoft.features.missing.MissingFragment;
import pe.com.dms.inventariosoft.features.queries.QueryFragment;
import pe.com.dms.inventariosoft.features.shared.BaseActivity;
import pe.com.dms.inventariosoft.features.shared.BaseFragment;
import pe.com.dms.inventariosoft.utils.UtilMethods;
import pe.com.dms.inventariosoft.utils.dialogs.CustomDialog;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import pe.com.dms.inventariosoft.data.source.DataSourceRepository;
import pe.com.dms.inventariosoft.data.source.remote.ApiError;
import retrofit2.HttpException;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.nav_view)
    public BottomNavigationView navView;

    @Inject
    PreferenceManager preferenceManager;

    @Inject
    DataSourceRepository dataSourceRepository;

    private BaseFragment fragment;
    private boolean isOpe;

    public static Intent newInstance(Context context) {
        return new Intent(context, MainActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivityComponent().inject(this);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        lyProgress = findViewById(R.id.ly_progress);

        setupNavigation();
        if (savedInstanceState == null) {
            navView.setSelectedItemId(R.id.nav_settings);
        }
    }

    private void setupNavigation() {
        setupHeaderView();
        listenNavigationView();
        setupNavigationMenuView();
    }

    private void setupHeaderView() {
        // Implementation if needed
    }

    public void setupNavigationMenuView() {
        isOpe = false;
        if (navView == null) return;
        
        navView.getMenu().clear();
        navView.inflateMenu(R.menu.drawer_inventory);

        Menu menu = navView.getMenu();
        if (preferenceManager.getConfig().getAlmacen() == null ||
                "".equalsIgnoreCase(preferenceManager.getConfig().getAlmacen().getDescripcion())) {
            
            // Ocultamos de forma segura buscando por ID, no por posicion
            if (menu.findItem(R.id.nav_search) != null) menu.findItem(R.id.nav_search).setVisible(false);
            if (menu.findItem(R.id.nav_query) != null) menu.findItem(R.id.nav_query).setVisible(false);
            if (menu.findItem(R.id.nav_history) != null) menu.findItem(R.id.nav_history).setVisible(false);
            
        } else {
            if (preferenceManager.getUserInfo().getNombre().toUpperCase(Locale.ROOT).contains("OPE") || 
                preferenceManager.getUserInfo().getUsername().toUpperCase(Locale.ROOT).contains("OPE")) {
                isOpe = true;
                MenuItem queryItem = menu.findItem(R.id.nav_query);
                if (queryItem != null) {
                    queryItem.setTitle("Faltantes");
                    queryItem.setIcon(R.drawable.ic_list);
                }
            }

            // Habilitamos todo de forma segura
            for (int i = 0; i < menu.size(); i++) {
                menu.getItem(i).setVisible(true);
                menu.getItem(i).setEnabled(true);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (fragment != null && fragment.onBackPressed()) {
            new CustomDialog.Builder(getContext())
                    .setTitle("Alerta")
                    .setMessage("¿Está seguro que desea salir?")
                    .setPositiveButtonLabel(getString(R.string.label_yes))
                    .setIcon(R.drawable.ic_logout)
                    .setNegativeButtonLabel(getString(R.string.label_no))
                    .setPositiveButtonlistener(super::onBackPressed)
                    .build().show();
        }
    }

    private void clearDrawerSelection(Menu menu) {
        int i = 0;
        while (menu != null) {
            try {
                MenuItem menuItem = menu.getItem(i++);
                menuItem.setChecked(false);
                clearDrawerSelection(menuItem.getSubMenu());
            } catch (IndexOutOfBoundsException e) {
                break;
            }
        }
    }

    public void navigateTo(@IdRes int menuId) {
        MenuItem menuItem = retrieveMenuItem(navView.getMenu(), menuId);
        onNavigationItemSelected(menuItem);
    }

    private MenuItem retrieveMenuItem(Menu menu, @IdRes int menuId) {
        int i = 0;
        while (menu != null) {
            try {
                MenuItem m = menu.getItem(i++);
                if (m.getItemId() == menuId) return m;
                MenuItem sub = retrieveMenuItem(m.getSubMenu(), menuId);
                if (sub != null) return sub;
            } catch (IndexOutOfBoundsException e) {
                break;
            }
        }
        return null;
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        return true;
    }

    private void listenNavigationView() {
        navView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.nav_search:
                    fragment = LectureFragment.newInstance();
                    setTitle("Registro de Inventario");
                    break;
                case R.id.nav_query:
                    if (isOpe) {
                        fragment = MissingFragment.newInstance();
                        setTitle("Reporte de Faltantes");
                    } else {
                        fragment = QueryFragment.newInstance();
                        setTitle("Consultar Inventario");
                    }
                    break;
                case R.id.nav_history:
                    fragment = LectureListFragment.newInstance(true);
                    setTitle("Historial");
                    break;
                case R.id.nav_home:
                    fragment = HomeFragment.Companion.newInstance();
                    setTitle("Home");
                    break;
                case R.id.nav_settings:
                    fragment = ConfigFragment.newInstance();
                    setTitle(getString(R.string.menu_title_configuration));
                    break;
                case R.id.nav_logout:
                    openLogoutDialog();
                    return false;
            }

            if (fragment != null) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fl_main, fragment)
                        .commit();
                return true;
            }
            return false;
        });
    }

    private void openLogoutDialog() {
        new CustomDialog.Builder(MainActivity.this)
                .setTitle(getString(R.string.dialog_logout_title))
                .setMessage(getString(R.string.dialog_logout_message))
                .setPositiveButtonLabel(getString(R.string.label_yes))
                .setNegativeButtonLabel(getString(R.string.label_no))
                .setPositiveButtonlistener(() -> {
                    if (!UtilMethods.isNetworkConnected(getContext())) {
                        UtilMethods.showToast("Se requiere conexión a internet para cerrar sesión.");
                        return;
                    }
                    showLoading();
                    dataSourceRepository.logoutUsuario(preferenceManager.getUserInfo())
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(mensajeResponse -> {
                                hideLoading();
                                if (mensajeResponse.getCod() == 1) {
                                    preferenceManager.removeUser();
                                    preferenceManager.removeUserConfig();
                                    startActivity(LoginActivity.newInstance(getContext()));
                                    finish();
                                } else {
                                    UtilMethods.showToast(mensajeResponse.getMsg());
                                }
                            }, throwable -> {
                                hideLoading();
                                UtilMethods.showToast("Error al cerrar sesión");
                            });
                })
                .build().show();
    }

    public void goToRegistrar(ProductoAsignado producto) {
        Lectura lectura = new Lectura();
        lectura.setCodigoProducto(producto.getCodProducto());
        lectura.setCodigoUbicacion(producto.getUbicacionBase());
        lectura.setDescProducto(producto.getDescripcion());
        
        String json = new com.google.gson.Gson().toJson(lectura);
        fragment = LectureFragment.newInstanceQuick(json);
        
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fl_main, fragment)
                .commit();
        
        navView.setSelectedItemId(R.id.nav_search);
    }

    /**
     * Habilita o deshabilita el botón de Registro en el menú inferior de forma segura
     */
    public void setRegistrarEnabled(boolean enabled) {
        if (navView != null) {
            MenuItem registrarItem = navView.getMenu().findItem(R.id.nav_search);
            if (registrarItem != null) {
                registrarItem.setEnabled(enabled);
                if (registrarItem.getIcon() != null) {
                    registrarItem.getIcon().setAlpha(enabled ? 255 : 70);
                }
            }
        }
    }
}
