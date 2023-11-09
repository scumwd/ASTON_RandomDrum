package com.example.aston_randomdrum

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View

class RainbowWheelView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    var isText = false
    var currentColor = 0

    private val colors = listOf(
        Color.RED, Color.rgb(255, 165, 0), Color.YELLOW,
        Color.GREEN, Color.rgb(66, 170, 255),
        Color.BLUE, Color.rgb(138, 43, 226)
    )

    private val paint = Paint().apply {
        isAntiAlias = true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (isText) {
            drawText(canvas)
        } else {
            drawWheel(canvas)
        }

    }

    private fun drawWheel(canvas: Canvas) {
        val centerX = (width / 2).toFloat()
        val centerY = (height / 2).toFloat()
        val radius = (width / 2).coerceAtMost(height / 2).toFloat()

        val eachSection = 360f / colors.size
        var startFrom = 0f

        colors.forEachIndexed { _, color ->
            paint.color = color
            val rect = RectF(centerX - radius, centerY - radius, centerX + radius, centerY + radius)
            canvas.drawArc(rect, startFrom, eachSection, true, paint)
            startFrom += eachSection
        }
    }

    private fun drawText(canvas: Canvas) {
        rotation = 0f
        paint.textSize = (height / 5).toFloat()
        paint.style = Paint.Style.FILL
        paint.textSize = 50f
        paint.color = currentColor

        val centerX = (width / 2).toFloat()
        val centerY = (height / 2).toFloat()
        val text = when (currentColor) {
            Color.RED -> "RED"
            Color.YELLOW -> "YELLOW"
            Color.GREEN -> "GREEN"
            Color.BLUE -> "BLUE"
            Color.rgb(255, 165, 0) -> "ORANGE"
            Color.rgb(66, 170, 255) -> "AZURE"
            Color.rgb(138, 43, 226) -> "VIOLET"
            else -> ""
        }

        val textWidth = paint.measureText(text)
        val textX = centerX - textWidth / 2

        canvas.drawText(text, textX, centerY, paint)
    }
}