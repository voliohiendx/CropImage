package com.example.customviewtutorial

import android.graphics.*
import java.util.*

class ImageCustom {
    val rectF: RectF = RectF()
    private val rectOrigin:RectF = RectF()
    private val paint: Paint = Paint()

    init {
        val rnd = Random()
        val color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))
        paint.color = color

        rectOrigin.top = 0f
        rectOrigin.bottom = 300f
    }

    fun setLeftRight(left: Int) {
        rectOrigin.left = left.toFloat()
        rectOrigin.right = left.toFloat() + 500f
    }

    fun updateMatrix(matrix: Matrix) {
        matrix.mapRect(rectF,rectOrigin)
    }

    fun draw(canvas: Canvas) {
        canvas.drawRect(rectF, paint)
    }
}