package com.appero.vehiclecomplaint.presentation.camera

import android.Manifest
import android.app.Activity
import android.content.ContentUris
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.OrientationEventListener
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.core.impl.CameraValidator.CameraIdListIncorrectException
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.transition.Scene
import com.appero.vehiclecomplaint.R
import com.appero.vehiclecomplaint.databinding.FragmentCameraCaptureBinding
import com.bumptech.glide.Glide
import java.io.File

class CameraCaptureFragment : Fragment(), View.OnClickListener {

    private var _binding: FragmentCameraCaptureBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CameraCaptureViewModel by viewModels()

    private lateinit var defaultScene: Scene
    private lateinit var timerScene: Scene
    private lateinit var mOrientationEventListener: OrientationEventListener
    private lateinit var mFocusCircleView: FocusCircleView
    private lateinit var galleryImages: List<String>
    private var selectedImageUri: Uri? = null

    private var cameraSelector      : CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    private var imageCapture        : ImageCapture? = null
    private val handler by lazy { Handler(Looper.getMainLooper()) }
    private var currentFlashMode: Int = ImageCapture.FLASH_MODE_OFF

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCameraCaptureBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initListeners()
        startCamera()
    }

    private fun initListeners() {
        binding.toggleCamera.setOnClickListener(this)
        binding.shutter.setOnClickListener(this)
        binding.btnFlash.setOnClickListener(this)
        binding.photoGallery.setOnClickListener(this)
    }

    private fun startCamera(){
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener(
            {
                val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
                val preview = createPreview()
                imageCapture = createImageCapture()

                try {
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture)
                } catch (e: Exception) {
                    Log.e("AttendanceFragment=", "startCamera: ERR ${e.message}", )
                    Toast.makeText(context, "Gagal memunculkan kamera", Toast.LENGTH_SHORT).show()
                } catch (e: CameraIdListIncorrectException) {
                    switchCamera()
                }
            }, ContextCompat.getMainExecutor(requireContext())
        )
    }

    private fun createPreview(): Preview {
        return Preview.Builder()
            .build()
            .also { it.setSurfaceProvider(binding.viewPreview.surfaceProvider) }
    }

    private fun createImageCapture(): ImageCapture {
        return ImageCapture.Builder().build()
    }

    private fun switchCamera() {
        cameraSelector = if (cameraSelector == CameraSelector.DEFAULT_FRONT_CAMERA) {
            updateCameraToggleIcon(R.drawable.ic_camera_rear)
            CameraSelector.DEFAULT_BACK_CAMERA
        } else {
            updateCameraToggleIcon(R.drawable.ic_camera_front)
            CameraSelector.DEFAULT_FRONT_CAMERA
        }
        startCamera()
    }

    private fun updateCameraToggleIcon(iconRes: Int) {
        handler.postDelayed({ binding.toggleCamera.setIconResource(iconRes) }, 500)
    }

    private fun takePhoto() {
        val imageCapture = imageCapture ?: throw IllegalStateException("Camera initialization failed.")

        imageCapture.flashMode = currentFlashMode

        val outputOptions = createOutputOptions()
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(requireContext()),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    handlePhotoSaved(outputFileResults)
                }

                override fun onError(exception: ImageCaptureException) {
                    handlePhotoError(exception)
                }
            }
        )
    }

    private fun handlePhotoSaved(outputFileResults: ImageCapture.OutputFileResults) {
        outputFileResults.savedUri?.let { uri ->
            Log.d("CameraFragment", "Photo saved in $uri")
            navigateToPreview(uri.toString())
        }
    }

    private fun navigateToPreview(imagePath: String) {
        handler.postDelayed(
            {
                val previewNavigation = CameraCaptureFragmentDirections.actionCameraCaptureFragmentToPreviewImageFragment()
                previewNavigation.pathImage = imagePath
                findNavController().navigate(previewNavigation) },
            1000
        )
    }

    private fun handlePhotoError(exception: ImageCaptureException) {
        Log.e("CameraFragment", "Photo capture failed: ${exception.message}")
        Toast.makeText(requireContext(), "Gagal mengambil foto", Toast.LENGTH_SHORT).show()
    }

    private fun createOutputOptions(): ImageCapture.OutputFileOptions {
        val outputDirectory = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            Environment.DIRECTORY_PICTURES + "/VehicleAppMedia"
        } else {
            requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)?.absolutePath ?: ""
        }

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, System.currentTimeMillis())
                put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                put(MediaStore.MediaColumns.RELATIVE_PATH, outputDirectory)
            }
            val contentUri = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
            ImageCapture.OutputFileOptions.Builder(requireContext().contentResolver, contentUri, contentValues).build()
        } else {
            File(outputDirectory).mkdirs()
            val file = File(outputDirectory, "${System.currentTimeMillis()}.jpg")
            ImageCapture.OutputFileOptions.Builder(file).build()
        }
    }

    private fun setupFlashToggle() {
        currentFlashMode = if (currentFlashMode == ImageCapture.FLASH_MODE_ON) {
            updateFlashIcon(R.drawable.ic_flash_off)
            ImageCapture.FLASH_MODE_OFF
        } else {
            updateFlashIcon(R.drawable.ic_flash_on)
            ImageCapture.FLASH_MODE_ON
        }
    }

    private fun updateFlashIcon(iconRes: Int) {
        handler.postDelayed({ binding.btnFlash.setIconResource(iconRes) }, 500)
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            binding.toggleCamera.id -> {
                switchCamera()
            }

            binding.shutter.id -> {
                takePhoto()
            }

            binding.btnFlash.id -> {
                setupFlashToggle()
            }

            binding.photoGallery.id -> {
                selectImageLauncher.launch("image/*")
            }
        }
    }

    private val selectImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            handleSelectedImage(it)
        }
    }

    private fun handleSelectedImage(uri: Uri) {
        val realPath = getRealPathFromURI(uri)
        Log.d("CameraFragment", "Selected Image Path: $realPath")
        navigateToPreview(realPath)
    }

    private fun getRealPathFromURI(uri: Uri): String {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = requireContext().contentResolver.query(uri, projection, null, null, null)
            return cursor?.use {
                if (it.moveToFirst()) {
                    val columnIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                    val path = it.getString(columnIndex)
                    it.close()
                    return path
                } else ""
            } ?: ""
    }

    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacksAndMessages(null)
    }
}