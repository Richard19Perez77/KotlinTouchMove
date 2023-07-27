package com.touchmove.kotlin;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

/**
 * 
 * A class to hold the set of rectangle objects that are drawn to the screen.
 * 
 * @author Rick
 *
 */
public class SquareSet {

	/**
	 * Set relative size of squares to be drawn from touch. 0 is minimized and 1
	 * is full draw.
	 */
	final double SQUARE_RATIO = 0.2;

	/**
	 * Create random places for squares.
	 */
	Random random = new Random();

	/**
	 * Begin with a paused blue screen until tapped to green and running.
	 */
	int screenColor = Color.BLUE;

	/**
	 * System clock text notification.
	 */
	Paint datePaint = new Paint();

	/**
	 * Running time counter for user feedback of thread running.
	 */
	Date date = new Date();

	/**
	 * Store the screen sizes for scaling of objects.
	 */
	int screenW, screenH;

	/**
	 * Holds all the current rectangles to draw.
	 */
	private ArrayList<MyRectF> rectFs = new ArrayList<MyRectF>();

	/**
	 * The Paint for all of the rectangles
	 */
	Paint rectPaint = new Paint();

	/**
	 * Flag for creating a new square.
	 */
	private boolean createSquare;

	/**
	 * Flag for clearing all created squares.
	 */
	private boolean clearSquares;

	/**
	 * Flag for the touch event
	 */
	private boolean touchingScreen;

	/**
	 * Set most recently recorded x touch.
	 */
	private float newX;

	/**
	 * Set most recently recorded y touch.
	 */
	private float newY;

	/**
	 * Coutner used for draw method calls.
	 */
	private int drawCounter;

	/**
	 * Counter to store touch method calls
	 */
	public int touchCounter;

	/**
	 * Square line should disperse during this time
	 */
	private boolean disperseSquares;

	String formattedDate = "";

	/**
	 * Create style and colors for Paint objects.
	 */
	public SquareSet() {
		rectPaint.setStyle(Style.STROKE);
		rectPaint.setColor(Color.BLUE);
		datePaint.setColor(Color.RED);
		datePaint.setTextSize(35);
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
		formattedDate = simpleDateFormat.format(date);

	}

	/**
	 * Draw method of the application will draw the background and squares here.
	 * 
	 * @param canvas
	 */
	public void draw(Canvas canvas) {
		drawCounter++;

		canvas.drawColor(screenColor);

		canvas.drawText("date: " + formattedDate, 0, datePaint.getTextSize(), datePaint);
		canvas.drawText("squares: " + getRectFs().size(), 0,
				datePaint.getTextSize() * 2, datePaint);
		canvas.drawText("draws: " + drawCounter, 0,
				datePaint.getTextSize() * 3, datePaint);
		canvas.drawText("touches: " + touchCounter, 0,
				datePaint.getTextSize() * 4, datePaint);

		// draw rectFs in array
		drawRectFs(canvas);
	}

	/**
	 * On surface changed we need to record the screen sizes for scaling.
	 * 
	 * @param holder
	 *            surface holder
	 * @param format
	 *            type of screen
	 * @param width
	 * @param height
	 */
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		screenW = width;
		screenH = height;
	}

	private void drawRectFs(Canvas canvas) {
		// draw every rectF in list
		for (RectF rectF : getRectFs().toArray(new RectF[0])) {
			canvas.drawRect(rectF, rectPaint);
		}
	}

	private synchronized ArrayList<MyRectF> getRectFs() {
		return rectFs;
	}

	public void updatePhysics() {
		screenColor = Color.WHITE;
		date = new Date();
		if (createSquare) {
			createSquare = false;
			new RectFFactory().execute();
		} else if (clearSquares) {
			clearSquares = false;
			disperseSquares = false;
			getRectFs().clear();
		} else if (disperseSquares) {
			// disperse squares
			for (MyRectF rectF : getRectFs().toArray(new MyRectF[0])) {
				rectF.updatePoint();
			}
		}
	}

	public void restart() {
		screenColor = Color.BLUE;
		createSquare = false;
		clearSquares = false;
		drawCounter = 0;
		getRectFs().clear();
	}

	/**
	 * touch event is separate from game thread, could create objects from a
	 * flag set in on touch in stead of doing work here
	 * 
	 * @param event
	 */
	public boolean onTouchEvent(MotionEvent event) {
		newX = event.getX();
		newY = event.getY();
		touchCounter++;
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			clearSquares = true;
			touchingScreen = true;
			drawCounter = 0;
			touchCounter = 0;
			return true;
		case MotionEvent.ACTION_MOVE:
			createSquare = true;
			return true;
		case MotionEvent.ACTION_UP:
			touchingScreen = false;
			disperseSquares = true;
			return true;
		default:
			return false;
		}
	}

	class RectFFactory extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			// the new square should be a random generation pixel of the
			// distance of the shortest side
			int x1 = (int) newX;
			int x2 = (int) (screenW - newX);
			int y1 = (int) newY;
			int y2 = (int) (screenH - newY);

			// find smallest of the above four ints
			int diff = x1 < x2 ? x1 : x2;
			diff = diff < y1 ? diff : y1;
			diff = diff < y2 ? diff : y2;

			// get random percent 0.0 to 1.0
			double percent = Math.random();
			double res = diff * percent;
			int buffer = (int) (res * SQUARE_RATIO);
			int left = (int) (newX - buffer);
			int top = (int) (newY - buffer);
			int right = (int) (newX + buffer);
			int bottom = (int) (newY + buffer);

			if (touchingScreen)
				getRectFs().add(new MyRectF(left, top, right, bottom));
			return null;
		}
	}

	PlotPoints plot = new PlotPoints();

	class MyRectF extends RectF {
		Point[] pointLineA;
		Point tempA;
		int currentPointA;

		public MyRectF(float left, float top, float right, float bottom) {
			super(left, top, right, bottom);
			Point a = new Point((int) left, (int) top);
			// make the distance point a random number within the screen width
			// and height
			int xvalues = (int) (((float) screenW) * Math.random());
			int yvalues = (int) (((float) screenH) * Math.random());

			Point b = new Point(xvalues, yvalues);
			pointLineA = plot.plotLine(a, b);
		}

		public void updatePoint() {
			currentPointA++;
			if (currentPointA < pointLineA.length) {
				tempA = pointLineA[currentPointA];
				if (tempA.x > 0 && tempA.y > 0) {
					float xdiff = tempA.x + (right - left);
					float ydiff = tempA.y + (bottom - top);
					set(tempA.x, tempA.y, xdiff, ydiff);
				}
			}
		}
	}
}