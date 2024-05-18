package com.example.dinorun

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class startGameActivity : AppCompatActivity() {

    private lateinit var playButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_start_game)

        playButton=findViewById(R.id.paly)

        // Set OnClickListener for the PLAY button
        playButton.setOnClickListener {
            val startGameIntent = Intent(this, MainActivity::class.java)
            startActivity(startGameIntent);
        }
    }
}