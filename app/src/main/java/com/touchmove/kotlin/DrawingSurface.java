package com.touchmove.kotlin;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.TextView;

/**
 * 
 * The drawing surface class will be used to hold the thread and handler for
 * message to the UI.
 * 
 * @author Rick
 *
 */
public class DrawingSurface extends SurfaceView implements
		SurfaceHolder.Callback, OnTouchListener {

	/**
	 * The context object allows access to the resources needed and also
	 * contains information about the application.
	 */
	Context context;

	/**
	 * The thread that runs the cycle of run and update physics during the
	 * applications lifetime.
	 */
	public SurfaceThread targetThread;

	/**
	 * Sends message to the UI via the thread
	 */
	Handler myHandler;

	/**
	 * Set the message to the UI here.
	 */
	public TextView messageTextView;

	/**
	 * True when the introduction animation is started and false when finished.
	 */
	public boolean introAnimationFinished;

	/**
	 * As the application resumes we may need to recreate our thread.
	 */
	boolean recreateSurfaceThread;

	/**
	 * Contains the squares and movements across the screen.
	 */
	public SquareSet squareSet = new SquareSet();

	/**
	 * A class to handle setting the message and them showing the message.
	 */
	class IncomingHandlerCallback implements Handler.Callback {
		/**
		 * The Message object can contain more than one value.
		 */
		@Override
		public boolean handleMessage(final Message m) {
			// handle message code
			messageTextView.setVisibility(m.getData().getInt("show"));
			messageTextView.setText(m.getData().getString("message"));
			return true;
		}
	}

	/**
	 * 
	 * If we are creating our surface by calling the setContentView in the
	 * MainActivity then you must have a constructor in this class that accepts
	 * two parameters.
	 * 
	 * @param con
	 *            The application Context.
	 * @param attrs
	 *            XML defined attributes can be sent though here.
	 */
	public DrawingSurface(Context con, AttributeSet attrs) {
		super(con, attrs);
		context = con;
		// register the call back interface
		SurfaceHolder holder = getHolder();
		holder.addCallback(this);

		// prepare the thread and its message handler (handlers can also execute
		// code if needed)
		myHandler = new Handler(new IncomingHandlerCallback());
		targetThread = new SurfaceThread(getHolder(), con, myHandler, this);
		setOnTouchListener(this);
	}

	/**
	 * Our draw class uses a canvas to draw on and we pass the work to our
	 * targeting class.
	 */
	@Override
	public void draw(Canvas canvas) {
		super.draw(canvas);
		squareSet.draw(canvas);
	}

	/**
	 * When the surface is created we should have a new thread from our class
	 * constructor but if it was running and terminated then need to recreate
	 * it.
	 */
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if (targetThread.getState() == Thread.State.TERMINATED) {
			targetThread = new SurfaceThread(holder, context, myHandler, this);
			targetThread.start();
			targetThread.setRunning(true);
		} else if (targetThread.getState() == Thread.State.NEW) {
			targetThread.start();
			targetThread.setRunning(true);
		}
	}

	/**
	 * Screen dimensions are set at this point and we can record them in the
	 * targeting class.
	 */
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		squareSet.surfaceChanged(holder, format, width, height);
	}

	/**
	 * When the current windows loses or gains touch focus.
	 */
	@Override
	public void onWindowFocusChanged(boolean hasWindowFocus) {
		super.onWindowFocusChanged(hasWindowFocus);
		if (!hasWindowFocus)
			onPause();
		else
			surfaceCreated(getHolder());
	}

	/**
	 * Surface is destroyed and we can let the thread run out its execution
	 * path.
	 */
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		boolean retry = true;
		targetThread.setRunning(false);
		while (retry) {
			try {
				targetThread.join();
				retry = false;
			} catch (InterruptedException e) {
			}
		}
	}

	/**
	 * Actual physics are encapsulated in the targeting class.
	 */
	public void updatePhysics() {
		squareSet.updatePhysics();
	}

	/**
	 * Override for accessibility actions
	 */
	@Override
	public boolean performClick() {
		return super.performClick();
	}

	/**
	 * The touch handler for the surface.
	 */
	public boolean onTouch(View v, MotionEvent event) {
		synchronized (targetThread.mSurfaceHolder) {
			if (event.getAction() == MotionEvent.ACTION_UP) {
				v.performClick();
			}
			// if restarting the thread may not be valid, surface created will
			// not be called to do this for us.
			if (recreateSurfaceThread) {
				recreateSurfaceThread = false;
				if (targetThread.getState() == Thread.State.TERMINATED) {
					targetThread = new SurfaceThread(getHolder(), context,
							myHandler, this);
					targetThread.start();
					targetThread.setRunning(true);
				} else if (targetThread.getState() == Thread.State.NEW) {
					targetThread.start();
					targetThread.setRunning(true);
				}
			}

			/**
			 * If the resume view animation(s) is finished we can perform
			 * actions on the touch events.
			 */
			if (introAnimationFinished)
				switch (targetThread.mMode) {
				case SurfaceThread.STATE_PAUSE:
					targetThread.setState(SurfaceThread.STATE_RUNNING);
					break;
				case SurfaceThread.STATE_RUNNING:
					// application touch logic
					return squareSet.onTouchEvent(event);
				}
			return super.onTouchEvent(event);
		}
	}

	/**
	 * Restart the application by resetting the message to the user and the
	 * square set variables.
	 */
	public void restart() {
		messageTextView.setText("Tap Blue Screen");
		squareSet.restart();
	}

	/**
	 * We need to set the flag to recreate our thread here, we want the method
	 * to be lightweight so we wait to create it on touch when animation is
	 * needed.
	 */
	public void onResume() {
		if (targetThread.getState() == Thread.State.TERMINATED) {
			recreateSurfaceThread = true;
		}
	}

	/**
	 * When we pause the thread we will set a flag and send a message to the
	 * user.
	 */
	public void onPause() {
		if (targetThread != null) {
			targetThread.pause();
			targetThread.setRunning(false);
		}
	}
}