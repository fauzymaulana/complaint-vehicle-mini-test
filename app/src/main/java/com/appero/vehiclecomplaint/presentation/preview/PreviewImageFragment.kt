package com.appero.vehiclecomplaint.presentation.preview

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.appero.vehiclecomplaint.R
import com.appero.vehiclecomplaint.databinding.FragmentPreviewImageBinding
import com.bumptech.glide.Glide

class PreviewImageFragment : Fragment(), View.OnClickListener {

    private var _binding: FragmentPreviewImageBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PreviewImageViewModel by viewModels()
    private var imagePath: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPreviewImageBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getParamArgs()
        initListeners()

    }

    private fun initListeners() {
        binding.btnUploadImage.setOnClickListener(this)
    }

    private fun getParamArgs() {
        imagePath = PreviewImageFragmentArgs.fromBundle(arguments as Bundle).pathImage

        Glide.with(requireContext())
            .load(imagePath)
            .into(binding.imgPreview)
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            binding.btnUploadImage.id -> {

            }
        }
    }
}