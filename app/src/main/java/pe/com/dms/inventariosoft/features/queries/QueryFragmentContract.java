package pe.com.dms.inventariosoft.features.queries;

import java.util.List;

import pe.com.dms.inventariosoft.data.pojos.HijoConsulta;
import pe.com.dms.inventariosoft.features.shared.BaseContractView;

public interface QueryFragmentContract {
    interface View extends BaseContractView {
//        void showLecture(List<HeaderConsulta> headerConsultaList);

        void showLecture(List<HijoConsulta> lista);


        void removeLectura();

        void removeAll();

        void setupValue(String ubicacion, boolean cameraScan);

        void displayError(String message);

        void goToConfig();

        void displayInventarioDoneError(String message);
    }

    interface Presenter {
        void syncLecture();

        void showLecture(int tipo, String filtro);

        void removeLectureHijo(int id, int position);

//        void removeLecturePadre(List<HijoConsulta> items);

        void removeAll();

        void requestNextInventario();

        void setCriteria(int pos);
    }
}
