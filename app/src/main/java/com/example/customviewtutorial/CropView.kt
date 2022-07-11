package com.example.customviewtutorial

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View

class CropView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    val allMatrix: Matrix = Matrix()
    val saveMatrix: Matrix = Matrix()
    var lastPoint: PointF = PointF()
    val cropCustom = CropCustom()

    private val cropBitmap: Bitmap by lazy {
        BitmapFactory.decodeResource(resources, R.drawable.ic_wifi_red)
    }

    init {
        cropCustom.setWithHeight(width.toFloat(), height.toFloat())
        cropCustom.updateMatrix(allMatrix)
        postInvalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        cropCustom.draw(canvas)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        cropCustom.setWithHeight(width.toFloat(), height.toFloat())
        cropCustom.updateMatrix(allMatrix)
        postInvalidate()
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action?.and(MotionEvent.ACTION_MASK)) {
            MotionEvent.ACTION_DOWN -> {
                lastPoint.set(event.x, event.y)
                saveMatrix.set(allMatrix)
            }
            MotionEvent.ACTION_MOVE -> {
                allMatrix.set(saveMatrix)
                allMatrix.postTranslate(event.x - lastPoint.x, event.y - lastPoint.y)
                cropCustom.updateMatrix(allMatrix)
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
//        Log.d("Na00007", "left: ${cropCustom.rectF.left}")
//        Log.d("Na00007", "right: ${cropCustom.rectF.right}")
//        Log.d("Na00007", "top: ${cropCustom.rectF.top}")


//        if (cropCustom.rectF.right - width > 0) {
//            allMatrix.postTranslate(width.toFloat() - cropCustom.rectF.right, 0f)
//            cropCustom.updateMatrix(allMatrix)
//        }
//        if (cropCustom.rectF.left < 0) {
//            allMatrix.postTranslate(0f, 0f)
//            cropCustom.updateMatrix(allMatrix)
//        }
//        if (cropCustom.rectF.right > 100) {
//            allMatrix.postTranslate(100 - cropCustom.rectF.left, 0f)
//            cropCustom.updateMatrix(allMatrix)
//        }
    }


}