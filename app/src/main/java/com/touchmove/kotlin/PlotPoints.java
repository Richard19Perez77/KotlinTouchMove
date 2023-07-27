package com.touchmove.kotlin;

import android.graphics.Point;

public class PlotPoints {

	/**
	 * This class is used to get an array of Point objects that are the line
	 * from a to b;
	 * 
	 */
	Point a, b, point;
	Point[] pointArr;

	/**
	 * Begins plotting points on a line between two points.
	 * 
	 * @param pointA
	 *            start point
	 * @param pointB
	 *            end point
	 * @return list of points between them in a line
	 */
	public Point[] plotLine(Point pointA, Point pointB) {
		a = pointA;
		b = pointB;

		PlotPointsToLocation();
		return pointArr;
	}

	/**
	 * Check for up or down movement, if not straight left or right.
	 */
	public void PlotPointsToLocation() {
		// move object up wards
		if (a.y > b.y) {
			if (a.x < b.x) {
				plotPointsUpRight();
			} else if (a.x > b.x) {
				plotPointsUpLeft();
			} else {
				plotPointsUp();
			}
			// move objects downwards
		} else if (a.y < b.y) {
			if (a.x < b.x) {
				plotPointsDownRight();
			} else if (a.x > b.x) {
				plotPointsDownLeft();
			} else {
				plotPointsDown();
			}
			// move left or right
		} else if (a.x > b.x) {
			plotPointsLeft();
		} else {
			plotPointsRight();
		}
	}

	/**
	 * Keep the y value constant and increment the x value
	 */
	private void plotPointsRight() {
		double horizontalPixels = Math.abs(a.x - b.x);
		pointArr = new Point[(int) horizontalPixels];

		// create new points in array with y values filled
		for (int i = 0; i < horizontalPixels; i++) {
			point = new Point();
			point.x = a.x + i + 1;
			point.y = a.y;
			pointArr[i] = point;
		}
	}

	/**
	 * Keep the y value constant and decrement the x value
	 */
	private void plotPointsLeft() {
		double horizontalPixels = Math.abs(a.x - b.x);
		pointArr = new Point[(int) horizontalPixels];

		// create new points in array with y values filled
		for (int i = 0; i < horizontalPixels; i++) {
			point = new Point();
			point.x = a.x - i - 1;
			point.y = a.y;
			pointArr[i] = point;
		}
	}

	/**
	 * 
	 */
	private void plotPointsUp() {
		int tempY = a.y;
		int tempX = a.x;
		// given points to the top create the list of points to traverse
		double verticalPixels = Math.abs(a.y - b.y);
		pointArr = new Point[(int) verticalPixels];
		for (int i = 0; i < verticalPixels; i++) {
			point = new Point();
			// every step move up a point
			tempY--;
			point.x = tempX;
			point.y = tempY;
			// add point to list
			pointArr[i] = point;
		}
	}

	private void plotPointsUpRight() {
		double verticalPixels = Math.abs(a.y - b.y);
		double horizontalPixels = Math.abs(a.x - b.x);

		if (horizontalPixels == verticalPixels) {
			pointArr = new Point[(int) horizontalPixels];
			for (int i = 0; i < horizontalPixels; i++) {
				point = new Point();
				pointArr[i] = point;
				pointArr[i].x = a.x + i + 1;
				pointArr[i].y = a.y - i - 1;
			}
		} else if (horizontalPixels >= verticalPixels) {
			// more horizontal moves
			pointArr = new Point[(int) horizontalPixels];
			// create new points in array with x values filled
			for (int i = 0; i < horizontalPixels; i++) {
				point = new Point();
				pointArr[i] = point;
				pointArr[i].x = a.x + i + 1;
			}

			// set first and last y
			pointArr[0].y = a.y;
			pointArr[pointArr.length - 1].y = b.y;

			// start filling in missing values
			int startValue = pointArr[0].y;
			double distance = Math.abs(a.y - b.y);

			float distancePerMove = (float) distance
					/ (float) (pointArr.length - 1);

			float acc = startValue;
			int i = 1;
			while (i < (pointArr.length - 1)) {
				acc -= distancePerMove;
				pointArr[i].y = Math.round(acc);
				i++;
			}
		} else {

			pointArr = new Point[(int) verticalPixels];

			// create new points in array with y values filled
			for (int i = 0; i < verticalPixels; i++) {
				point = new Point();
				pointArr[i] = point;
				pointArr[i].y = a.y - i - 1;
			}

			// set first and last x
			pointArr[0].x = a.x;
			pointArr[pointArr.length - 1].x = b.x;

			// start filling in missing values
			int startValue = pointArr[0].x;
			double distance = Math.abs(a.x - b.x);

			float distancePerMove = (float) distance
					/ (float) (pointArr.length - 1);

			float acc = startValue;
			int i = 1;
			while (i < (pointArr.length - 1)) {
				acc += distancePerMove;
				pointArr[i].x = Math.round(acc);
				i++;
			}
		}
	}

