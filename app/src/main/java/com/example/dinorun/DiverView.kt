import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.media.MediaPlayer
import android.net.Uri
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.appcompat.widget.SwitchCompat
import com.example.dinorun.GameOverActivity
import com.example.dinorun.R
import java.util.*

class DiverView(context: Context, private val resources: Resources) : View(context) {

    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var diver: Bitmap // Array to store all diver frames
    private var currentFrameIndex = 0 // Index to keep track of the current frame
    private val frameDelayMillis = 100 // Delay between frame updates in milliseconds
    private var diverX = 10
    private var diverY: Int = 0
    private var diverSpeed: Int = 0
    private var touch = false
    private var canvasWidth: Int = 0
    private var canvasHeight: Int = 0

    private var yellowX: Int = 0
    private var yellowY: Int = 0
    private var yellowSpeed = 16

    private var greenX: Int = 0
    private var greenY: Int = 0
    private var greenSpeed = 20

    private var turtleX: Int = 0
    private var turtleY: Int = 0
    private var turtleSpeed = 25

    private var score: Int = 0
    private var lifeOfDiver: Int = 0
    private lateinit var backgroundImage: Bitmap
    private val scorePaint = Paint()
    private val life = arrayOfNulls<Bitmap>(2)
    private lateinit var scoreIc: Bitmap
    private lateinit var lifeIc: Bitmap
    private lateinit var settingIc: Bitmap
    private lateinit var tinBitmap: Bitmap
    private val tinWidth = 100
    private val tinHeight = 100
    private lateinit var garbageBitmap: Bitmap
    private val garbageWidth = 100
    private val garbageHeight = 100
    private lateinit var bombBitmap: Bitmap
    private val bombWidth = 150
    private val bombHeight = 150

    private val mediaPlayer: MediaPlayer = MediaPlayer()
    private lateinit var tinSoundPlayer: MediaPlayer
    private lateinit var garbageSoundPlayer: MediaPlayer
    private lateinit var turtleSoundPlayer: MediaPlayer

