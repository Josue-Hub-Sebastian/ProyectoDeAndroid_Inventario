package pe.com.dms.inventariosoft.features.home

import pe.com.dms.inventariosoft.data.models.ProductoAsignado
import pe.com.dms.inventariosoft.features.shared.BaseContractView

interface HomeContract {
    interface View : BaseContractView {
        fun showDefaultInfo()
        fun showInvitation(nroConteo: Int, tipoConteo: String?)
        fun updateTimer(time: String)
        fun showWaiting()
        fun updateOperatorStatus(operators: List<pe.com.dms.inventariosoft.data.models.OperadorEstado>)
        fun showAssignedList(products: List<ProductoAsignado>, progress: String)
        fun setRegistrarAccess(enabled: Boolean)
        fun refreshMenu()
        fun showError(message: String)
    }

    interface Presenter {
        fun onViewCreated()
        fun responderParticipacion(participa: Boolean)
        fun onDetach()
    }
}
