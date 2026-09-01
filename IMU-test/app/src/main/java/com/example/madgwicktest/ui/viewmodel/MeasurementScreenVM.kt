package com.example.madgwicktest.ui.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import com.example.madgwicktest.data.model.MethodType
import com.example.madgwicktest.data.model.Orientation
import com.example.madgwicktest.data.repository.SensorRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MeasurementScreenVM(
    application: Application
) : AndroidViewModel(application) {

    private val repository =
        SensorRepository(application)

    val orientation: StateFlow<Orientation> =
        repository.orientation

    val accOrientation: StateFlow<Orientation> =
        repository.accOrientation

    val compOrientation: StateFlow<Orientation> =
        repository.compOrientation

    val madgwickOrientation: StateFlow<Orientation> =
        repository.madgwickOrientation

    private val _selectedMethod =
        MutableStateFlow(MethodType.ACCELEROMETER)

    val selectedMethod: StateFlow<MethodType> =
        _selectedMethod

    var recording by mutableStateOf(false)
        private set

    val accPitch =
        mutableStateListOf<Float>()

    val compPitch =
        mutableStateListOf<Float>()

    val madgwickPitch =
        mutableStateListOf<Float>()


    val accRoll =
        mutableStateListOf<Float>()

    val compRoll =
        mutableStateListOf<Float>()

    val madgwickRoll =
        mutableStateListOf<Float>()


    val accYaw =
        mutableStateListOf<Float>()

    val compYaw =
        mutableStateListOf<Float>()

    val madgwickYaw =
        mutableStateListOf<Float>()

    init {
        repository.start()
    }

    fun setMethod(
        method: MethodType
    ) {
        _selectedMethod.value = method
        repository.setMethod(method)
    }


    fun toggleRecording() {

        if (!recording) {
            resetGraphs()
        }

        recording = !recording
    }

    fun resetGraphs() {

        accPitch.clear()
        compPitch.clear()
        madgwickPitch.clear()

        accRoll.clear()
        compRoll.clear()
        madgwickRoll.clear()

        accYaw.clear()
        compYaw.clear()
        madgwickYaw.clear()
    }

    private fun addValue(
        list: MutableList<Float>,
        value: Float
    ) {
        list.add(value)
    }

    fun updateGraphData() {

        if (!recording) return

        addValue(
            accPitch,
            accOrientation.value.roll
        )

        addValue(
            compPitch,
            compOrientation.value.roll
        )

        addValue(
            madgwickPitch,
            madgwickOrientation.value.roll
        )

        addValue(
            accRoll,
            accOrientation.value.pitch
        )

        addValue(
            compRoll,
            compOrientation.value.pitch
        )

        addValue(
            madgwickRoll,
            madgwickOrientation.value.pitch
        )

        addValue(
            accYaw,
            accOrientation.value.yaw
        )

        addValue(
            compYaw,
            compOrientation.value.yaw
        )

        addValue(
            madgwickYaw,
            madgwickOrientation.value.yaw
        )
    }

    override fun onCleared() {
        repository.stop()
        super.onCleared()
    }
}