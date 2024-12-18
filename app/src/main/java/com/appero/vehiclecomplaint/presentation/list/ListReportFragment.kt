package com.appero.vehiclecomplaint.presentation.list

import android.Manifest
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.appero.vehiclecomplaint.R
import com.appero.vehiclecomplaint.databinding.FragmentListReportBinding
import com.appero.vehiclecomplaint.domain.entities.Report
import com.appero.vehiclecomplaint.domain.entities.Vehicle
import com.appero.vehiclecomplaint.presentation.dialog.FormReportDialog
import com.appero.vehiclecomplaint.presentation.list.adapter.ReportComplaintAdapter
import com.appero.vehiclecomplaint.utilities.OnClickListenerAdapter
import com.appero.vehiclecomplaint.utilities.ResultState
import com.appero.vehiclecomplaint.utilities.observe
import com.appero.vehiclecomplaint.utilities.permission.PermissionHelper
import com.appero.vehiclecomplaint.utilities.permission.PermissionListener
import com.appero.vehiclecomplaint.utilities.permission.intent.openPermissionSetting
import com.appero.vehiclecomplaint.utilities.pop_up.GeneralDialog

class ListReportFragment : Fragment(), PermissionListener, View.OnClickListener {

    private var _binding: FragmentListReportBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ReportViewModel by viewModels()

    private val permissionsToRequest by lazy {
        arrayOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
        )
    }

    private val vehicleList: MutableList<Vehicle> = mutableListOf<Vehicle>()

    private lateinit var permissionHelper: PermissionHelper
    private lateinit var pickImageLauncher: ActivityResultLauncher<String>
    private lateinit var generalDialog: GeneralDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        permissionHelper = PermissionHelper(this, this)
        pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                GeneralDialog(requireContext()).formDialog.imgPreview.setImageURI(it)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListReportBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observer()
        setupAdapter()
        initListeners()

    }

    private val startForResult = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
        var allAreGranted = true
        var isAnyDenied = false

        for (isGranted in it.values) {
            allAreGranted = allAreGranted && isGranted
            if (!isAnyDenied){
                isAnyDenied = true

            }
        }
    }

    private fun initListeners() {
        binding.btnAddReport.setOnClickListener(this)
    }
    private val reportAdapter by lazy {
        ReportComplaintAdapter(OnClickListenerAdapter { r ->
            Log.e("TAG", "REPORT DI KLIK ${r.reportId}: ", )
        })
    }

    private fun observer() {
        observe(viewModel.allComplaintResultState, ::observeReportComplaints)
        observe(viewModel.allVehicleResultState, ::observeVehicle)
    }

    private fun setupAdapter() {
        binding.rvList.removeAllViews()
        binding.rvList.setHasFixedSize(true)
        binding.rvList.layoutManager = LinearLayoutManager(context)
        binding.rvList.adapter = reportAdapter
    }

    private fun observeReportComplaints(resultState: ResultState<List<Report>>) {
        when(resultState) {
            is ResultState.BadRequest -> {}
            is ResultState.Forbidden -> {}
            is ResultState.HideLoading -> {}
            is ResultState.Loading -> {}
            is ResultState.NoConnection, is ResultState.Timeout -> {}
            is ResultState.NotFound -> {}
            is ResultState.Success -> {
                reportAdapter.submitList(resultState.data)
            }
            is ResultState.Unauthorized -> {}
            is ResultState.UnknownError -> {}
            else -> {

            }
        }
    }

    private fun observeVehicle(resultState: ResultState<List<Vehicle>>) {
        when(resultState) {
            is ResultState.BadRequest -> {}
            is ResultState.Forbidden -> {}
            is ResultState.HideLoading -> {}
            is ResultState.Loading -> {}
            is ResultState.NoConnection, is ResultState.Timeout -> {}
            is ResultState.NotFound -> {}
            is ResultState.Success -> {
                vehicleList.clear()
                resultState.data?.toMutableList()?.let { vehicleList.addAll(it) }
                if (vehicleList.isNotEmpty()) {
                    binding.btnAddReport.isEnabled = true
                }
            }
            is ResultState.Unauthorized -> {}
            is ResultState.UnknownError -> {}
            else -> {

            }
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.apply {
            getAllComplaint()
            getAllVehicle()
        }
    }

    override fun shouldShowRationaleInfo() {
        dialogPermissionDenied()
    }

    override fun isPermissionGranted(isGranted: Boolean) {
        if (isGranted){
            startForResult.launch(permissionsToRequest)
//            findNavController().navigate(R.id.action_listReportFragment_to_cameraCaptureFragment)
            GeneralDialog(requireContext()).setFormDialog(
                dateTime = "Senin, 12 Jan 24 11:30",
                listVehicle = vehicleList,
                activityResultLauncher = pickImageLauncher
            ) { btnSubmit, btnCancel, dropDownVehicle, imgPreview, dialog ->
                var selectedVehicle: Vehicle?
                dropDownVehicle.setOnItemClickListener { _, _, position, _ ->
                    selectedVehicle = vehicleList[position]
                    Toast.makeText(context, "KENDARANA YANG DI PILIH ${selectedVehicle?.type} license ${selectedVehicle?.licenseNumber}", Toast.LENGTH_SHORT).show()
                }

                dropDownVehicle.setOnFocusChangeListener { _, hasFocus ->
                    if (hasFocus) {
                        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        imm.hideSoftInputFromWindow(dropDownVehicle.windowToken, 0)
                    }
                }

                imgPreview.setOnClickListener {
                    pickImageLauncher.launch("image/*")
//                        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
//                            uri?.let {
//                                // Tampilkan gambar ke ImageView dalam dialog
//                                GeneralDialog(requireContext()).formDialog.imgPreview.setImageURI(it)
//                            }
//                        }
                }

                btnCancel.setOnClickListener { dialog.dismiss() }


            }
        }
    }

    override fun isDenied(isDenied: Boolean) {
        dialogPermissionDenied()
    }

    private fun dialogPermissionDenied() {
        val dialogBuilder = AlertDialog.Builder(requireContext())
        dialogBuilder.setMessage("Izin aplikasi diperlukan dalam mengakses fitur.")
            .setCancelable(false)
            .setPositiveButton("KE SETTING") { dialog, _ ->
                dialog.cancel()
                startActivity(openPermissionSetting())
            }

        val alert = dialogBuilder.create()
        alert.setTitle("Izin Aplikasi")
        alert.show()
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            binding.btnAddReport.id -> {
                permissionHelper.checkForMultiplePermissions(permissionsToRequest)

//                val dialog = FormReportDialog.newInstance("TITLE", "MESSAGE")
//                dialog.show(parentFragmentManager, "Report")
            }
        }
    }
}