    init {

        sharedPreferences = context.getSharedPreferences("DinoRunSettings", Context.MODE_PRIVATE)

        // Load diver frames from resources
        diver = BitmapFactory.decodeResource(resources, R.drawable.diver)

        // Define the desired width and height for the diver image
        val desiredWidth = 328 // Adjust this value as needed
        val desiredHeight = 422 // Adjust this value as needed

        // Scale the original bitmaps to the desired size
        diver = Bitmap.createScaledBitmap(diver!!, desiredWidth, desiredHeight, true)

        // Load other bitmaps from resources
        backgroundImage = BitmapFactory.decodeResource(resources, R.drawable.background)
        tinBitmap = BitmapFactory.decodeResource(resources, R.drawable.tin)
        garbageBitmap = BitmapFactory.decodeResource(resources, R.drawable.garbage)
        bombBitmap = BitmapFactory.decodeResource(resources, R.drawable.turtle)
        settingIc = BitmapFactory.decodeResource(resources, R.drawable.settingic)
        scoreIc = BitmapFactory.decodeResource(resources, R.drawable.lifebord)
        lifeIc = BitmapFactory.decodeResource(resources, R.drawable.lifebord)

        scorePaint.color = Color.BLUE
        scorePaint.textSize = 80f
        scorePaint.isAntiAlias = true
        val typeface = ResourcesCompat.getFont(context, R.font.pixel)
        scorePaint.typeface = typeface

        life[0] = BitmapFactory.decodeResource(resources, R.drawable.life1)
        life[1] = BitmapFactory.decodeResource(resources, R.drawable.life2)

        // Initialize the MediaPlayer and load the sound file
        mediaPlayer.apply {
            val rawUri = Uri.parse("android.resource://${context.packageName}/${R.raw.ocean}")
            setDataSource(context, rawUri)
            isLooping = true
            prepareAsync()
            setOnPreparedListener {
                // Start playing the soundtrack only if the music switch is on
                if (isMusicOn()) {
                    start()
                }
            }
        }

        mediaPlayer.isLooping = true // Loop the soundtrack
        mediaPlayer.start() // Start playing the soundtrack

        // Initialize sound players with volume based on saved sound settings
        tinSoundPlayer = MediaPlayer.create(context, R.raw.tin).apply {
            val volume = if (isSoundOn()) 1f else 0f
            setVolume(volume, volume)
        }
        garbageSoundPlayer = MediaPlayer.create(context, R.raw.garbage).apply {
            val volume = if (isSoundOn()) 1f else 0f
            setVolume(volume, volume)
        }
        turtleSoundPlayer = MediaPlayer.create(context, R.raw.turtle).apply {
            val volume = if (isSoundOn()) 1f else 0f
            setVolume(volume, volume)
        }

        // Set initial position and score
        diverY = 700
        score = 0
        lifeOfDiver = 3
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Get canvas dimensions
        canvasWidth = canvas.width
        canvasHeight = canvas.height

        // Draw background image scaled to fit the canvas
        val scaledBackground = Bitmap.createScaledBitmap(backgroundImage, canvasWidth, canvasHeight, true)
        canvas.drawBitmap(scaledBackground, 0f, 0f, null)

        // Ensure correct range for diver y-coordinate
        val minDiverY = diver!!.height
        val maxDiverY = canvasHeight - diver!!.height * 2
        diverY += diverSpeed
        if (diverY < minDiverY) {
            diverY = minDiverY
        }
        if (diverY > maxDiverY) {
            diverY = maxDiverY
        }
        diverSpeed += 2

        // Draw diver frame based on touch state
        if (touch) {
            canvas.drawBitmap(diver!!, diverX.toFloat(), diverY.toFloat(), null) // Display diving animation
            touch = false
        } else {
            // Draw the current diver frame
            canvas.drawBitmap(diver!!, diverX.toFloat(), diverY.toFloat(), null)
        }

        yellowX -= yellowSpeed
        if (hitBallChecker(yellowX, yellowY)) {
            score += 10
            yellowX = canvasWidth + 21 // Move the ball out of the screen
            yellowY = generateRandomY(minDiverY, maxDiverY)

            tinSoundPlayer.start()
        }
        if (yellowX < 0) {
            yellowX = canvasWidth + 21 // Move the ball out of the screen
            yellowY = generateRandomY(minDiverY, maxDiverY)
        }
        val resizedTinBitmap = Bitmap.createScaledBitmap(tinBitmap, tinWidth, tinHeight, true)
        canvas.drawBitmap(resizedTinBitmap, yellowX.toFloat(), yellowY.toFloat(), null)

        greenX -= greenSpeed
        if (hitBallChecker(greenX, greenY)) {
            score += 20
            greenX = canvasWidth + 21 // Move the ball out of the screen
            greenY = generateRandomY(minDiverY, maxDiverY)

            garbageSoundPlayer.start()
        }
        if (greenX < 0) {
            greenX = canvasWidth + 21 // Move the ball out of the screen
            greenY = generateRandomY(minDiverY, maxDiverY)
        }
        val resizedGarbageBitmap = Bitmap.createScaledBitmap(garbageBitmap, garbageWidth, garbageHeight, true)
        canvas.drawBitmap(resizedGarbageBitmap, greenX.toFloat(), greenY.toFloat(), null)

        turtleX -= turtleSpeed
        if (hitBallChecker(turtleX, turtleY)) {
            turtleX = -100
            lifeOfDiver--
            turtleSoundPlayer.start()
            if (lifeOfDiver == 0) {
                Toast.makeText(context, "Game Over", Toast.LENGTH_SHORT).show()
                val gameOverIntent = Intent(context, GameOverActivity::class.java)
                gameOverIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                gameOverIntent.putExtra("Score", score)
                context.startActivity(gameOverIntent)
            }
        }
        if (turtleX < 0) {
            turtleX = canvasWidth + 21 // Move the ball out of the screen
            turtleY = generateRandomY(minDiverY, maxDiverY)
        }
        val resizedTurtleBitmap = Bitmap.createScaledBitmap(bombBitmap, bombWidth, bombHeight, true)
        canvas.drawBitmap(resizedTurtleBitmap, turtleX.toFloat(), turtleY.toFloat(), null)

        val scoreBitmapWidth = 440
        val scoreBitmapHeight = 160
        val scoreBitmapX = 40
        val scoreBitmapY = 100
        val scaledScoreBitmap = Bitmap.createScaledBitmap(scoreIc, scoreBitmapWidth, scoreBitmapHeight, true)
        canvas.drawBitmap(scaledScoreBitmap, scoreBitmapX.toFloat(), scoreBitmapY.toFloat(), null)

        val lifebordBitmapWidth = 440
        val lifebordBitmapHeight = 160
        val lifebordBitmapX = 40
        val lifebordBitmapY = 300
        val scaledlifebordBitmap = Bitmap.createScaledBitmap(lifeIc, lifebordBitmapWidth, lifebordBitmapHeight, true)
        canvas.drawBitmap(scaledlifebordBitmap, lifebordBitmapX.toFloat(), lifebordBitmapY.toFloat(), null)

        // Draw score text
        val scoreTextX = 120
        val scoreTextY = 200
        canvas.drawText("" + score, scoreTextX.toFloat(), scoreTextY.toFloat(), scorePaint)

        // Draw life bitmaps
        val lifeBitmapWidth = 75
        val lifeBitmapHeight = 75
        val lifeBitmapSpacing = 10
        for (i in 0 until 3) {
            val lifeBitmapX = 120 + (lifeBitmapWidth + lifeBitmapSpacing) * i
            val lifeBitmapY = 340
            if (i < lifeOfDiver) {
                val scaledLifeBitmap = Bitmap.createScaledBitmap(life[0]!!, lifeBitmapWidth, lifeBitmapHeight, true)
                canvas.drawBitmap(scaledLifeBitmap, lifeBitmapX.toFloat(), lifeBitmapY.toFloat(), null)
            } else {
                val scaledLifeBitmap2 = Bitmap.createScaledBitmap(life[1]!!, lifeBitmapWidth, lifeBitmapHeight, true)
                canvas.drawBitmap(scaledLifeBitmap2, lifeBitmapX.toFloat(), lifeBitmapY.toFloat(), null)
            }
        }

        //Draw setting ic

        val settingBitmapWidth = 150
        val settingBitmapHeight = 150
        val settingBitmapX = 850
        val settingBitmapY = 150
        val scaledSettingBitmap = Bitmap.createScaledBitmap(settingIc, settingBitmapWidth, settingBitmapHeight, true)
        canvas.drawBitmap(scaledSettingBitmap, settingBitmapX.toFloat(), settingBitmapY.toFloat(), null)

        // Increment frame index for the next frame
        currentFrameIndex = (currentFrameIndex + 1) % 10

        // Request redraw after delay
        postInvalidateDelayed(frameDelayMillis.toLong())
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            // Check if touch event is within setting icon bounds
            val settingBitmapX = 850
            val settingBitmapY = 150
            val settingBitmapWidth = 120
            val settingBitmapHeight = 120
            if (event.x >= settingBitmapX && event.x <= settingBitmapX + settingBitmapWidth &&
                event.y >= settingBitmapY && event.y <= settingBitmapY + settingBitmapHeight
            ) {
                // Show custom dialog
                showCustomDialog()
            } else {
                // Handle other touch events
                touch = true
                diverSpeed = -22
            }
        }
        return true
    }

    private fun showCustomDialog() {
        // Create custom dialog
        val dialog = Dialog(context, R.style.CustomDialogTheme)
        dialog.setContentView(R.layout.custom_dialog_box)
        val musicSwitch = dialog.findViewById<SwitchCompat>(R.id.music)
        val soundSwitch = dialog.findViewById<SwitchCompat>(R.id.sound)

        // Set initial state of switches based on saved preferences
        musicSwitch.isChecked = isMusicOn()
        soundSwitch.isChecked = isSoundOn()

        musicSwitch.setOnCheckedChangeListener { _, isChecked ->
            // Save the state of the music switch
            saveMusicSetting(isChecked)
            // Pause or resume the MediaPlayer based on the state of the music switch
            if (isChecked) {
                mediaPlayer.start()
            } else {
                mediaPlayer.pause()
            }
        }

        soundSwitch.setOnCheckedChangeListener { _, isChecked ->
            // Save the state of the sound switch
            saveSoundSetting(isChecked)
        }

        dialog.show()
    }

    private fun saveMusicSetting(isMusicOn: Boolean) {
        // Save the state of the music switch to SharedPreferences
        val editor = sharedPreferences.edit()
        editor.putBoolean("MusicOn", isMusicOn)
        editor.apply()
    }

    private fun saveSoundSetting(isSoundOn: Boolean) {
        // Save the state of the sound switch to SharedPreferences
        val editor = sharedPreferences.edit()
        editor.putBoolean("SoundOn", isSoundOn)
        editor.apply() // Persist the changes
        // Update the state of sound players based on the switch state
        updateSoundPlayers(isSoundOn)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        // Release the MediaPlayer resources when the view is destroyed
        mediaPlayer.release()
        tinSoundPlayer.release()
        garbageSoundPlayer.release()
        turtleSoundPlayer.release()
    }

    private fun isMusicOn(): Boolean {
        // Retrieve the state of the music switch from SharedPreferences
        return sharedPreferences.getBoolean("MusicOn", true) // Default value is true if not found
    }

    private fun isSoundOn(): Boolean {
        // Retrieve the state of the sound switch from SharedPreferences
        return sharedPreferences.getBoolean("SoundOn", true) // Default value is true if not found
    }

    private fun updateSoundPlayers(isSoundOn: Boolean) {
        // Mute or unmute the sound players based on the switch state
        if (!isSoundOn) {
            tinSoundPlayer.setVolume(0f, 0f)
            garbageSoundPlayer.setVolume(0f, 0f)
            turtleSoundPlayer.setVolume(0f, 0f)
        } else {
            tinSoundPlayer.setVolume(1f, 1f)
            garbageSoundPlayer.setVolume(1f, 1f)
            turtleSoundPlayer.setVolume(1f, 1f)
        }
    }

    // Method to check if ball hits diver
    private fun hitBallChecker(x: Int, y: Int): Boolean {
        return diverX < x && x < diverX + diver!!.width && diverY < y && y < diverY + diver!!.height
    }

    // Method to generate random y-coordinate
    private fun generateRandomY(min: Int, max: Int): Int {
        val random = Random()
        return random.nextInt(max - min) + min
    }
}
