package com.touchmove.kotlin;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.SurfaceHolder;
import android.view.View;

/**
 * 
 * A class to run the update physics and draw loop.
 * 
 * @author Rick
 *
 */
public class SurfaceThread extends Thread {

	/**
	 * The context object allows access to the resources needed and also
	 * contains information about the application.
	 */
	Context context;

	/**
	 * The thread that runs the cycle of run and update physics during the
	 * applications lifetime.
	 */
	private DrawingSurface drawingSurface;

	/**
	 * A reference to the surface we are working on, we synchronize on this to
	 * ensure it is valid.
	 */
	SurfaceHolder mSurfaceHolder;

	/**
	 * Sends message to the UI via the thread
	 */
	public Handler mHandler;

	/**
	 * Flag that is used to tell the thread to start updating or drawing.
	 */
	public boolean mRun = false;

	/**
	 * Mode states tell us what implementation of the game we should be using
	 * and when to send messages to the UI.
	 */
	public int mMode;

	/**
	 * Thread is paused.
	 */
	public final static int STATE_PAUSE = 0;

	/**
	 * Thread is running
	 */
	public final static int STATE_RUNNING = 1;

	/**
	 * Constructs a new thread for our application loop.
	 * 
	 * @param surfaceHolder
	 *            reference to the drawing surface's holder.
	 * @param con
	 *            our application context.
	 * @param handler
	 *            the message handler to show messages or run code.
	 * @param drawSurface
	 *            the actual surface we are drawing on.
	 */
	public SurfaceThread(SurfaceHolder surfaceHolder, Context con,
			Handler handler, DrawingSurface drawSurface) {
		mSurfaceHolder = surfaceHolder;
		context = con;
		mHandler = handler;
		drawingSurface = drawSurface;
		mMode = STATE_PAUSE;
	}

	/**
	 * The loop here if running will draw the screen to the user but not animate
	 * it until the mode has changed.
	 */
	@Override
	public void run() {
		while (mRun) {
			Canvas c = null;
			try {
				c = mSurfaceHolder.lockCanvas(null);
				if (c != null) {
					synchronized (mSurfaceHolder) {
						if (mMode == STATE_RUNNING) {
							drawingSurface.updatePhysics();
						}
						drawingSurface.draw(c);
					}
				}
			} finally {
				if (c != null) {
					mSurfaceHolder.unlockCanvasAndPost(c);
				}
			}
		}
	}

	/**
	 * Sets the flag to let us know if the thread should start drawing or
	 * updating.
	 * 
	 * @param b
	 */
	public void setRunning(boolean b) {
		synchronized (mSurfaceHolder) {
			mRun = b;
		}
	}

	/**
	 * Set a new application state.
	 * 
	 * @param mode
	 *            the state to set.
	 */
	public void setState(int mode) {
		synchronized (mSurfaceHolder) {
			setState(mode, null);
		}
	}

	/**
	 * Sets the state and creates a message to the UI if needed. Use the Bundle
	 * object to carry the message and if to show it.
	 * 
	 * @param mode
	 *            the new state.
	 * @param message
	 *            the message to the UI.
	 */
	public void setState(int mode, CharSequence message) {
		synchronized (mSurfaceHolder) {
			mMode = mode;
			Message msg;
			Bundle bundle;
			switch (mMode) {
			case STATE_RUNNING:
				msg = mHandler.obtainMessage();
				Bundle b = new Bundle();
				b.putString("message", "");
				b.putInt("show", View.INVISIBLE);
				msg.setData(b);
				mHandler.sendMessage(msg);
				break;
			case STATE_PAUSE:
				Resources res = context.getResources();
				CharSequence str = "";
				str = res.getText(R.string.message_text);

				if (message != null) {
					str = message + "\n" + str;
				}

				msg = mHandler.obtainMessage();
				bundle = new Bundle();
				bundle.putString("message", str.toString());
				bundle.putInt("show", View.VISIBLE);
				msg.setData(bundle);
				mHandler.sendMessage(msg);
				break;
			}
		}
	}

	/**
	 * Handles putting the thread into a pause state.
	 */
	public void pause() {
		synchronized (mSurfaceHolder) {
			setState(STATE_PAUSE);
		}
	}
}