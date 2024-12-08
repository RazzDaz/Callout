package com.example.myapplicationdh

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.*
import androidx.lifecycle.ViewModelProvider

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel

    // Timer related variables
    private var timerRunning = false
    private var startTime: Long = 0L
    private var pausedTime: Long = 0L
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var timerText: TextView
    private val updateInterval: Long = 10

    private val runnable = object : Runnable {
        override fun run() {
            if (timerRunning) {
                val elapsed = android.os.SystemClock.elapsedRealtime() - startTime
                updateTimerDisplay(elapsed)
                handler.postDelayed(this, updateInterval)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val deflectionText: TextView = findViewById(R.id.deflectionText)
        val elevationText: TextView = findViewById(R.id.elevationText)
        val previousDeflectionText: TextView = findViewById(R.id.previousDeflectionText)
        val previousElevationText: TextView = findViewById(R.id.previousElevationText)
        val generateButton: Button = findViewById(R.id.generateButton)
        val toggleTimerButton: Button = findViewById(R.id.toggleTimerButton)
        val resetButton: Button = findViewById(R.id.resetButton)
        val resetMortarButton: Button = findViewById(R.id.resetMortarButton)
        timerText = findViewById(R.id.timerText)

        val deflectionModeGroup: RadioGroup = findViewById(R.id.deflectionModeGroup)
        val radioSmall: RadioButton = findViewById(R.id.radioSmall)
        val radioLarge: RadioButton = findViewById(R.id.radioLarge)

        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        // Set initial mortar values
        viewModel.resetMortar()

        viewModel.currentData.observe(this) { data ->
            deflectionText.text = "Deflection: ${data.deflection}"
            elevationText.text = "Elevation: ${data.elevation}"

            // Update previous values if they exist
            val prevDef = viewModel.getPreviousDeflection()
            val prevElev = viewModel.getPreviousElevation()

            previousDeflectionText.text = if (prevDef != null) {
                "Previous Deflection: $prevDef"
            } else {
                "Previous Deflection: " // blank if no previous data
            }

            previousElevationText.text = if (prevElev != null) {
                "Previous Elevation: $prevElev"
            } else {
                "Previous Elevation: " // blank if no previous data
            }
        }

        // Generate new data on button click
        generateButton.setOnClickListener {
            viewModel.generateNewData()
        }

        // Deflection mode change listener
        deflectionModeGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radioSmall -> viewModel.toggleDeflectionMode(false)
                R.id.radioLarge -> viewModel.toggleDeflectionMode(true)
            }
        }

        // Timer toggle button
        toggleTimerButton.setOnClickListener {
            if (!timerRunning) {
                startTimer()
                toggleTimerButton.text = "Stop Timer"
            } else {
                stopTimer()
                toggleTimerButton.text = "Start Timer"
            }
        }

        // Reset timer button
        resetButton.setOnClickListener {
            resetTimer()
            toggleTimerButton.text = "Start Timer"
        }

        // Reset Mortar button
        resetMortarButton.setOnClickListener {
            viewModel.resetMortar()
        }
    }

    private fun startTimer() {
        startTime = android.os.SystemClock.elapsedRealtime() - pausedTime
        timerRunning = true
        handler.post(runnable)
    }

    private fun stopTimer() {
        timerRunning = false
        pausedTime = android.os.SystemClock.elapsedRealtime() - startTime
    }

    private fun resetTimer() {
        stopTimer()
        pausedTime = 0L
        updateTimerDisplay(0L)
    }

    private fun updateTimerDisplay(elapsed: Long) {
        val seconds = elapsed / 1000
        val milliseconds = elapsed % 1000
        timerText.text = String.format("Timer: %d.%03d s", seconds, milliseconds)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(runnable)
    }
}

