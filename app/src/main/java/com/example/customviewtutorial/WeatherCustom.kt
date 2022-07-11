package com.example.customviewtutorial

import android.graphics.*

class WeatherCustom(val weatherModel: WeatherModel) {
    private var rectOrigin: RectF = RectF()
    var rectDraw: RectF = RectF()
    private val paint: Paint = Paint()
    private val paintDashed: Paint = Paint()

    init {
        paint.color = Color.WHITE
        paint.textSize = 50f

        paintDashed.style = Paint.Style.STROKE
        paintDashed.pathEffect = DashPathEffect(floatArrayOf(15f, 15f), 0f)
        paintDashed.color = Color.BLACK
        paintDashed.strokeWidth = 3f
    }

    fun setLeftRight(left: Float, space: Float) {
        rectOrigin.left = left
        rectOrigin.right = left + space
    }

    fun updateMatrix(matrix: Matrix) {
        matrix.mapRect(rectDraw, rectOrigin)
    }

    fun draw(canvas: Canvas) {
        val textCurrent = "10:00"
        val textTemp = "35"
        val start = rectDraw.left
        val end = rectDraw.right
        val center = (start + end) / 2

        val rectTime = Rect()
        paint.getTextBounds(textCurrent, 0, textCurrent.length, rectTime)

        canvas.drawText(
            textCurrent,
            0,
            textCurrent.length,
            center - rectTime.width() / 2,
            100f,
            paint
        )

        val rectTemp = Rect()
        paint.getTextBounds(textTemp, 0, textTemp.length, rectTemp)
        canvas.drawText(
            textTemp,
            0,
            textTemp.length,
            center - rectTemp.width() / 2,
            300f,
            paint
        )

//
//        canvas.drawLine(start, 300f, start + 200, 300f, paint)
//
//        canvas.drawLine(start + 70, 300f, start + 75, 400f, paintDashed)
//

        canvas.drawLine(start, 450f, center, 400f, paint)
        canvas.drawLine(center, 400f, end, 450f, paint)

        canvas.drawLine(start , 550f, end, 550f, paintDashed)


        canvas.drawBitmap(
            weatherModel.bitmap!!,
            center - weatherModel.bitmap.width / 2,
            150f,
            paint
        )

        canvas.drawCircle(
            center,
            400f,
           15f,
            paint
        )

    }
}