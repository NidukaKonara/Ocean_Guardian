package com.example.dinorun

import DiverView
import android.os.Bundle
import android.os.Handler
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var diverView: DiverView
    private val handler = Handler()
    private val interval: Long = 30

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        diverView = DiverView(this, resources)
        setContentView(diverView)

        val timer = Timer()
        timer.schedule(object : TimerTask() {
            override fun run() {
                handler.post {
                    diverView.invalidate()
                }
            }
        }, 0, interval)
    }
}
