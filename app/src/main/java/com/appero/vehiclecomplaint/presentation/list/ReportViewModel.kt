package com.appero.vehiclecomplaint.presentation.list

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.appero.vehiclecomplaint.di.CoreApplication
import com.appero.vehiclecomplaint.domain.entities.Report
import com.appero.vehiclecomplaint.utilities.BaseViewModel
import com.appero.vehiclecomplaint.utilities.ResultState
import io.reactivex.android.schedulers.AndroidSchedulers
//import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class ReportViewModel: BaseViewModel() {
    private val appContainer by lazy { CoreApplication().appContainer }
    private val getAllComplaintUseCase by lazy { appContainer.getAllComplaintUseCase }

    private val _allComplaintResultState by lazy { MutableLiveData<ResultState<List<Report>>>() }
    val allComplaintResultState : LiveData<ResultState<List<Report>>> = _allComplaintResultState

    fun getAllComplaint() {
        val disposable = getAllComplaintUseCase(userId = "NOvVq9vbj23Nqg3")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { resultState ->
                    Log.e("TAG", "getAllComplaint: DATANYA INI DIANYA ${resultState.data?.toList()}", )
                    _allComplaintResultState.value = resultState
                },
                { err ->
                    _allComplaintResultState.value = ResultState.UnknownError(
                        message = err.message.toString(),
                        code = 0,
                        data = null
                    )
                }
            )

        addDisposable(disposable)
    }
}