	private void plotPointsUpLeft() {
		double verticalPixels = Math.abs(a.y - b.y);
		double horizontalPixels = Math.abs(a.x - b.x);

		if (horizontalPixels == verticalPixels) {
			pointArr = new Point[(int) horizontalPixels];
			for (int i = 0; i < horizontalPixels; i++) {
				point = new Point();
				pointArr[i] = point;
				pointArr[i].x = a.x - i - 1;
				pointArr[i].y = a.y - i - 1;
			}
		} else if (horizontalPixels >= verticalPixels) {
			// more horizontal moves
			pointArr = new Point[(int) horizontalPixels];
			// create new points in array with x values filled
			for (int i = 0; i < horizontalPixels; i++) {
				point = new Point();
				pointArr[i] = point;
				pointArr[i].x = a.x - i - 1;
			}

			// set first and last y
			pointArr[0].y = a.y;
			pointArr[pointArr.length - 1].y = b.y;

			// start filling in missing values
			int startValue = pointArr[0].y;
			double distance = Math.abs(a.y - b.y);

			float distancePerMove = (float) distance
					/ (float) (pointArr.length - 1);

			float acc = startValue;
			int i = 1;
			while (i < (pointArr.length - 1)) {
				acc -= distancePerMove;
				pointArr[i].y = Math.round(acc);
				i++;
			}

		} else {

			pointArr = new Point[(int) verticalPixels];

			// create new points in array with y values filled
			for (int i = 0; i < verticalPixels; i++) {
				point = new Point();
				pointArr[i] = point;
				pointArr[i].y = a.y - 1 - i;
			}

			// set first and last x
			pointArr[0].x = a.x;
			pointArr[pointArr.length - 1].x = b.x;

			// start filling in missing values
			int startValue = pointArr[0].x;
			double distance = Math.abs(a.x - b.x);

			float distancePerMove = (float) distance
					/ (float) (pointArr.length - 1);

			float acc = startValue;
			int i = 1;
			while (i < (pointArr.length - 1)) {
				acc -= distancePerMove;
				pointArr[i].x = Math.round(acc);
				i++;
			}
		}
	}

	private void plotPointsDown() {
		int tempY = a.y;
		int tempX = a.x;
		// given points to the top create the list of points to traverse
		double verticalPixels = Math.abs(b.y - a.y);
		pointArr = new Point[(int) verticalPixels];
		for (int i = 0; i < verticalPixels; i++) {
			point = new Point();
			// every step move up a point
			tempY++;
			point.x = tempX;
			point.y = tempY;
			// add point to list
			pointArr[i] = point;
		}
	}

