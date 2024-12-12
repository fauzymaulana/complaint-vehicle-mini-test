package com.appero.vehiclecomplaint.domain.entities

data class SnackBar(
    val message: String,
    val status: TypeSnackBar = TypeSnackBar.ERROR,
    var action: (() -> Unit)? = null
)

enum class TypeSnackBar {
    SUCCESS,
    ERROR,
    PROGRESS
}
