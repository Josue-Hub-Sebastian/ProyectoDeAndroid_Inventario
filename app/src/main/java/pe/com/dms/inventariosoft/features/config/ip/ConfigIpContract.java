package pe.com.dms.inventariosoft.features.config.ip;

import pe.com.dms.inventariosoft.features.shared.BaseContractView;

interface ConfigIpContract {

    interface View extends BaseContractView {
        void displayIp(String ip);
    }

    interface Presenter {
        void onViewCreated();

        void updateIp(String ip);
    }
}