	private void plotPointsDownRight() {

		double verticalPixels = Math.abs(a.y - b.y);
		double horizontalPixels = Math.abs(a.x - b.x);

		if (horizontalPixels == verticalPixels) {
			pointArr = new Point[(int) horizontalPixels];
			for (int i = 0; i < horizontalPixels; i++) {
				point = new Point();
				pointArr[i] = point;
				pointArr[i].x = a.x + i + 1;
				pointArr[i].y = a.y + i + 1;
			}
		} else if (horizontalPixels >= verticalPixels) {
			pointArr = new Point[(int) horizontalPixels];
			// create new points in array with y values filled
			for (int i = 0; i < horizontalPixels; i++) {
				point = new Point();
				pointArr[i] = point;
				pointArr[i].x = a.x + i + 1;
			}

			// set first and last y
			pointArr[0].y = a.y;
			pointArr[pointArr.length - 1].y = b.y;

			// start filling in missing values
			int startValue = pointArr[0].y;
			int endValue = pointArr[pointArr.length - 1].y;
			int distance = Math.abs(endValue - startValue);
			float distancePerMove = (float) distance
					/ (float) (pointArr.length - 1);

			float acc = startValue;
			int i = 1;
			while (i < (pointArr.length - 1)) {
				acc += distancePerMove;
				pointArr[i].y = Math.round(acc);
				i++;
			}
		} else {

			pointArr = new Point[(int) verticalPixels];

			// create new points in array with y values filled
			for (int i = 0; i < verticalPixels; i++) {
				point = new Point();
				pointArr[i] = point;
				pointArr[i].y = a.y + i + 1;
			}

			// set first and last x
			pointArr[0].x = a.x;
			pointArr[pointArr.length - 1].x = b.x;
			// start filling in missing values
			int startValue = pointArr[0].x;
			int endValue = pointArr[pointArr.length - 1].x;
			int distance = Math.abs(endValue - startValue);
			float distancePerMove = (float) distance
					/ (float) (pointArr.length - 1);
			float acc = startValue;
			int i = 1;
			while (i < (pointArr.length - 1)) {
				acc += distancePerMove;
				pointArr[i].x = Math.round(acc);
				i++;
			}
		}
	}

	private void plotPointsDownLeft() {

		double verticalPixels = Math.abs(a.y - b.y);
		double horizontalPixels = Math.abs(a.x - b.x);

		if (horizontalPixels == verticalPixels) {
			pointArr = new Point[(int) horizontalPixels];
			for (int i = 0; i < horizontalPixels; i++) {
				point = new Point();
				pointArr[i] = point;
				pointArr[i].x = a.x - i - 1;
				pointArr[i].y = a.y + i + 1;
			}
		} else if (horizontalPixels >= verticalPixels) {
			pointArr = new Point[(int) horizontalPixels];
			// create new points in array with y values filled
			for (int i = 0; i < horizontalPixels; i++) {
				point = new Point();
				pointArr[i] = point;
				pointArr[i].x = a.x - i - 1;
			}

			// set first and last y
			pointArr[0].y = a.y;
			pointArr[pointArr.length - 1].y = b.y;

			// start filling in missing values
			int startValue = pointArr[0].y;
			int endValue = pointArr[pointArr.length - 1].y;
			int distance = Math.abs(endValue - startValue);
			float distancePerMove = (float) distance
					/ (float) (pointArr.length - 1);
			float acc = startValue;
			int i = 1;
			while (i < (pointArr.length - 1)) {
				acc += distancePerMove;
				pointArr[i].y = Math.round(acc);
				i++;
			}
		} else {

			pointArr = new Point[(int) verticalPixels];

			// create new points in array with y values filled
			for (int i = 0; i < verticalPixels; i++) {
				point = new Point();
				pointArr[i] = point;
				pointArr[i].y = a.y + i + 1;
			}

			// set first and last x
			pointArr[0].x = a.x;
			pointArr[pointArr.length - 1].x = b.x;
			// start filling in missing values
			int startValue = pointArr[0].x;
			int endValue = pointArr[pointArr.length - 1].x;
			int distance = Math.abs(endValue - startValue);
			float distancePerMove = (float) distance
					/ (float) (pointArr.length - 1);
			float acc = startValue;
			int i = 1;
			while (i < (pointArr.length - 1)) {
				acc -= distancePerMove;
				pointArr[i].x = Math.round(acc);
				i++;
			}
		}
	}
}