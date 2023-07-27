package com.touchmove.kotlin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.Animation.AnimationListener
import android.view.animation.AnimationUtils
import android.widget.TextView
import android.widget.Toolbar
import androidx.fragment.app.Fragment

/**
 * Fragment class to hold our SurfaceView, the SurfaceThread and the SquareSet
 * to be drawn and manipulated.
 *
 * @author Rick
 */
class SurfaceFragment
/**
 * Allow for options handled by this fragment in the menu based on this
 * fragment.
 */
    : Fragment() {
    /**
     * Our drawing surface object.
     */
    var drawingSurface: DrawingSurface? = null

    /**
     * After the layout is inflated we can reference the surfaceView and
     * messageTextView. Create an animation and track with a boolean so the user
     * has a way of knowing when the application appears to be ready and the
     * application knows when it should accept touch events.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_main, container, false)
        val toolbar = view.findViewById<Toolbar>(R.id.toolbar)
        toolbar.title = "Touch Move Kotlin"
        toolbar.inflateMenu(R.menu.menu_draw)
        toolbar.setOnMenuItemClickListener { menuItem: MenuItem -> onMenuItemSelected(menuItem) }
        drawingSurface = view
            .findViewById<View>(R.id.drawingSurface) as DrawingSurface
        drawingSurface!!.messageTextView = view
            .findViewById<View>(R.id.messageTextView) as TextView
        drawingSurface!!.restart()
        val anim = AnimationUtils.loadAnimation(
            view.context,
            R.anim.intro
        )
        anim.reset()
        anim.setAnimationListener(object : AnimationListener {
            override fun onAnimationStart(animation: Animation) {
                drawingSurface!!.introAnimationFinished = false
            }

            override fun onAnimationRepeat(animation: Animation) {}
            override fun onAnimationEnd(animation: Animation) {
                drawingSurface!!.introAnimationFinished = true
            }
        })
        view.clearAnimation()
        view.startAnimation(anim)
        return view
    }

    fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        if (menuItem.itemId == R.id.action_restart) {
            drawingSurface!!.restart()
            return true
        }
        return false
    }
}