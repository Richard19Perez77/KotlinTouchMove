package com.touchmove.kotlin

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Point
import android.graphics.RectF
import android.os.AsyncTask
import android.view.MotionEvent
import android.view.SurfaceHolder
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Random

/**
 *
 * A class to hold the set of rectangle objects that are drawn to the screen.
 *
 * @author Rick
 */
class SquareSet {
    /**
     * Set relative size of squares to be drawn from touch. 0 is minimized and 1
     * is full draw.
     */
    val SQUARE_RATIO = 0.2

    /**
     * Create random places for squares.
     */
    var random = Random()

    /**
     * Begin with a paused blue screen until tapped to green and running.
     */
    var screenColor = Color.BLUE

    /**
     * System clock text notification.
     */
    var datePaint = Paint()

    /**
     * Store the screen sizes for scaling of objects.
     */
    var screenW = 0
    var screenH = 0

    /**
     * Holds all the current rectangles to draw.
     */
    @get:Synchronized
    private val rectFs = ArrayList<MyRectF>()

    /**
     * The Paint for all of the rectangles
     */
    var rectPaint = Paint()

    /**
     * Flag for creating a new square.
     */
    private var createSquare = false

    /**
     * Flag for clearing all created squares.
     */
    private var clearSquares = false

    /**
     * Flag for the touch event
     */
    private var touchingScreen = false

    /**
     * Set most recently recorded x touch.
     */
    private var newX = 0f

    /**
     * Set most recently recorded y touch.
     */
    private var newY = 0f

    /**
     * Coutner used for draw method calls.
     */
    private var drawCounter = 0

    /**
     * Counter to store touch method calls
     */
    var touchCounter = 0

    /**
     * Square line should disperse during this time
     */
    private var disperseSquares = false
    var simpleDateFormat: SimpleDateFormat
    var formattedDate = ""

    /**
     * Draw method of the application will draw the background and squares here.
     *
     * @param canvas
     */
    fun draw(canvas: Canvas) {
        drawCounter++
        formattedDate = simpleDateFormat.format(Date())
        canvas.drawColor(screenColor)
        canvas.drawText(
            "Date: $formattedDate", 20f,
            datePaint.textSize + 10, datePaint
        )
        canvas.drawText(
            "Squares: " + rectFs.size, 20f,
            datePaint.textSize * 2 + 20, datePaint
        )
        canvas.drawText(
            "Draws: $drawCounter", 20f,
            datePaint.textSize * 3 + 30, datePaint
        )
        canvas.drawText(
            "Touches: $touchCounter", 20f,
            datePaint.textSize * 4 + 40, datePaint
        )

        // draw rectFs in array
        drawRectFs(canvas)
    }

    /**
     * On surface changed we need to record the screen sizes for scaling.
     *
     * @param holder
     * surface holder
     * @param format
     * type of screen
     * @param width
     * @param height
     */
    fun surfaceChanged(
        holder: SurfaceHolder?, format: Int, width: Int,
        height: Int
    ) {
        screenW = width
        screenH = height
    }

    private fun drawRectFs(canvas: Canvas) {
        // draw every rectF in list
        for (rectF in rectFs.toTypedArray<RectF>()) {
            canvas.drawRect(rectF, rectPaint)
        }
    }

    fun updatePhysics() {
        screenColor = Color.WHITE
        if (createSquare) {
            createSquare = false
            RectFFactory().execute()
        } else if (clearSquares) {
            clearSquares = false
            disperseSquares = false
            rectFs.clear()
        } else if (disperseSquares) {
            // disperse squares
            for (rectF in rectFs.toTypedArray()) {
                rectF.updatePoint()
            }
        }
    }

    fun restart() {
        screenColor = Color.BLUE
        createSquare = false
        clearSquares = false
        drawCounter = 0
        rectFs.clear()
    }

    /**
     * touch event is separate from game thread, could create objects from a
     * flag set in on touch in stead of doing work here
     *
     * @param event
     */
    fun onTouchEvent(event: MotionEvent): Boolean {
        newX = event.x
        newY = event.y
        touchCounter++
        return when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                clearSquares = true
                touchingScreen = true
                drawCounter = 0
                touchCounter = 0
                true
            }

            MotionEvent.ACTION_MOVE -> {
                createSquare = true
                true
            }

            MotionEvent.ACTION_UP -> {
                touchingScreen = false
                disperseSquares = true
                true
            }

            else -> false
        }
    }

    internal inner class RectFFactory : AsyncTask<Void?, Void?, Void?>() {
        override fun doInBackground(vararg params: Void?): Void? {
            // the new square should be a random generation pixel of the
            // distance of the shortest side
            val x1 = newX.toInt()
            val x2 = (screenW - newX).toInt()
            val y1 = newY.toInt()
            val y2 = (screenH - newY).toInt()

            // find smallest of the above four ints
            var diff = if (x1 < x2) x1 else x2
            diff = if (diff < y1) diff else y1
            diff = if (diff < y2) diff else y2

            // get random percent 0.0 to 1.0
            val percent = Math.random()
            val res = diff * percent
            val buffer = (res * SQUARE_RATIO).toInt()
            val left = (newX - buffer).toInt()
            val top = (newY - buffer).toInt()
            val right = (newX + buffer).toInt()
            val bottom = (newY + buffer).toInt()
            if (touchingScreen) rectFs.add(
                MyRectF(
                    left.toFloat(),
                    top.toFloat(),
                    right.toFloat(),
                    bottom.toFloat()
                )
            )
            return null
        }
    }

    var plot = PlotPoints()

    /**
     * Create style and colors for Paint objects.
     */
    init {
        rectPaint.style = Paint.Style.STROKE
        rectPaint.color = Color.BLUE
        datePaint.color = Color.RED
        datePaint.textSize = 35f
        simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        formattedDate = simpleDateFormat.format(Date())
    }

    internal inner class MyRectF(left: Float, top: Float, right: Float, bottom: Float) :
        RectF(left, top, right, bottom) {
        var pointLineA: Array<Point?>
        var tempA: Point? = null
        var currentPointA = 0

        init {
            val a = Point(left.toInt(), top.toInt())
            // make the distance point a random number within the screen width
            // and height
            val xvalues = (screenW.toFloat() * Math.random()).toInt()
            val yvalues = (screenH.toFloat() * Math.random()).toInt()
            val b = Point(xvalues, yvalues)
            pointLineA = plot.plotLine(a, b)
        }

        fun updatePoint() {
            currentPointA++
            if (currentPointA < pointLineA.size) {
                tempA = pointLineA[currentPointA]
                if (tempA!!.x > 0 && tempA!!.y > 0) {
                    val xdiff = tempA!!.x + (right - left)
                    val ydiff = tempA!!.y + (bottom - top)
                    set(tempA!!.x.toFloat(), tempA!!.y.toFloat(), xdiff, ydiff)
                }
            }
        }
    }
}