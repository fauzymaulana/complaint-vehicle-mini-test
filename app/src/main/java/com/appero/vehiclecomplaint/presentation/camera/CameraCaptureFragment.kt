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
import com.appero.vehiclecomplaint.presentation.camera.adapter.ImageAdapter
import com.appero.vehiclecomplaint.utilities.OnClickListenerAdapter
import com.appero.vehiclecomplaint.utilities.permission.PermissionHelper
import com.appero.vehiclecomplaint.utilities.permission.PermissionListener
import com.appero.vehiclecomplaint.utilities.pop_up.GeneralDialog
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

    private val imageLatestAdapter by lazy {
        ImageAdapter(galleryImages, OnClickListenerAdapter { s ->

        })
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
//        setupGalleryAdapter()
    }

    private fun initListeners() {
        binding.toggleCamera.setOnClickListener(this)
        binding.shutter.setOnClickListener(this)
        binding.btnFlash.setOnClickListener(this)
        binding.photoGallery.setOnClickListener(this)
    }

    private fun setupGalleryAdapter() {
        galleryImages = fetchImageLatest()
        Log.e("TAG", "setupGalleryAdapter: IMAGENY ${galleryImages.toList()}", )
        binding.rvLatestImage.removeAllViews()
        binding.rvLatestImage.setHasFixedSize(true)
//        imageLatestAdapter.submitList(galleryImages)
        binding.rvLatestImage.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.rvLatestImage.adapter = imageLatestAdapter
    }

    private fun fetchImageLatest(): List<String> {
        val imageList = mutableListOf<String>()
        val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.MediaColumns.DATA,
            MediaStore.MediaColumns.BUCKET_DISPLAY_NAME,
            MediaStore.MediaColumns.DATE_MODIFIED
        )
        val sortOrder = "${MediaStore.Images.Media.DATE_MODIFIED} DESC" // Urutkan dari terbaru


        val cursor = requireContext().contentResolver.query(uri, projection, null, null, sortOrder)
        cursor?.use {
            val columnIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
//            val bucketIdColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_ID)
//            val modifiedIndex = it.getColumnIndex(MediaStore.MediaColumns.DATE_MODIFIED)
            val bucketNameColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)
//            val uniqueBuckets = mutableSetOf<String>()
            while (it.moveToFirst()) {
//                Log.e("FKAFLA", "fetchImageLatest: bucket col $bucketNameColumn", )
                val imagePath = it.getString(columnIndex)
//                val bucketID = it.getString(bucketIdColumn)
//                val createdTime = it.getLong(modifiedIndex)
//                val albumName = it.getString(bucketNameColumn)
                imageList.add(imagePath)

//                val imageId = it.getLong(columnIndex)
//                val imageUri = ContentUris.withAppendedId(uri, imageId)
//
//                imageList.add(imageUri.toString())
            }
        }
        return imageList
    }

    private fun startCamera(){
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener(
            {
                val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
                val preview = Preview.Builder()
                    .build()
                    .also { it.setSurfaceProvider(binding.viewPreview.surfaceProvider) }

                imageCapture = ImageCapture.Builder()
                    .build()

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

    private fun switchCamera() {
        cameraSelector = if (cameraSelector == CameraSelector.DEFAULT_FRONT_CAMERA) {
            handler.postDelayed(
                { binding.toggleCamera.setIconResource(R.drawable.ic_camera_rear) },
                500
            )
            CameraSelector.DEFAULT_BACK_CAMERA
        } else {
            handler.postDelayed(
                { binding.toggleCamera.setIconResource(R.drawable.ic_camera_front) },
                500
            )
            CameraSelector.DEFAULT_FRONT_CAMERA
        }
        startCamera()
    }

    private fun takePhoto() {
        val imageCapture = imageCapture ?: throw IllegalStateException("Camera initialization failed.")

        imageCapture.flashMode = currentFlashMode

        val outputDirectory = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            Environment.DIRECTORY_PICTURES + "/MyApp"
        } else {
            requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)?.absolutePath ?: ""
        }

        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        // Options fot the output image file
        val outputOptions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, System.currentTimeMillis())
                put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                put(MediaStore.MediaColumns.RELATIVE_PATH, outputDirectory)
            }

            val contentResolver = requireContext().contentResolver

            // Create the output uri
            val contentUri = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)

            ImageCapture.OutputFileOptions.Builder(contentResolver, contentUri, contentValues)
        } else {
            File(outputDirectory).mkdirs()
            val file = File(outputDirectory, "${System.currentTimeMillis()}.jpg")

            ImageCapture.OutputFileOptions.Builder(file)
        }.build()

        imageCapture.takePicture(
            outputOptions, // the options needed for the final image
            ContextCompat.getMainExecutor(requireContext()), // the executor, on which the task will run
            object : ImageCapture.OnImageSavedCallback { // the callback, about the result of capture process
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    // This function is called if capture is successfully completed
                    outputFileResults.savedUri
                        ?.let { uri ->
//                            setGalleryThumbnail(uri)
                            Log.d("TAG", "Photo saved in $uri")
                            handler.postDelayed(
                                {
                                    val previewNavigation = CameraCaptureFragmentDirections.actionCameraCaptureFragmentToPreviewImageFragment()
                                    previewNavigation.pathImage = uri.toString()
                                    findNavController().navigate(previewNavigation) },
                                1000
                            )
                        }
//                        ?: run { setLastPictureThumbnail() }
                }

                override fun onError(exception: ImageCaptureException) {
                    // This function is called if there is an errors during capture process
                    val msg = "Photo capture failed: ${exception.message}"
                    Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
//                    Log.e(TAG, msg)
                    exception.printStackTrace()
                }
            }
        )



        val path = context?.filesDir ?: return
