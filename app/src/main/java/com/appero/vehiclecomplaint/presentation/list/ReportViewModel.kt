package com.appero.vehiclecomplaint.presentation.list

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.appero.vehiclecomplaint.di.CoreApplication
import com.appero.vehiclecomplaint.domain.entities.FormVehicle
import com.appero.vehiclecomplaint.domain.entities.Report
import com.appero.vehiclecomplaint.domain.entities.Vehicle
import com.appero.vehiclecomplaint.utilities.base.BaseViewModel
import com.appero.vehiclecomplaint.utilities.helpers.ResultState
import io.reactivex.android.schedulers.AndroidSchedulers
//import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class ReportViewModel: BaseViewModel() {
    private val appContainer by lazy { CoreApplication().appContainer }
    private val getAllComplaintUseCase by lazy { appContainer.getAllComplaintUseCase }
    private val postAddComplaintUseCase by lazy { appContainer.postAddComplaintUseCase }
    private val getAllVehicleUseCase by lazy { appContainer.getAllVehicleUseCase }

    private val _allComplaintResultState by lazy { MutableLiveData<ResultState<List<Report>>>() }
    val allComplaintResultState : LiveData<ResultState<List<Report>>> = _allComplaintResultState

    private val _allVehicleResultState by lazy { MutableLiveData<ResultState<List<Vehicle>>>() }
    val allVehicleResultState: LiveData<ResultState<List<Vehicle>>> = _allVehicleResultState

    private val _addComplaintResultState by lazy { MutableLiveData<ResultState<Int>>() }
    val addComplaintResultState: LiveData<ResultState<Int>> = _addComplaintResultState
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

    fun getAllVehicle() {
        val disposable = getAllVehicleUseCase()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { resultState ->
                    Log.e("TAG", "getAllComplaint: DATANYA INI DIANYA ${resultState.data?.toList()}", )
                    _allVehicleResultState.value = resultState
                },
                { err ->
                    _allVehicleResultState.value = ResultState.UnknownError(
                        message = err.message.toString(),
                        code = 0,
                        data = null
                    )
                }
            )

        addDisposable(disposable)
    }

    fun postComplaint(formReq: FormVehicle) {
        val disposable = postAddComplaintUseCase(formReq)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { resultState ->
                    Log.e("TAG", "postComplaint: DATANYA INI DIANYA ${resultState.data}", )
                    _addComplaintResultState.value = resultState
                },
                { err ->
                    _addComplaintResultState.value = ResultState.UnknownError(
                        message = err.message.toString(),
                        code = 0,
                        data = null
                    )
                }
            )

        addDisposable(disposable)
    }
}