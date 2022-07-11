package com.example.customviewtutorial

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.util.Log

/*
*  A--------B
*  |       |
* |        |
* |        |
* D-------C
* */
class CropCustom {

    val srcPoint: FloatArray = FloatArray(8)
    val dstPoint: FloatArray = FloatArray(8)
    var dstPointSave: FloatArray = FloatArray(8)

    //    val rectF: RectF = RectF()
//    private val rectOrigin: RectF = RectF()
    private val paint: Paint = Paint()
    var with = 0f
    var height = 0f

    init {
        paint.apply {
            color = Color.parseColor("#CC000000")
            strokeWidth = 2f
            style = Paint.Style.STROKE
        }
    }

    private fun FloatArray.Ax(): Float {
        return dstPoint[0]
    }

    private fun FloatArray.Ay(): Float {
        return dstPoint[1]
    }

    private fun FloatArray.Bx(): Float {
        return dstPoint[2]
    }

    private fun FloatArray.By(): Float {
        return dstPoint[3]
    }

    private fun FloatArray.Cx(): Float {
        return dstPoint[4]
    }

    private fun FloatArray.Cy(): Float {
        return dstPoint[5]
    }

    private fun FloatArray.Dx(): Float {
        return dstPoint[6]
    }

    private fun FloatArray.Dy(): Float {
        return dstPoint[7]
    }

    fun setWithHeight(with: Float, height: Float) {
        this.with = with
        this.height = height
        srcPoint[0] = 100f
        srcPoint[1] = 100f
        srcPoint[2] = with - 100f
        srcPoint[3] = 100f
        srcPoint[4] = with - 100f
        srcPoint[5] = height - 100f
        srcPoint[6] = 100f
        srcPoint[7] = height - 100f
    }

    private val matrixInverter = Matrix()
    fun updateMatrix(matrix: Matrix) {
        matrix.invert(matrixInverter)
        Log.d("Hiendxxxx", " vvvvv${dstPoint.Ay()}")
        dstPointSave = dstPoint
        matrix.mapPoints(dstPoint, srcPoint)
        Log.d("Hiendxxxx", " yyyyyyy${dstPoint.Ay()}")
        if (dstPoint.Ax() < 100f) {
            dstPoint[0] = 100f
        }
        if (dstPoint.Ay() <= 100f) {
            dstPoint[1] = 100f
        }

        if (dstPoint.Bx() > srcPoint[2]) {
            dstPoint[2] = with - 100f
        }
        if (dstPoint.By() < 100f) {
            dstPoint[3] = 100f
        }

        if (dstPoint.Cx() > srcPoint[4]) {
            dstPoint[4] = with - 100f
        }
        if (dstPoint.Cy() > srcPoint[5]) {
            dstPoint[5] = height - 100f
        }

        if (dstPoint.Dx() < 100f) {
            dstPoint[6] = 100f
        }
        if (dstPoint.Dy() > srcPoint[7]) {
            dstPoint[7] = height - 100f
        }
        matrixInverter.mapPoints(srcPoint, dstPoint)

//        if (dstPoint.Dx() < 100f) {
//            dstPoint[6] = 100f
//        }
    }

    fun draw(canvas: Canvas) {

        //line A to B
        canvas.drawLine(
            dstPoint.Ax(),
            dstPoint.Ay(),
            dstPoint.Bx(),
            dstPoint.By(),
            paint
        )

        //line B to C
        canvas.drawLine(
            dstPoint.Bx(),
            dstPoint.By(),
            dstPoint.Cx(),
            dstPoint.Cy(),
            paint
        )

        //line C to D
        canvas.drawLine(
            dstPoint.Cx(),
            dstPoint.Cy(),
            dstPoint.Dx(),
            dstPoint.Dy(),
            paint
        )

        //line D to A
        canvas.drawLine(
            dstPoint.Dx(),
            dstPoint.Dy(),
            dstPoint.Ax(),
            dstPoint.Ay(),
            paint
        )

    }

}