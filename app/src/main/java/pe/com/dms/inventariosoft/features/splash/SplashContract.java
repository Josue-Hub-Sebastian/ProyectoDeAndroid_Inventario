package pe.com.dms.inventariosoft.features.splash;

import pe.com.dms.inventariosoft.features.shared.BaseContractView;

interface SplashContract {

    interface View extends BaseContractView {

        void goToLogin();

        void goToMainActivity();

        void viewMessage(String message);
    }

    interface Presenter {
        void onSplashDone();

        void setAndroidId(String imei);
    }
}