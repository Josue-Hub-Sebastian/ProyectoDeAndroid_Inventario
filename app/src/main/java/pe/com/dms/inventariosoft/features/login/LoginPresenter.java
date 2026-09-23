package pe.com.dms.inventariosoft.features.login;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import pe.com.dms.inventariosoft.data.PreferenceManager;
import pe.com.dms.inventariosoft.data.models.Usuario;
import pe.com.dms.inventariosoft.data.pojos.Configuracion;
import pe.com.dms.inventariosoft.data.source.DataSourceRepository;
import pe.com.dms.inventariosoft.data.source.remote.ApiError;
import pe.com.dms.inventariosoft.data.pojos.MensajeResponse;
import pe.com.dms.inventariosoft.features.shared.BasePresenter;
import retrofit2.HttpException;
import timber.log.Timber;
import io.reactivex.Observable;

class LoginPresenter extends BasePresenter<LoginContract.View> implements LoginContract.Presenter {

    private final DataSourceRepository dataSourceRepository;
    private final PreferenceManager preferenceManager;
    private Disposable mDisposable;

    private Configuracion config;

    private String lastUsername;
    private String lastPassword;
    private boolean isForcingLogin = false;

    @Inject
    public LoginPresenter(PreferenceManager preferenceManager, DataSourceRepository dataSourceRepository) {
        this.preferenceManager = preferenceManager;
        this.config = preferenceManager.getConfig();
        this.dataSourceRepository = dataSourceRepository;
        this.dataSourceRepository.setOnlineMode(!config.isBatch());
    }

    @Override
    public void attachView(LoginContract.View mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
        if (mDisposable != null) mDisposable.dispose();
    }

    @Override
    public void setupView() {
        getView().setupBatchModeSwitch(config.isBatch());
    }

    @Override
    public boolean getBatchMode() {
        this.config = preferenceManager.getConfig();
        return config.isBatch();
    }

    @Override
    public void getRemoteData() {
        Usuario usuario = preferenceManager.getUserInfo();
        String username = (usuario != null) ? usuario.getUsername() : "";

        Completable syncFlow  = dataSourceRepository.cleanLocalDatabase()
                .andThen(dataSourceRepository.listAlmacenesByUser(username).ignoreElements())
                .andThen(dataSourceRepository.listAllUsuariosSync().ignoreElements())
                .andThen(dataSourceRepository.listAllProductosSync().ignoreElements())
                .andThen(dataSourceRepository.listAllUbicacionesSync().ignoreElements());

        mDisposable = syncFlow
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
               .subscribe(
                       () -> {
                           getView().goToMainActivity();
                       },
                       e -> {
                           if (e instanceof HttpException) {
                               ApiError apiError = ApiError.parse(((HttpException) e));
                               Timber.e(e);

                           }
                           else {
                               Timber.e(e);
                               getView().showLoginErrorDialog("Sincronizaion fallida.");
                           }

                       }
               );

    }

    @Override
    public void setBatchMode(boolean batch) {
        config.setBatch(batch);
        preferenceManager.saveConfig(config);
    }

    @Override
    public void onAttemptLogin(String username, String password) {
        this.lastUsername = username;
        this.lastPassword = password;

        mDisposable = dataSourceRepository.loginUsuario(username, password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(usuario -> {
                    if (usuario.isFlgOnline() && !config.isBatch() && !isForcingLogin) {
                        getView().showSessionActiveDialog(usuario);
                    } else {
                        isForcingLogin = false;
                        preferenceManager.saveUser(usuario);
                        getRemoteData();
                    }
                }, e -> {
                    isForcingLogin = false;
                    if (e instanceof HttpException) {
                        ApiError apiError = ApiError.parse(((HttpException) e));
                        Timber.e(e);
                        getView().showLoginErrorDialog(apiError.getMessage());
                    } else {
                        Timber.e(e);
                        getView().showLoginErrorDialog("Usuario o Contraseña incorrecto.");
                    }
                });
    }

    @Override
    public void onConfirmCloseSession(Usuario usuario) {
        mDisposable = dataSourceRepository.logoutUsuario(usuario)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mensajeResponse -> {
                    isForcingLogin = true;
                    onAttemptLogin(lastUsername, lastPassword);
                }, e -> {
                    isForcingLogin = false;
                    getView().showLoginErrorDialog("Error al cerrar sesión previa.");
                });
    }
}
