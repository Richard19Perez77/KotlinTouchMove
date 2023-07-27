package com.touchmove.kotlin

import android.content.Context
import android.graphics.Canvas
import android.os.Handler
import android.os.Message
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import android.view.View.OnTouchListener
import android.widget.TextView

/**
 *
 * The drawing surface class will be used to hold the thread and handler for
 * message to the UI.
 *
 * @author Rick
 */
class DrawingSurface(
    /**
     * The context object allows access to the resources needed and also
     * contains information about the application.
     */
    var c: Context, attrs: AttributeSet?
) : SurfaceView(c, attrs), SurfaceHolder.Callback, OnTouchListener {
    /**
     * The thread that runs the cycle of run and update physics during the
     * applications lifetime.
     */
    var targetThread: SurfaceThread?

    /**
     * Sends message to the UI via the thread
     */
    var myHandler: Handler

    /**
     * Set the message to the UI here.
     */
    var messageTextView: TextView? = null

    /**
     * True when the introduction animation is started and false when finished.
     */
    var introAnimationFinished = false

    /**
     * As the application resumes we may need to recreate our thread.
     */
    var recreateSurfaceThread = false

    /**
     * Contains the squares and movements across the screen.
     */
    var squareSet = SquareSet()

    /**
     * A class to handle setting the message and them showing the message.
     */
    internal inner class IncomingHandlerCallback : Handler.Callback {
        /**
         * The Message object can contain more than one value.
         */
        override fun handleMessage(m: Message): Boolean {
            // handle message code
            messageTextView!!.visibility = m.data.getInt("show")
            messageTextView!!.text = m.data.getString("message")
            return true
        }
    }

    /**
     *
     * If we are creating our surface by calling the setContentView in the
     * MainActivity then you must have a constructor in this class that accepts
     * two parameters.
     *
     * @param con
     * The application Context.
     * @param attrs
     * XML defined attributes can be sent though here.
     */
    init {
        // register the call back interface
        val holder = holder
        holder.addCallback(this)

        // prepare the thread and its message handler (handlers can also execute
        // code if needed)
        myHandler = Handler(IncomingHandlerCallback())
        targetThread = SurfaceThread(getHolder(), c, myHandler, this)
        setOnTouchListener(this)
    }

    /**
     * Our draw class uses a canvas to draw on and we pass the work to our
     * targeting class.
     */
    override fun draw(canvas: Canvas) {
        super.draw(canvas)
        squareSet.draw(canvas)
    }

    /**
     * When the surface is created we should have a new thread from our class
     * constructor but if it was running and terminated then need to recreate
     * it.
     */
    override fun surfaceCreated(holder: SurfaceHolder) {
        if (targetThread!!.state == Thread.State.TERMINATED) {
            targetThread = SurfaceThread(holder, c, myHandler, this)
            targetThread!!.start()
            targetThread!!.setRunning(true)
        } else if (targetThread!!.state == Thread.State.NEW) {
            targetThread!!.start()
            targetThread!!.setRunning(true)
        }
    }

    /**
     * Screen dimensions are set at this point and we can record them in the
     * targeting class.
     */
    override fun surfaceChanged(
        holder: SurfaceHolder, format: Int, width: Int,
        height: Int
    ) {
        squareSet.surfaceChanged(holder, format, width, height)
    }

    /**
     * When the current windows loses or gains touch focus.
     */
    override fun onWindowFocusChanged(hasWindowFocus: Boolean) {
        super.onWindowFocusChanged(hasWindowFocus)
        if (!hasWindowFocus) onPause() else surfaceCreated(holder)
    }

    /**
     * Surface is destroyed and we can let the thread run out its execution
     * path.
     */
    override fun surfaceDestroyed(holder: SurfaceHolder) {
        var retry = true
        targetThread!!.setRunning(false)
        while (retry) {
            try {
                targetThread!!.join()
                retry = false
            } catch (e: InterruptedException) {
            }
        }
    }

    /**
     * Actual physics are encapsulated in the targeting class.
     */
    fun updatePhysics() {
        squareSet.updatePhysics()
    }

    /**
     * Override for accessibility actions
     */
    override fun performClick(): Boolean {
        return super.performClick()
    }

    /**
     * The touch handler for the surface.
     */
    override fun onTouch(v: View, event: MotionEvent): Boolean {
        synchronized(targetThread!!.mSurfaceHolder) {
            if (event.action == MotionEvent.ACTION_UP) {
                v.performClick()
            }
            // if restarting the thread may not be valid, surface created will
            // not be called to do this for us.
            if (recreateSurfaceThread) {
                recreateSurfaceThread = false
                if (targetThread!!.state == Thread.State.TERMINATED) {
                    targetThread = SurfaceThread(
                        holder, c,
                        myHandler, this
                    )
                    targetThread!!.start()
                    targetThread!!.setRunning(true)
                } else if (targetThread!!.state == Thread.State.NEW) {
                    targetThread!!.start()
                    targetThread!!.setRunning(true)
                }
            }
            /**
             * If the resume view animation(s) is finished we can perform
             * actions on the touch events.
             */
            /**
             * If the resume view animation(s) is finished we can perform
             * actions on the touch events.
             */
            if (introAnimationFinished) when (targetThread!!.mMode) {
                SurfaceThread.STATE_PAUSE -> targetThread!!.setState(SurfaceThread.STATE_RUNNING)
                SurfaceThread.STATE_RUNNING ->                    // application touch logic
                    return squareSet.onTouchEvent(event)
            }
            return super.onTouchEvent(event)
        }
    }

    /**
     * Restart the application by resetting the message to the user and the
     * square set variables.
     */
    fun restart() {
        messageTextView!!.text = "Tap Blue Screen"
        squareSet.restart()
    }

    /**
     * We need to set the flag to recreate our thread here, we want the method
     * to be lightweight so we wait to create it on touch when animation is
     * needed.
     */
    fun onResume() {
        if (targetThread!!.state == Thread.State.TERMINATED) {
            recreateSurfaceThread = true
        }
    }

    /**
     * When we pause the thread we will set a flag and send a message to the
     * user.
     */
    fun onPause() {
        if (targetThread != null) {
            targetThread!!.pause()
            targetThread!!.setRunning(false)
        }
    }
}