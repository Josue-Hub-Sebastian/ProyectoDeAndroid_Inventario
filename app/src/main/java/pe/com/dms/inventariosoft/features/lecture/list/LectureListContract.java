package pe.com.dms.inventariosoft.features.lecture.list;

import java.util.List;

import pe.com.dms.inventariosoft.data.models.Lectura;
import pe.com.dms.inventariosoft.data.pojos.LecturaRFID;
import pe.com.dms.inventariosoft.features.shared.BaseContractView;

interface LectureListContract {

    interface View extends BaseContractView {
        void displayLectures(List<Lectura> lecturas);
        void displayLecturesRFID(List<LecturaRFID> lecturas);
        void actualizarTextoPagina(String texto);

        void refreshList(Boolean checkedFiltro);

        void getIdInventario(String idInventario);

        void lectureDeleted();

        void displayMessage(String message);

        void displayError(String message);
    }

    interface Presenter {
        void loadLastLectures( Boolean checkedFiltro);

        boolean isRFID();

        void saveRFI(String codUbicacion, Boolean filterChecked);

        void deleteLecture(int id);

        void deleteLectureRFID(String cod);

        void loadNextPage();
        void loadPreviousPage();
        void search(String query);


    }
}
