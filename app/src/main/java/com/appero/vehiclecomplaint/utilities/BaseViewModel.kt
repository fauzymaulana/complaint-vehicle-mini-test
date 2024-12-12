package com.appero.vehiclecomplaint.utilities

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.appero.vehiclecomplaint.domain.entities.SnackBar
import com.appero.vehiclecomplaint.domain.entities.TypeSnackBar
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

open class BaseViewModel : ViewModel() {

    private val compositeDisposable by lazy { CompositeDisposable() }

    private val _showSnackBar by lazy { MutableLiveData<SnackBar>() }
    val showSnackBar: LiveData<SnackBar> = _showSnackBar

    private val _errorUnAuthorize by lazy { MutableLiveData<String?>() }
    val errorUnAuthorize: LiveData<String?> = _errorUnAuthorize

    private val _errorForbidden by lazy { MutableLiveData<String?>() }
    val errorForbidden: LiveData<String?> = _errorForbidden

    private val _errorServer by lazy { MutableLiveData<String?>() }
    val errorServer: LiveData<String?> = _errorServer

    private val _isLoadingLogout by lazy { MutableLiveData<Boolean>() }
    val isLoadingLogout: LiveData<Boolean> = _isLoadingLogout

    private val _timezone by lazy { MutableLiveData<String>() }
    val timezone: LiveData<String> = _timezone

    private val _timezoneEncoded by lazy { MutableLiveData<String>() }
    val timezoneEncoded: LiveData<String> = _timezoneEncoded

    fun setTimezone(timezone: String, timezoneEncoded: String) {
        _timezone.value = timezone
        _timezoneEncoded.value = timezoneEncoded
    }

    fun errorUnAuthorize(message: String?) {
        _errorUnAuthorize.value = message
    }

    fun errorServer(message: String?) {
        _errorServer.value = message
    }

    fun errorForbidden(message: String?) {
        _errorForbidden.value = message
    }

    protected fun addDisposable(disposable: Disposable) {
        compositeDisposable.add(disposable)
    }

    private fun clearDisposable() {
        compositeDisposable.clear()
        compositeDisposable.dispose()
    }

    override fun onCleared() {
        clearDisposable()
    }

    fun showSnackBar(message: String, status: TypeSnackBar = TypeSnackBar.ERROR, action: (() -> Unit)?) {
        _showSnackBar.value = SnackBar(
            message = message,
            action = action,
            status = status
        )
    }
}