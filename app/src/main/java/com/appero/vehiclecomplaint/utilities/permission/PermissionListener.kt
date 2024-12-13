package com.klik.adzkia.core.utilities.permission

interface PermissionListener {
  fun   shouldShowRationaleInfo()
  fun   isPermissionGranted(isGranted : Boolean)
  fun   isDenied(isDenied: Boolean)
}