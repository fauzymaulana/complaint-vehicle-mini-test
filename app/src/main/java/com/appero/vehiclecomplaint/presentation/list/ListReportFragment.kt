package com.appero.vehiclecomplaint.presentation.list

import android.Manifest
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.appero.vehiclecomplaint.databinding.FragmentListReportBinding
import com.appero.vehiclecomplaint.domain.entities.FormVehicle
import com.appero.vehiclecomplaint.domain.entities.Report
import com.appero.vehiclecomplaint.domain.entities.SnackBar
import com.appero.vehiclecomplaint.domain.entities.TypeSnackBar
import com.appero.vehiclecomplaint.domain.entities.Vehicle
import com.appero.vehiclecomplaint.presentation.list.adapter.ReportComplaintAdapter
import com.appero.vehiclecomplaint.utilities.base.BaseFragment
import com.appero.vehiclecomplaint.utilities.Constant.Companion.USER_ID
import com.appero.vehiclecomplaint.utilities.helpers.OnClickListenerAdapter
import com.appero.vehiclecomplaint.utilities.helpers.ResultState
import com.appero.vehiclecomplaint.utilities.date_time.DateTimeHelper
import com.appero.vehiclecomplaint.utilities.extensions.observe
import com.appero.vehiclecomplaint.utilities.permission.PermissionHelper
import com.appero.vehiclecomplaint.utilities.permission.PermissionListener
import com.appero.vehiclecomplaint.utilities.permission.intent.openPermissionSetting
import com.appero.vehiclecomplaint.utilities.pop_up.GeneralDialog
import java.time.LocalDateTime

class ListReportFragment : BaseFragment(), PermissionListener, View.OnClickListener {

    private var _binding: FragmentListReportBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ReportViewModel by viewModels()
    private var showDialogDirectly = false
    private var imagePath: String? = null
    private lateinit var alertDialog: AlertDialog

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
        getParamArgs()
        observer()
        setupAdapter()
        initListeners()

    }

    private fun getParamArgs() {
        showDialogDirectly = ListReportFragmentArgs.fromBundle(arguments as Bundle).showPopUpDirectly
        imagePath = ListReportFragmentArgs.fromBundle(arguments as Bundle).imagePath

        Log.e("TAG", "getParamArgs: image PTAH ${imagePath}", )
        if (showDialogDirectly) {
            checkDialog(imagePath)
        }
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
        observe(viewModel.showSnackBar, ::observeSnackbar)
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
        Log.e("TAG", "shouldShowRationaleInfo: INI shouldShowRationaleInfo", )
        dialogPermissionDenied()
    }

    private fun formAddComplaintDialog(imagePath: String? = null) {
        val timestamp = LocalDateTime.now()
        val timeFormatted = DateTimeHelper().formatDateTime(timestamp)

        GeneralDialog(requireContext()).setFormDialog(
            dateTime = timeFormatted,
            listVehicle = vehicleList,
            activityResultLauncher = pickImageLauncher,
            imagePath = imagePath

        ) { btnSubmit, btnCancel, dropDownVehicle, imgPreview, notesLayout, etNotes, dialog ->
            alertDialog = dialog
            var selectedVehicle: Vehicle? = null
            dropDownVehicle.setOnItemClickListener { _, _, position, _ ->
                selectedVehicle = vehicleList[position]
            }

            dropDownVehicle.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(dropDownVehicle.windowToken, 0)
                }
            }

//            if (imagePath != "" && imagePath != null) {
//                Glide.with(requireContext())
//                    .load(imagePath)
//                    .centerCrop()
//                    .diskCacheStrategy(DiskCacheStrategy.ALL)
//                    .error(R.drawable.img_not_found)
//                    .into(imgPreview)
//            }
            imgPreview.setOnClickListener {
//                pickImageLauncher.launch("image/*")
                dismissDialog()
                navigateToCamera()
            }

            btnCancel.setOnClickListener {
                this.imagePath = null
                dismissDialog()
            }
            btnSubmit.setOnClickListener {

                if (imagePath != null && selectedVehicle != null && !etNotes.text.isNullOrEmpty()) {
                    val vehicleData = FormVehicle(
                        vehicleId = selectedVehicle!!.vehicleId,
                        notes = etNotes.text.toString(),
                        userId = USER_ID,
                        photo = imagePath
                    )

                    viewModel.postComplaint(vehicleData)
                    return@setOnClickListener dismissDialog()
                }

                viewModel.showSnackBar(
                    message = "Data tidak boleh kosong",
                    status = TypeSnackBar.ERROR,
                    action = null
                )

                return@setOnClickListener
            }
        }
    }

    override fun isPermissionGranted(isGranted: Boolean) {
        Log.e("TAG", "isPermissionGranted: ĩiiiiiiiiiii", )
        if (isGranted){
            startForResult.launch(permissionsToRequest)
            Log.e("TAG", "isPermissionGranted: BERAPA KALI DI EKSEKUSI ?", )
            checkDialog(null)
        }
    }

    override fun isDenied(isDenied: Boolean) {
        Log.e("TAG", "isDenied: INI DENIED", )
//        dialogPermissionDenied()
    }

    private fun observeSnackbar(snackBar: SnackBar) {
        showSnackBarWithAction(
            snack = snackBar,
            actionMessage = if (snackBar.action != null) "Muat ulang" else null
        ) {
            snackBar.action
        }
    }

    private fun checkDialog(imagePath: String?) {
        if (!::alertDialog.isInitialized || !alertDialog.isShowing) {
            formAddComplaintDialog(imagePath)
        }
    }

    private fun dismissDialog() {
        if (::alertDialog.isInitialized && alertDialog.isShowing) {
            alertDialog.dismiss()
        }
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

    private fun navigateToCamera() {
        val camera = ListReportFragmentDirections.actionListReportFragmentToCameraCaptureFragment()
        findNavController().navigate(camera)
    }
}