package pe.com.dms.inventariosoft.features.login;

import pe.com.dms.inventariosoft.data.models.Usuario;
import pe.com.dms.inventariosoft.features.shared.BaseContractView;

interface LoginContract {

    interface View extends BaseContractView {

        void showLoginErrorDialog(String message);

        void goToMainActivity();

        void setupBatchModeSwitch(boolean batch);

        void showSessionActiveDialog(Usuario usuario);
    }

    interface Presenter {
        void setupView();

        boolean getBatchMode();

        void  getRemoteData();

        void setBatchMode(boolean batch);

        void onAttemptLogin(String username, String password);

        void onConfirmCloseSession(Usuario usuario);
    }
}
