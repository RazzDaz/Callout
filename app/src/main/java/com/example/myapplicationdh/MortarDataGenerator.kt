package com.example.myapplicationdh

import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

object MortarDataGenerator {
    private val defMin = 2800
    private val defMax = 3600
    private val elevMin = 800
    private val elevMax = 1200

    // For small mode
    private val smallDefRange = 10..60
    private val smallElevRange = 1..90

    // For large mode
    private val largeDefRange = 60..600
    private val largeElevRange = 91..390

    fun generateRandomData(
        largeDeflection: Boolean,
        lastDeflection: Int,
        lastElevation: Int
    ): MortarData {
        val (defOffsetRange, elevOffsetRange) = if (largeDeflection) {
            largeDefRange to largeElevRange
        } else {
            smallDefRange to smallElevRange
        }

        val newDeflection = generateNewValue(
            lastValue = lastDeflection,
            offsetRange = defOffsetRange,
            minVal = defMin,
            maxVal = defMax
        )

        val newElevation = generateNewValue(
            lastValue = lastElevation,
            offsetRange = elevOffsetRange,
            minVal = elevMin,
            maxVal = elevMax
        )

        return MortarData(newDeflection, newElevation)
    }

    private fun generateNewValue(
        lastValue: Int,
        offsetRange: IntRange,
        minVal: Int,
        maxVal: Int
    ): Int {
        val maxAttempts = 50
        repeat(maxAttempts) {
            val offset = offsetRange.random()
            var sign = if (Random.nextBoolean()) 1 else -1

            var candidate = lastValue + offset * sign

            // If out of range, flip direction
            if (candidate < minVal || candidate > maxVal) {
                sign = -sign
                candidate = lastValue + offset * sign
            }

            // Check range again
            if (candidate < minVal || candidate > maxVal) {
                return@repeat
            }

            // Ensure not the same as last value
            if (candidate == lastValue) {
                return@repeat
            }

            // Found a suitable candidate
            return candidate
        }

        // If no suitable value found, just return lastValue
        return lastValue
    }
}
