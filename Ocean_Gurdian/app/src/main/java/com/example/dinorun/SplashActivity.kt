package com.example.dinorun

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ProgressBar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {

    private lateinit var progressBar: ProgressBar
    private var progressStatus = 0
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash)

        progressBar = findViewById(R.id.progressBar)

            val mainIntent = Intent(this@SplashActivity, startGameActivity::class.java)

            // Start a new thread to update the progress bar
            Thread {
                while (progressStatus < 100) {
                    progressStatus++
                    // Update the progress bar on the main thread
                    handler.post {
                        progressBar.progress = progressStatus
                    }
                    try {
                        // Sleep for 50 milliseconds to simulate loading
                        Thread.sleep(50)
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }
                }

                // Start MainActivity after the progress bar reaches 100%
                startActivity(mainIntent)
                finish()
            }.start()


    }
}
