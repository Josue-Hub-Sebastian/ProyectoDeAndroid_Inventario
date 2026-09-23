package pe.com.dms.inventariosoft.features.missing;

import java.util.List;

import pe.com.dms.inventariosoft.data.models.Lectura;
import pe.com.dms.inventariosoft.features.shared.BaseContractView;

public interface MissingFragmentContract {
    interface View extends BaseContractView {
        void showMissing(List<Lectura> missings);

        void showError();

        void setupValue(String ubicacion, boolean cameraScan);
    }

    interface Presenter {
        void showMissing(String filtro);

        void setCriteria(int pos);
    }
}
