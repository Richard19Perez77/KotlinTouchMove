package com.touchmove.kotlin

import android.os.Bundle
import androidx.fragment.app.FragmentActivity

/**
 * Starting point for application, defines layout in the activity_main file.
 *
 * @author Rick
 *
 * Git created https://Richard19Perez77@bitbucket.org/Richard19Perez77/touchmovementdemo.git
 */
class MainActivity : FragmentActivity() {
    /**
     * Set the layout from XML file.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}