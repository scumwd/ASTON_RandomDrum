package com.example.aston_randomdrum

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.LinearInterpolator
import android.widget.*
import com.bumptech.glide.Glide
import okhttp3.*
import java.io.IOException
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    private val random = Random(System.currentTimeMillis())
    private var isSpinning: Boolean = false
    private lateinit var rainbowWheelContainer: LinearLayout
    private lateinit var seekBar: SeekBar
    private lateinit var resultImage: ImageView
    private lateinit var rainbowWheelView: RainbowWheelView

    private val colors = listOf(
        Color.RED, Color.rgb(255, 165, 0), Color.YELLOW,
        Color.GREEN, Color.rgb(66, 170, 255),
        Color.BLUE, Color.rgb(138, 43, 226)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val rotateButton = findViewById<Button>(R.id.startButton)
        val resetButton = findViewById<Button>(R.id.resetButton)

        rainbowWheelView = findViewById(R.id.rainbowWheelView)
        rainbowWheelContainer = findViewById(R.id.rainbowWheelContainer)
        resultImage = findViewById(R.id.resultImage)
        seekBar = findViewById(R.id.sizeSeekBar)

        seekBar.max = 100
        seekBar.progress = 50
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                updateRainbowWheelSize(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                //
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                //
            }
        })

        rotateButton.setOnClickListener {
            if (isSpinning) Toast.makeText(
                this,
                "Wheel is spinning",
                Toast.LENGTH_SHORT
            ).show()
            else startSpin()
        }

        resetButton.setOnClickListener {
            resetWheel()
        }
    }

    private fun updateRainbowWheelSize(progress: Int) {
        val rainbowWheelContainerSize = rainbowWheelContainer.width

        val wheelMinSize = (rainbowWheelContainerSize * 0.5).toInt()
        val wheelMaxSize = rainbowWheelContainerSize

        val newWheelSize =
            (wheelMinSize + (progress / 100.0 * (wheelMaxSize - wheelMinSize))).toInt()

        val params = LinearLayout.LayoutParams(newWheelSize, newWheelSize)
        rainbowWheelView.layoutParams = params
        rainbowWheelContainer.requestLayout()
    }

    private fun startSpin() {
        resultImage.setImageResource(R.drawable.placeholder_img)
        rainbowWheelView.isText = false
        rainbowWheelView.postInvalidate()
        rainbowWheelView.rotation = 0f
        isSpinning = true
        val spinDuration = random.nextInt(3, 10) * 1000
        val degreesPerSecond = 420
        val totalDegrees = (spinDuration / 1000) * degreesPerSecond

        spinTheWheel(totalDegrees, spinDuration)
    }

    private fun spinTheWheel(totalDegrees: Int, spinDuration: Int) {
        val animator = ValueAnimator.ofFloat(0f, totalDegrees.toFloat())
        animator.addUpdateListener { valueAnimator ->
            val value = valueAnimator.animatedValue as Float
            rainbowWheelView.rotation = value % 360
        }
        animator.duration = spinDuration.toLong()
        animator.interpolator = LinearInterpolator()
        animator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                isSpinning = false
                rainbowWheelView.postInvalidate()
                decideContent()
            }
        })
        animator.start()
    }

    private fun decideContent() {
        val angleToBottom = (rainbowWheelView.rotation - 90) % 360
        val sectionIndex =
            ((colors.size - (angleToBottom / (360f / colors.size))) % colors.size).toInt()
        rainbowWheelView.currentColor = colors[sectionIndex]

        rainbowWheelView.isText = rainbowWheelView.currentColor == Color.RED ||
                rainbowWheelView.currentColor == Color.YELLOW ||
                //Azure
                rainbowWheelView.currentColor == Color.rgb(66, 170, 255) ||
                //Violet
                rainbowWheelView.currentColor == Color.rgb(138, 43, 226)

        if (rainbowWheelView.isText) {
            rainbowWheelView.postInvalidate()
        } else {
            setImage()
        }
    }

    private fun resetWheel() {
        resultImage.setImageResource(R.drawable.placeholder_img)
        rainbowWheelView.isText = false
        isSpinning = false
        rainbowWheelView.rotation = 0f
        rainbowWheelView.postInvalidate()
    }

    private fun setImage() {
        val url = "https://loremflickr.com/640/360"
        val client = OkHttpClient()
        val request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    throw IOException("Unexpected code $response")
                }

                val imageUrl = response.request.url.toString()

                runOnUiThread {
                    Glide.with(this@MainActivity)
                        .load(imageUrl)
                        .error(R.drawable.error)
                        .placeholder(R.drawable.load_img)
                        .into(resultImage)
                }
            }
        })
    }
}