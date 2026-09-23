package pe.com.dms.inventariosoft.features.sync;

import pe.com.dms.inventariosoft.features.shared.BaseContractView;

/**
 * Created by Alvaro Santa Cruz on 26/09/2017.
 */

interface SyncContract {

    interface View extends BaseContractView {

        void setupDialog(String title, String message, boolean download);

        void startSync();

        void updateProgress(int doneItems, int totalItems);

        void syncSuccess();

        void syncError(String message);
    }

    interface Presenter {
        void setupView();

        void startSync();

        void syncSuccess();
    }
}