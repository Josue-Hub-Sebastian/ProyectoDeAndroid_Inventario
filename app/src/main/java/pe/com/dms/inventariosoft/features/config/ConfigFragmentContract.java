package pe.com.dms.inventariosoft.features.config;

import java.util.List;

import pe.com.dms.inventariosoft.data.models.Almacen;
import pe.com.dms.inventariosoft.data.models.Usuario;
import pe.com.dms.inventariosoft.data.pojos.Configuracion;
import pe.com.dms.inventariosoft.features.shared.BaseContractView;

interface ConfigFragmentContract {

    interface View extends BaseContractView {

        void initListeners();

        void display(Configuracion configuracion);

        void selectAlmacen(List<Almacen> almacenList);

        void showRFID(boolean flag);

        void showMessage(String message);

        void loading(Boolean visible);

        void logoutSuccess();
    }

    interface Presenter {
        void onViewCreated();

        void getListAlmacen();

        void saveModo(int modo);

        void saveRFID(boolean flag);

        void saveAlmacen(Almacen almacen);

        void saveUbicacion(String ubicacion);

        void saveLote(boolean flag);

        void saveSerie(boolean flag);

        void saveCamaraScan(boolean flag);

        void saveBatch(boolean flag);

        void saveSolicitarConfirmacion(boolean flag);

        void saveSizeNumber(int sizeNumber);

        void saveDecimalNumber(boolean flag);

        void saveCalculadora(boolean flag);

        Usuario getUserInfo();

        void downLoadProducts();

    }
}
