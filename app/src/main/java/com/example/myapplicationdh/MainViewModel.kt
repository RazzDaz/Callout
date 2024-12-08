package com.example.myapplicationdh

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {

    private val _currentData = MutableLiveData<MortarData>()
    val currentData: LiveData<MortarData> = _currentData

    var largeDeflectionMode = true

    // Track current and previous values
    private var lastDeflection = 3200
    private var lastElevation = 1100
    private var previousDeflection: Int? = null
    private var previousElevation: Int? = null

    fun toggleDeflectionMode(isLarge: Boolean) {
        largeDeflectionMode = isLarge
    }

    fun generateNewData() {
        // Move last values into previous before generating new ones
        previousDeflection = lastDeflection
        previousElevation = lastElevation

        val newData = MortarDataGenerator.generateRandomData(
            largeDeflection = largeDeflectionMode,
            lastDeflection = lastDeflection,
            lastElevation = lastElevation
        )

        lastDeflection = newData.deflection
        lastElevation = newData.elevation
        _currentData.value = newData
    }

    fun resetMortar() {
        // Reset to initial values and clear previous
        previousDeflection = null
        previousElevation = null
        lastDeflection = 3200
        lastElevation = 1100
        _currentData.value = MortarData(3200, 1100)
    }

    fun getPreviousDeflection(): Int? = previousDeflection
    fun getPreviousElevation(): Int? = previousElevation
}



