package pe.com.dms.inventariosoft.features.lecture;

import java.util.List;
import java.util.regex.Pattern;

import pe.com.dms.inventariosoft.data.models.Lectura;
import pe.com.dms.inventariosoft.data.models.Producto;
import pe.com.dms.inventariosoft.data.models.ProductoAsignado;
import pe.com.dms.inventariosoft.features.shared.BaseContractView;

interface LectureContract {

    interface View extends BaseContractView {

        void setupConteo(int conteo);

        void setupCameraScan(boolean active);

        void setupLecturas(int lecturas);

        void setupUbicacion(String defaultUbicacion);

        void setupProducto(boolean isBarrido);

        //CANELAZA
        void showProductoInfo(Producto producto, boolean matchedByCode);

        void setupLote(boolean visible, boolean config);

        void setupSerie(boolean visible, boolean config);

        void setupModoConteo(int modo);

        void setupCantidad(boolean auto);

        void setupFocoProducto();

        void setupFocoUbicacion();

        void setupConfirmation();

        void showErrorDialog(String message);

        void showConfirmation(Lectura lectura);

        void showValidationError(String message);

        void showError(String message);

        void ubicacionCreated(String codUbicacion, String almacen);

        void lecturaCreated();

        void lecturaDeleted();

        void displayInventarioDoneError(String message);

        void refreshView();

        void goToConfig();

        void goToHome();

        void openRFID();

        void closeRFID();

        void showParticipacionDialog(int nroConteo, String fchLimite);

        void updateCountdown(String time);

        void showWaitingForOthers();

        void hideWaitingForOthers();

        void showAssignedProductsList(List<ProductoAsignado> assignedProducts);
    }



    interface Presenter {

        void saveModoLote(boolean isLote);

        void saveModoLecture(boolean isBarrido);

        Pattern getPattern();

        boolean getRFID();

        boolean getCameraScan();

        boolean getNumberDecimal();

        boolean isCalculadora();

        void onViewCreated();

        boolean isBarrido();

        void verifarFoco();

        void createUbicacion(String codUbicacion);

        void sendLecture(String ubicacion, String producto, String lote, String serie, String cantidad);

        void requestNextInventario();

        void saveRFID(List<String> rfid, String cod);

        void getLectura();

        void removeAllLecturas();
        void onConfirmationAccepted(Lectura lecture);

        void validateUbicacion(String codUbicacion);

        void updateUbicacion(String codUbicacion);

        void searchProducto(String code);

        void responderParticipacion(boolean participa);
    }

}
