package com.appero.vehiclecomplaint.utilities

class OnClickListenerAdapter<T>(val clickListener: (item: T) -> Unit) {
    fun onClick(item: T) = clickListener(item)
}