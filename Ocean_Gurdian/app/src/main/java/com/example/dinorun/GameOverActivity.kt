package com.example.dinorun

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class GameOverActivity : AppCompatActivity() {

    private lateinit var startGameAgain: Button
    private lateinit var displayScore: TextView
    private lateinit var highScoreTextView: TextView
    private var score: Int = 0
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_game_over)

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("DinoRunPreferences", Context.MODE_PRIVATE)

        // Get the score from the intent extras
        score = intent.getIntExtra("Score", 0)

        // Initialize views
        startGameAgain = findViewById(R.id.playAgain)
        displayScore = findViewById(R.id.displayScore)
        highScoreTextView = findViewById(R.id.highscore)

        // Set OnClickListener for the play again button
        startGameAgain.setOnClickListener {
            val mainIntent = Intent(this, MainActivity::class.java)
            startActivity(mainIntent)
            finish() // Finish the current activity when starting MainActivity
        }

        // Set the score text
        displayScore.text = "$score"

        // Get the saved high score from SharedPreferences
        val savedHighScore = sharedPreferences.getInt(HIGH_SCORE_KEY, DEFAULT_HIGH_SCORE)

        // Compare current score with saved high score
        if (score > savedHighScore) {
            // If current score is higher, save it as the new high score
            saveHighScore(score)
            // Update the high score text view
            highScoreTextView.text = "$score"
        } else {
            // If current score is not higher, display the saved high score
            highScoreTextView.text = "$savedHighScore"
        }
    }

    private fun saveHighScore(highScore: Int) {
        // Save the high score in SharedPreferences
        sharedPreferences.edit().putInt(HIGH_SCORE_KEY, highScore).apply()
    }

    companion object {
        private const val HIGH_SCORE_KEY = "HighScore"
        private const val DEFAULT_HIGH_SCORE = 0
    }
}
