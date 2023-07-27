package com.touchmove.kotlin

import androidx.activity.ComponentActivity
import android.os.Bundle

/**
 * Starting point for application, defines layout in the activity_main file.
 *
 * @author Rick
 *
 * Git created https://Richard19Perez77@bitbucket.org/Richard19Perez77/touchmovementdemo.git
 */
class MainActivity : ComponentActivity() {
    /**
     * Set the layout from XML file.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}