package com.example.customviewtutorial

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.dynamicanimation.animation.FlingAnimation

class TutorialView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    val allMatrix: Matrix = Matrix()
    val saveMatrix: Matrix = Matrix()
    var lastPoint: PointF = PointF()
    private var flingAnimation: FlingAnimation? = null

    // private val listImage: MutableList<ImageCustom> = mutableListOf()
    private val listWeather: MutableList<WeatherCustom> = mutableListOf()

    private val viewBitmap: Bitmap by lazy {
        BitmapFactory.decodeResource(resources, R.drawable.ic_wifi_red)
    }

    init {
//        for (i in 0..10) {
//            val imageCustom = ImageCustom()
//            listImage.add(imageCustom)
//            imageCustom.setLeftRight(5 * i * 100)
//            imageCustom.updateMatrix(allMatrix)
//            postInvalidate()
//        }
        var left = 0f
        for (i in 0..10) {

            val weatherCustom = WeatherCustom(
                WeatherModel(
                    "10:00",
                    "30",
                    "5",
                    ContextCompat.getDrawable(context, R.drawable.ic_wifi_red)?.toBitmap()
                )
            )
            listWeather.add(weatherCustom)
            weatherCustom.setLeftRight(left, (width / 6).toFloat())
            weatherCustom.updateMatrix(allMatrix)
            left += width / 6
            postInvalidate()
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        var left = 0f
        listWeather.forEach {
            it.setLeftRight(left, (width / 6).toFloat())
            left += width / 6
            it.updateMatrix(allMatrix)
        }
        postInvalidate()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.let {
            // for (image in listImage) image.draw(canvas)
            for (weather in listWeather) weather.draw(canvas)
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action?.and(MotionEvent.ACTION_MASK)) {
            MotionEvent.ACTION_DOWN -> {
                flingAnimation?.cancel()
                lastPoint.set(event.x, event.y)
                saveMatrix.set(allMatrix)
            }
            MotionEvent.ACTION_MOVE -> {
                allMatrix.set(saveMatrix)
                allMatrix.postTranslate(event.x - lastPoint.x, 0f)
                updateMatrix()
                standardize()
            }
            MotionEvent.ACTION_POINTER_DOWN -> {
                Log.d("Na00007", "ACTION_POINTER_DOWN: ")
            }
            MotionEvent.ACTION_UP -> {
                saveMatrix.set(allMatrix)
            }
        }
        postInvalidate()
        return true
    }

    private fun standardize() {
        if (listWeather.size > 0) {
            val first = listWeather.first().rectDraw
            val last = listWeather.last().rectDraw
            if (last.right - width < 100) {
                allMatrix.postTranslate(width - last.right - 100, 0f)
                updateMatrix()
            }
            if (first.left > 100) {
                allMatrix.postTranslate(100 - first.left, 0f)
                updateMatrix()
            }
        }
    }

    private fun updateMatrix() {
        for (image in listWeather) image.updateMatrix(allMatrix)
    }

}