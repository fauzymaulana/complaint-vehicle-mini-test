package com.appero.vehiclecomplaint.utilities.permission

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

class PermissionHelper(context: Fragment, permissionListener: PermissionListener) {

    private var context: Fragment? = null
    private var permissionListener : PermissionListener? = null

    init {
        this.context = context
        this.permissionListener = permissionListener
    }

    private val requestPermissionLauncher =
        context.registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                Log.i("Permission: ", "Granted")
                permissionListener.isPermissionGranted(true)
            } else {
                Log.i("Permission: ", "Denied")
            }
        }

    private val requestMultiplePermissionsLauncher = context.registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val arrStr = ArrayList<String>()
        permissions.entries.forEach {
            if(it.value){
                println("Successful......")
                permissionListener.isPermissionGranted(true)
            }
            else{
                Log.e("TAG", "INI LA: ", )
                arrStr.add(it.key)
            }
        }
        if (arrStr.size >= 1){
            permissionListener.isDenied(true)
        }
    }

    fun checkForPermissions(manifestPermission: String) {
        when {
            context?.requireContext()?.let {
                ContextCompat.checkSelfPermission(
                    it,
                    manifestPermission
                )
            } == PackageManager.PERMISSION_GRANTED -> {
                println("Permission Granted....")
                permissionListener?.isPermissionGranted(true)
            }

            ActivityCompat.shouldShowRequestPermissionRationale(
                context?.requireContext() as Activity,
                manifestPermission
            ) -> {
                println("Show Request Permission Rationale")
                permissionListener?.isPermissionGranted(false)
                permissionListener?.shouldShowRationaleInfo()

            }

            else -> {
                launchPermissionDialog(manifestPermission)
                println("Final Else....")
            }
        }
    }

    private var isDenied : Boolean  = false
    fun checkForMultiplePermissions(manifestPermissions: Array<String>) {
          for (permission in manifestPermissions) {
              when {
                  hasPermission(permission) -> {
                      permissionListener?.isPermissionGranted(true)
                  }
                  shouldShowStoragePermissionRationale(permission) -> {
                      permissionListener?.shouldShowRationaleInfo()
                  }
                  else -> {
                      requestMultiplePermissionsLauncher.launch(manifestPermissions)
                  }
              }
          }
    }

    private fun hasPermission(permission: String): Boolean {
        return context?.requireContext()?.let {
            ContextCompat.checkSelfPermission(
                it,
                permission
            )
        } == PackageManager.PERMISSION_GRANTED
    }

    private fun shouldShowStoragePermissionRationale(permission: String): Boolean {
        return ActivityCompat.shouldShowRequestPermissionRationale(
            context?.requireContext() as Activity,
            permission
        )
    }

   fun launchPermissionDialogForMultiplePermissions(manifestPermissions: Array<String>){
       requestMultiplePermissionsLauncher.launch(manifestPermissions)
   }
    private fun launchPermissionDialog(manifestPermission: String){
       requestPermissionLauncher.launch(
                    manifestPermission
                )
    }

    companion object {
        val STORAGE_PERMISSION = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(
                Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.READ_MEDIA_AUDIO,
                Manifest.permission.READ_MEDIA_VIDEO
            )
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        } else {
            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        }

        val CAMERA_PERMISSION by lazy {
            arrayOf(
                Manifest.permission.CAMERA
            )
        }
    }
}