package pe.com.dms.inventariosoft.features.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import pe.com.dms.inventariosoft.data.models.ProductoAsignado
import pe.com.dms.inventariosoft.databinding.FragmentHomeBinding
import pe.com.dms.inventariosoft.features.lecture.AssignedProductsAdapter
import pe.com.dms.inventariosoft.features.shared.BaseFragment
import javax.inject.Inject

class HomeFragment : BaseFragment(), HomeContract.View {

    @Inject
    lateinit var presenter: HomePresenter
    
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    
    private var adapter: AssignedProductsAdapter? = null
    private var operatorStatusAdapter: OperatorStatusAdapter? = null

    companion object {
        fun newInstance() = HomeFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupListeners()
        presenter.attachView(this)
        presenter.onViewCreated()
    }

    private fun setupRecyclerView() {
        adapter = AssignedProductsAdapter(emptyList()) { product ->
            (activity as? pe.com.dms.inventariosoft.features.main.MainActivity)?.goToRegistrar(product)
        }
        binding.rvProductosAsignados.layoutManager = LinearLayoutManager(context)
        binding.rvProductosAsignados.adapter = adapter

        operatorStatusAdapter = OperatorStatusAdapter(emptyList())
        binding.rvEstadoOperadores.layoutManager = LinearLayoutManager(context)
        binding.rvEstadoOperadores.adapter = operatorStatusAdapter
    }

    private fun setupListeners() {
        binding.btnSiParticipa.setOnClickListener {
            mostrarConfirmacionParticipacion(true)
        }
        binding.btnNoParticipa.setOnClickListener {
            mostrarConfirmacionParticipacion(false)
        }
    }

    private fun mostrarConfirmacionParticipacion(participa: Boolean) {
        // Obtenemos el número de conteo actual y restamos 1
        val nroActual = presenter.preferenceManager.config.almacen?.conteo ?: 1
        val titulo = "Ha finalizado el CONTEO ${nroActual - 1}"
        
        // Buscamos si tenemos información del tipo de conteo (esto es dinámico ahora)
        val tipoConteo = binding.tvTipoConteo.text.toString()

        val mensaje = if (participa) {
            if (tipoConteo.contains("REINICIO", true)) 
                "¿Desea participar en el REINICIO del conteo?"
            else
                "¿PARTICIPAR en el siguiente conteo diferencial?"
        } else {
            "¿NO participar en el siguiente conteo?"
        }

        pe.com.dms.inventariosoft.utils.dialogs.CustomDialog.Builder(context)
            .setTitle(titulo)
            .setMessage(mensaje)
            .setPositiveButtonLabel("SÍ")
            .setNegativeButtonLabel("NO")
            .setTheme(pe.com.dms.inventariosoft.R.style.AppTheme_Dialog_Warning)
            .setIcon(pe.com.dms.inventariosoft.R.drawable.ic_alert)
            .setPositiveButtonlistener {
                presenter.responderParticipacion(participa)
            }
            .build()
            .show()
    }

    override fun showDefaultInfo() {
        binding.layoutInfoDefault.visibility = View.VISIBLE
        binding.layoutInvitacion.visibility = View.GONE
        binding.layoutEspera.visibility = View.GONE
        binding.layoutListaAsignados.visibility = View.GONE
    }

    override fun showInvitation(nroConteo: Int, tipoConteo: String?) {
        val nroFinalizado = nroConteo - 1
        binding.tvFinalizoConteo.text = "Ha finalizado el\nCONTEO $nroFinalizado"
        
        binding.tvTipoConteo.text = tipoConteo ?: "DIFERENCIAL"
        binding.tvTipoConteo.visibility = if (tipoConteo != null) View.VISIBLE else View.GONE

        binding.layoutInfoDefault.visibility = View.GONE
        binding.layoutInvitacion.visibility = View.VISIBLE
        binding.layoutEspera.visibility = View.GONE
        binding.layoutListaAsignados.visibility = View.GONE
    }

    override fun updateTimer(time: String) {
        binding.tvTimer.text = time
    }

    override fun showWaiting() {
        binding.layoutInfoDefault.visibility = View.GONE
        binding.layoutInvitacion.visibility = View.GONE
        binding.layoutEspera.visibility = View.VISIBLE
        binding.layoutListaAsignados.visibility = View.GONE
    }

    override fun updateOperatorStatus(operators: List<pe.com.dms.inventariosoft.data.models.OperadorEstado>) {
        operatorStatusAdapter?.updateData(operators)
    }

    override fun showAssignedList(products: List<ProductoAsignado>, progress: String) {
        android.util.Log.d("HomeFragment", "Mostrando lista con ${products.size} productos. Progreso: $progress")
        
        binding.tvHeaderReparto.text = "Busca los productos y\nvuelve a contarlos\n$progress"
        adapter?.updateData(products)
        
        // Apagamos TODO lo demás explícitamente
        binding.layoutInfoDefault.visibility = View.GONE
        binding.layoutInvitacion.visibility = View.GONE
        binding.layoutEspera.visibility = View.GONE
        
        // Encendemos la lista
        binding.layoutListaAsignados.visibility = View.VISIBLE
    }

    override fun setRegistrarAccess(enabled: Boolean) {
        (activity as? pe.com.dms.inventariosoft.features.main.MainActivity)?.setRegistrarEnabled(enabled)
    }

    override fun refreshMenu() {
        (activity as? pe.com.dms.inventariosoft.features.main.MainActivity)?.setupNavigationMenuView()
    }

    override fun showError(message: String) {
        pe.com.dms.inventariosoft.utils.UtilMethods.showToast(message)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        presenter.onDetach()
        presenter.detachView()
        _binding = null
    }
}
