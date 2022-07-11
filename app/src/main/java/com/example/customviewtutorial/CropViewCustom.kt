package com.example.customviewtutorial

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

class CropViewCustom @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paint: Paint = Paint()
    private val paintBackground: Paint = Paint()
    val path = Path()
    var pointA: PointF = PointF()
    var pointB: PointF = PointF()
    var pointC: PointF = PointF()
    var pointD: PointF = PointF()
    var pointCenter: PointF = PointF()
    var pointRecCrop: PointF = PointF()
    var lastPoint: PointF = PointF()
    var isPointSelect: Int = 0
    var isPointDown = false
    var ratioView = RatioView.ratio11

    var imageBitmap: Bitmap? = null
    var touchType: TouchType = TouchType.NONE

    private val allMatrix: Matrix = Matrix()
    private val saveMatrix: Matrix = Matrix()
    private var rectOrigin: RectF = RectF()
    var rectDraw: RectF = RectF()
    private var previousDistance = 1f
    var pointCenterScale: PointF = PointF()
    var rotateLast = 0f


    init {
        paint.apply {
            color = ContextCompat.getColor(context, R.color.purple_200)
            strokeWidth = 2f
            style = Paint.Style.FILL
        }

        paintBackground.apply {
            color = ContextCompat.getColor(context, R.color.background_crop)
            style = Paint.Style.FILL
        }

    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        imageBitmap?.let {
            canvas.drawBitmap(it, allMatrix, paint)
        }

        //line A to B
        canvas.drawLine(
            pointA.x,
            pointA.y,
            pointB.x,
            pointB.y,
            paint
        )

        //line B to C
        canvas.drawLine(
            pointB.x,
            pointB.y,
            pointC.x,
            pointC.y,
            paint
        )

        //line C to D
        canvas.drawLine(
            pointC.x,
            pointC.y,
            pointD.x,
            pointD.y,
            paint
        )

        //line D to A
        canvas.drawLine(
            pointD.x,
            pointD.y,
            pointA.x,
            pointA.y,
            paint
        )

        val splitVertically = sqrt(
            ((pointD.y - pointA.y).toDouble().pow(2)
                    + (pointD.x - pointA.x).pow(2))
        )

        val splitHorizontal = sqrt(
            ((pointB.y - pointA.y).toDouble().pow(2)
                    + (pointB.x - pointA.x).pow(2))
        )
        canvas.drawLine(
            pointA.x,
            (pointA.y + splitVertically / 3).toFloat(),
            pointC.x,
            (pointA.y + splitVertically / 3).toFloat(),
            paint
        )

        canvas.drawLine(
            pointA.x,
            (pointA.y + splitVertically * 2 / 3).toFloat(),
            pointC.x,
            (pointA.y + splitVertically * 2 / 3).toFloat(),
            paint
        )

        canvas.drawLine(
            (pointA.x + splitHorizontal / 3).toFloat(),
            pointA.y,
            (pointA.x + splitHorizontal / 3).toFloat(),
            pointD.y,
            paint
        )

        canvas.drawLine(
            (pointA.x + splitHorizontal * 2 / 3).toFloat(),
            pointA.y,
            (pointA.x + splitHorizontal * 2 / 3).toFloat(),
            pointD.y,
            paint
        )

        drawOutLine(canvas)

    }

    private fun drawOutLine(canvas: Canvas) {

        path.apply {
            rewind()
            moveTo(0f, 0f)
            lineTo(pointA.x, pointA.y)
            lineTo(pointB.x, pointB.y)
            lineTo(width.toFloat(), 0f)
            canvas.drawPath(this, paintBackground)

            moveTo(0f, 0f)
            lineTo(pointA.x, pointA.y)
            lineTo(pointD.x, pointD.y)
            lineTo(0f, height.toFloat())
            canvas.drawPath(this, paintBackground)

            moveTo(width.toFloat(), 0f)
            lineTo(pointB.x, pointB.y)
            lineTo(pointC.x, pointC.y)
            lineTo(width.toFloat(), height.toFloat())
            canvas.drawPath(this, paintBackground)

            moveTo(width.toFloat(), height.toFloat())
            lineTo(pointC.x, pointC.y)
            lineTo(pointD.x, pointD.y)
            lineTo(0f, height.toFloat())
            close()
            canvas.drawPath(this, paintBackground)
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        pointA.set(100f, 100f)
        pointB.set(width.toFloat() - 100f, 100f)
        pointC.set(width.toFloat() - 100f, height.toFloat() - 100f)
        pointD.set(100f, height.toFloat() - 100f)
        pointCenter.set((width / 2).toFloat(), (height / 2).toFloat())
        if (ratioView == RatioView.ratio11) {
            if (width < height) {
                pointA.set(100f, 100f)
                pointB.set(width.toFloat() - 100f, 100f)
                pointC.set(width.toFloat() - 100f, width.toFloat() - 100f)
                pointD.set(100f, width.toFloat() - 100f)
            }
        }
        postInvalidate()
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action?.and(MotionEvent.ACTION_MASK)) {
            MotionEvent.ACTION_DOWN -> {
                val pointCheck: PointF = PointF()
                pointCheck.set(event.x, event.y)
                lastPoint = pointCheck

                checkPointerDown(event)

                isPointSelect = if (checkPointIsOnView(pointCheck)) {
                    MOVE_ON_VIEW_CROP
                } else if (checkPointDown(pointCheck, pointA))
                    MOVE_A
                else if (checkPointDown(pointCheck, pointB))
                    MOVE_B
                else if (checkPointDown(pointCheck, pointC))
                    MOVE_C
                else if (checkPointDown(pointCheck, pointD))
                    MOVE_D
                else if (checkEdgeView(pointCheck, pointA, pointB)) {
                    MOVE_AB
                } else if (checkEdgeView(pointCheck, pointB, pointC)) {
                    MOVE_BC
                } else if (checkEdgeView(pointCheck, pointD, pointC)) {
                    MOVE_CD
                } else if (checkEdgeView(pointCheck, pointA, pointD)) {
                    MOVE_DA
                } else MOVE_NONE
            }
            MotionEvent.ACTION_MOVE -> {
                if (touchType == TouchType.POINTER_1) {
                    moveViewCrop(isPointSelect, event)
                } else {
                    if (event.pointerCount == 1) {
//                        allMatrix.set(saveMatrix)
//                        translate(event.x - lastPoint.x, event.y - lastPoint.y)
                    } else {
                        val scale = calculateDistance(event) / previousDistance
                        scale(scale)
                        rotate(event)
                    }
                }
            }
            MotionEvent.ACTION_POINTER_DOWN -> {
                checkPointerDown(event)
            }
            MotionEvent.ACTION_UP -> {
                saveMatrix.set(allMatrix)
                touchType = TouchType.NONE
            }
        }
        postInvalidate()
        return true
    }

    fun checkPointerDown(event: MotionEvent) {
        if (event.pointerCount > 1) {
            touchType = TouchType.POINTER_2
            previousDistance = calculateDistance(event)
            pointCenterScale = calculateMidPoint(event)
            rotateLast = rotation(event)
        } else  {
            touchType = TouchType.POINTER_1
        }
    }

    private fun moveViewCrop(pointSelect: Int, event: MotionEvent) {
        when (pointSelect) {
            MOVE_A -> {
                if (checkPointInScreen(PointF(event.x, event.y))) {
                    val newEvent = getPointTFromX(event.x, pointA, pointC)

                    val pointANew = PointF(newEvent.x, newEvent.y)
                    val pointBNew = PointF(pointB.x, newEvent.y)
                    val pointCNew = PointF(pointC.x, pointC.y)
                    val pointDNew = PointF(newEvent.x, pointD.y)
                    setDraw4PointCrop(pointANew, pointBNew, pointCNew, pointDNew)
                }
            }
            MOVE_B -> {
                if (checkPointInScreen(PointF(event.x, event.y))) {
                    val newEvent = getPointTFromX(event.x, pointB, pointD)

                    val pointANew = PointF(pointA.x, newEvent.y)
                    val pointBNew = PointF(newEvent.x, newEvent.y)
                    val pointCNew = PointF(newEvent.x, pointC.y)
                    val pointDNew = PointF(pointD.x, pointD.y)
                    setDraw4PointCrop(pointANew, pointBNew, pointCNew, pointDNew)
                }
            }
            MOVE_C -> {
                if (checkPointInScreen(PointF(event.x, event.y))) {
                    val newEvent = getPointTFromX(event.x, pointA, pointC)

                    val pointANew = PointF(pointA.x, pointA.y)
                    val pointBNew = PointF(newEvent.x, pointB.y)
                    val pointCNew = PointF(newEvent.x, newEvent.y)
                    val pointDNew = PointF(pointD.x, newEvent.y)
                    setDraw4PointCrop(pointANew, pointBNew, pointCNew, pointDNew)
                }
            }
            MOVE_D -> {
                if (checkPointInScreen(PointF(event.x, event.y))) {
                    val newEvent = getPointTFromX(event.x, pointB, pointD)

                    val pointANew = PointF(newEvent.x, pointA.y)
                    val pointBNew = PointF(pointB.x, pointB.y)
                    val pointCNew = PointF(pointC.x, newEvent.y)
                    val pointDNew = PointF(newEvent.x, newEvent.y)
                    setDraw4PointCrop(pointANew, pointBNew, pointCNew, pointDNew)
                }
            }
            MOVE_ON_VIEW_CROP -> {
                if (checkScreenLimit(lastPoint, PointF(event.x, event.y))) {
                    // val newEvent = getYTFromX(event.x, ponitB, ponitD)
                    val pointANew = PointF(
                        pointA.x + (event.x - lastPoint.x),
                        pointA.y + (event.y - lastPoint.y)
                    )
                    val pointBNew = PointF(
                        pointB.x + (event.x - lastPoint.x),
                        pointB.y + (event.y - lastPoint.y)
                    )
                    val pointCNew = PointF(
                        pointC.x + (event.x - lastPoint.x),
                        pointC.y + (event.y - lastPoint.y)
                    )
                    val pointDNew = PointF(
                        pointD.x + (event.x - lastPoint.x),
                        pointD.y + (event.y - lastPoint.y)
                    )
                    setDraw4PointCrop(pointANew, pointBNew, pointCNew, pointDNew)
                }
            }
            MOVE_AB -> {
                val pointANew = getPointTFromY(event.y, pointA, pointC)
                val pointBNew = getPointTFromY(event.y, pointB, pointD)
                val pointCNew = getPointTFromX(pointBNew.x, pointA, pointC)
                val pointDNew = getPointTFromX(pointANew.x, pointB, pointD)
                setDraw4PointCrop(pointANew, pointBNew, pointCNew, pointDNew)
            }
            MOVE_BC -> {
                val pointBNew = getPointTFromX(event.x, pointB, pointD)
                val pointCNew = getPointTFromX(event.x, pointA, pointC)
                val pointANew = getPointTFromY(pointBNew.y, pointA, pointC)
                val pointDNew = getPointTFromY(pointCNew.y, pointB, pointD)
                setDraw4PointCrop(pointANew, pointBNew, pointCNew, pointDNew)
            }
            MOVE_CD -> {
                val pointCNew = getPointTFromY(event.y, pointA, pointC)
                val pointDNew = getPointTFromY(event.y, pointB, pointD)
                val pointANew = getPointTFromX(pointDNew.x, pointA, pointC)
                val pointBNew = getPointTFromX(pointCNew.x, pointB, pointD)
                setDraw4PointCrop(pointANew, pointBNew, pointCNew, pointDNew)
            }
            MOVE_DA -> {
                val pointANew = getPointTFromX(event.x, pointA, pointC)
                val pointDNew = getPointTFromX(event.x, pointB, pointD)
                val pointBNew = getPointTFromY(pointANew.y, pointB, pointD)
                val pointCNew = getPointTFromY(pointDNew.y, pointA, pointC)
                setDraw4PointCrop(pointANew, pointBNew, pointCNew, pointDNew)

//                ponitD.set(event.x, ponitD.y)
//                ponitA.set(event.x, ponitA.y)
            }
        }
        lastPoint.set(event.x, event.y)
    }

    private fun setDraw4PointCrop(
        pointANew: PointF,
        pointBNew: PointF,
        pointCNew: PointF,
        pointDNew: PointF
    ) {
        if (checkDrawInScreen(pointANew, pointBNew, pointCNew, pointDNew)) {
            pointA.set(pointANew)
            pointB.set(pointBNew)
            pointC.set(pointCNew)
            pointD.set(pointDNew)
        }
    }

    private fun checkPointIsOnView(pointCheck: PointF): Boolean {
        if (pointA.x < pointCheck.x
            && pointCheck.x < pointB.x
            && pointA.y < pointCheck.y
            && pointCheck.y < pointC.y
        ) {
            return true
        }
        return false
    }

    private fun checkPointInScreen(pointCheck: PointF): Boolean {
        if (100f < pointCheck.x
            && pointCheck.x < width - 100
            && 100f < pointCheck.y
            && pointCheck.y < height - 100
        ) {
            return true
        }
        return false
    }

    private fun checkScreenLimit(lastPoint: PointF, currentPoint: PointF): Boolean {
        if (lastPoint.x > currentPoint.x) {
            if ((pointA.x + (currentPoint.x - lastPoint.x)) < 100f) {
                return false
            }
        } else {
            if ((pointB.x + (currentPoint.x - lastPoint.x)) > width - 100f) {
                return false
            }
        }
        if (lastPoint.y > currentPoint.y) {
            if ((pointA.y + (currentPoint.y - lastPoint.y)) < 100f) {
                return false
            }
        } else {
            if ((pointD.y + (currentPoint.y - lastPoint.y)) > height - 100f) {
                return false
            }
        }
        return true
    }


    private fun checkPointDown(pointCheck: PointF, pointDown: PointF): Boolean {
        if (abs(pointCheck.x - pointDown.x) < 50f && abs(pointCheck.y - pointDown.y) < 50f) {
            return true
        }
        return false
    }

    private fun checkEdgeView(pointCheck: PointF, pointStart: PointF, pointEnd: PointF): Boolean {

        if (pointStart.x - 50f < pointCheck.x
            && pointEnd.x + 50f > pointCheck.x
            && pointStart.y - 50f < pointCheck.y
            && pointEnd.y + 50f > pointCheck.y
        ) {
            return true
        }
        return false
    }

    private fun getPointTFromX(x: Float, pointStart: PointF, pointEnd: PointF): PointF {
        return PointF(
            x,
            ((x - pointStart.x) * (pointEnd.y - pointStart.y)) / (pointEnd.x - pointStart.x) + pointStart.y
        )
    }

    private fun getPointTFromY(y: Float, pointStart: PointF, pointEnd: PointF): PointF {
        return PointF(
            ((y - pointStart.y) * (pointEnd.x - pointStart.x)) / (pointEnd.y - pointStart.y) + pointStart.x,
            y
        )
    }

    private fun checkDrawInScreen(
        pointANew: PointF,
        pointBNew: PointF,
        pointCNew: PointF,
        pointDNew: PointF,
    ): Boolean {
        if (theMiddleCompartment(pointANew, pointBNew) > 200
            && theMiddleCompartment(pointANew, pointDNew) > 200
            && theMiddleCompartment(pointANew, pointBNew) < width - 200
            && theMiddleCompartment(pointANew, pointDNew) < height - 200
            && pointANew.x < pointBNew.x
            && pointANew.y < pointDNew.y
            && pointANew.x > 100
            && pointANew.y > 100
            && pointBNew.x < width - 100
            && pointBNew.y > 100
            && pointCNew.x < width - 100
            && pointCNew.y < height - 100
            && pointDNew.x > 100
            && pointDNew.y < height - 100
        ) {
            return true
        }
        return false
    }

    fun setBitmapImage(bitmap: Bitmap) {
        imageBitmap = bitmap
        rectOrigin.top = 0f
        rectOrigin.bottom = bitmap.height.toFloat()
        rectOrigin.left = 0f
        rectOrigin.right = bitmap.width.toFloat()
        allMatrix.mapRect(rectOrigin, rectDraw)
        postInvalidate()
    }

    private fun scale(scale: Float) {
        allMatrix.set(saveMatrix)
        allMatrix.postScale(scale, scale, pointCenterScale.x, pointCenterScale.y)
    }

    private fun rotate(event: MotionEvent) {
        val r = rotation(event) - rotateLast
        allMatrix.postRotate(
            r, (event.getX(0) + event.getX(1)) / 2,
            (event.getY(0) + event.getY(1)) / 2
        )
    }

    private fun rotation(event: MotionEvent): Float {
        val delta_x = (event.getX(0) - event.getX(1)).toDouble()
        val delta_y = (event.getY(0) - event.getY(1)).toDouble()
        val radians = Math.atan2(delta_y, delta_x)
        return Math.toDegrees(radians).toFloat()
    }

    private fun translate(x: Float, y: Float) {
        allMatrix.set(saveMatrix)
        allMatrix.postTranslate(x, y)

    }

    fun moveToCenter(){

    }

    private fun calculateDistance(event: MotionEvent): Float {
        val x = event.getX(0) - event.getX(1)
        val y = event.getY(0) - event.getY(1)
        return sqrt(x * x + y * y.toDouble()).toFloat()
    }

    private fun theMiddleCompartment(pointStart: PointF, pointEnd: PointF): Double {
        return sqrt(
            ((pointEnd.y - pointStart.y).toDouble().pow(2)
                    + (pointEnd.x - pointStart.x).pow(2))
        )
    }

    private fun calculateMidPoint(event: MotionEvent): PointF {
        return PointF((event.getX(0) + event.getX(1)) / 2, (event.getY(0) + event.getY(1)) / 2)
    }


    companion object {
        const val MOVE_A = 1
        const val MOVE_B = 2
        const val MOVE_C = 3
        const val MOVE_D = 4
        const val MOVE_NONE = 0
        const val MOVE_ON_VIEW_CROP = 5
        const val MOVE_AB = 6
        const val MOVE_BC = 7
        const val MOVE_CD = 8
        const val MOVE_DA = 9

    }
}