//        val tempPhotoFile = com.klik.adzkia.attandance.utilities.createTempFile(requireContext())

//        val outputOptions = ImageCapture.OutputFileOptions.Builder(tempPhotoFile).build()
//        imageCapture.takePicture(outputOptions, ContextCompat.getMainExecutor(requireContext()), object: ImageCapture.OnImageSavedCallback{
//            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
////                rotateFile(file = tempPhotoFile, isBackCamera = cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA)
//
////                previewImageView?.let {
////                    isVisibleView(View.VISIBLE, it)
////                    it.setImageBitmap(BitmapFactory.decodeFile(tempPhotoFile.path))
////                }
//
////                viewModel?.apply {
////                    saveDataClockEntry(
////                        longitude = longitude,
////                        latitude = latitude,
////                        label = labelButton,
////                        timestamp = timestamp,
////                        image = tempPhotoFile.path,
////                        note = note ?: ""
////                    )
////                    saveClockResult()
////                }
//            }
//
//            override fun onError(exception: ImageCaptureException) {
//                Toast.makeText(context, "Gagal mengambil gambar", Toast.LENGTH_SHORT).show()
//            }
//        })
    }

//    private fun setGalleryThumbnail(savedUri: Uri?) = binding.lastPhotoPreview.load(savedUri) {
//        placeholder(R.drawable.ic_no_picture)
//        transformations(CircleCropTransformation())
//        listener(object : ImageRequest.Listener {
//            override fun onError(request: ImageRequest, result: ErrorResult) {
//                super.onError(request, result)
//                binding.btnGallery.load(savedUri) {
//                    placeholder(R.drawable.ic_no_picture)
//                    transformations(CircleCropTransformation())
////                    fetcher(VideoFrameUriFetcher(requireContext()))
//                }
//            }
//        })
//    }

    private fun getMedia(): List<MediaItem> {
        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.DATE_ADDED
        )

        val cursor = requireContext().contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            "${MediaStore.Images.Media.DATE_ADDED} DESC"
        )

        val mediaList = mutableListOf<MediaItem>()
        cursor?.use {
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val name = cursor.getString(nameColumn)
                val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI.buildUpon()
                    .appendPath(id.toString()).build()

                mediaList.add(MediaItem(uri, name))
            }
        }

        return mediaList
    }

    private fun setupFlashToggle() {
        currentFlashMode = when(currentFlashMode) {
            ImageCapture.FLASH_MODE_ON -> {
                handler.postDelayed(
                    { binding.btnFlash.setIconResource(R.drawable.ic_flash_off) },
                    500
                )
                ImageCapture.FLASH_MODE_OFF
            }
            ImageCapture.FLASH_MODE_OFF -> {
                handler.postDelayed(
                    { binding.btnFlash.setIconResource(R.drawable.ic_flash_on) },
                    500
                )
                ImageCapture.FLASH_MODE_ON
            }
            else -> {
                binding.btnFlash.setIconResource(R.drawable.ic_flash_off)
                ImageCapture.FLASH_MODE_OFF
            }
        }
    }

//    private fun setGalleryThumbnail(savedUri: Uri?) {
//        Glide.with(requireContext())
//            .load(savedUri)
//            .centerCrop()
//            .into(binding.lastPhotoPreview)
//    }

//    private fun setLastPictureThumbnail() = binding.lastPhotoPreview.post {
//        getMedia().firstOrNull() // check if there are any photos or videos in the app directory
//            ?.let { setGalleryThumbnail(it.uri) } // preview the last one
//            ?: binding.lastPhotoPreview.setImageResource(R.drawable.ic_photo) // or the default placeholder
//    }

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
                accessGallery()
            }
        }
    }

    private fun accessGallery(){
        Intent(Intent.ACTION_PICK).also {
            it.type = "image/*"
            val mimeType = arrayOf("image/jpg", "image/png", "image/jpeg")
            it.putExtra(Intent.EXTRA_MIME_TYPES, mimeType)
            startActivityForResult(it, REQUEST_CODE_IMAGE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_CODE_IMAGE -> {
                    if (data != null) {
                        selectedImageUri = data.data
                        val getpath = getRealPathFromURI(selectedImageUri!!)
                        val file = File(getpath)
                        Log.e("TAG", "onActivityResult: INI DATANYA YANG DI PILIH ${file.absolutePath}", )
//                        viewModel?.uploadAvatar(file)
                    }
                }
            }
        }
    }

    private fun getRealPathFromURI(uri: Uri): String {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = requireContext().contentResolver.query(uri, projection, null, null, null)
        val columnIndex = cursor?.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor?.moveToFirst()
        val path = columnIndex?.let { cursor.getString(it) }
        cursor?.close()
        return path ?: ""

    }

    override fun onResume() {
        super.onResume()
        startCamera()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacksAndMessages(null)
    }

    private companion object {
        const val CAPTURE_ANIMATION_DURATION = 500L
        const val PHOTO_MODE_INDEX = 1
        private const val MIN_SWIPE_DISTANCE_X = 100
        private const val REQUEST_CODE_IMAGE = 101
        private const val TIMER_2_SECONDS = 2001
    }


}

data class MediaItem(val uri: Uri, val name: String)