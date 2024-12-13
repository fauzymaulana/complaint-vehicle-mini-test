package com.appero.vehiclecomplaint.utilities.permission

interface PermissionListener {
  fun   shouldShowRationaleInfo()
  fun   isPermissionGranted(isGranted : Boolean)
  fun   isDenied(isDenied: Boolean)
}