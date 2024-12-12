package com.appero.vehiclecomplaint.presentation.list

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.appero.vehiclecomplaint.R
import com.appero.vehiclecomplaint.databinding.FragmentListReportBinding
import com.appero.vehiclecomplaint.domain.entities.Report
import com.appero.vehiclecomplaint.presentation.list.adapter.ReportComplaintAdapter
import com.appero.vehiclecomplaint.utilities.OnClickListenerAdapter
import com.appero.vehiclecomplaint.utilities.ResultState
import com.appero.vehiclecomplaint.utilities.observe

class ListReportFragment : Fragment() {

    private var _binding: FragmentListReportBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ReportViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
    }

    private val reportAdapter by lazy {
        ReportComplaintAdapter(OnClickListenerAdapter { r ->
            Log.e("TAG", "REPORT DI KLIK ${r.reportId}: ", )
        })
    }

    private fun observer() {
        observe(viewModel.allComplaintResultState, ::observeReportComplaints)
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

    override fun onStart() {
        super.onStart()
        viewModel.getAllComplaint()
    }
}