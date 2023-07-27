package com.touchmove.kotlin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

/**
 * Fragment class to hold our SurfaceView, the SurfaceThread and the SquareSet
 * to be drawn and manipulated.
 * 
 * @author Rick
 *
 */
public class SurfaceFragment extends Fragment {

	/**
	 * Our drawing surface object.
	 */
	DrawingSurface drawingSurface;

	/**
	 * Allow for options handled by this fragment in the menu based on this
	 * fragment.
	 */
	public SurfaceFragment() {

	}

	/**
	 * After the layout is inflated we can reference the surfaceView and
	 * messageTextView. Create an animation and track with a boolean so the user
	 * has a way of knowing when the application appears to be ready and the
	 * application knows when it should accept touch events.
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_main, container, false);

		Toolbar toolbar = view.findViewById(R.id.toolbar);
		toolbar.setTitle("Touch Move Kotlin");
		toolbar.inflateMenu(R.menu.menu_draw);
		toolbar.setOnMenuItemClickListener(this::onMenuItemSelected);

		drawingSurface = (DrawingSurface) view
				.findViewById(R.id.drawingSurface);

		drawingSurface.messageTextView = (TextView) view
				.findViewById(R.id.messageTextView);

		drawingSurface.restart();

		Animation anim = AnimationUtils.loadAnimation(view.getContext(),
				R.anim.intro);

		anim.reset();
		anim.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				drawingSurface.introAnimationFinished = false;
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				drawingSurface.introAnimationFinished = true;
			}
		});

		view.clearAnimation();
		view.startAnimation(anim);

		return view;
	}

	public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
		if (menuItem.getItemId() == R.id.action_restart) {
			drawingSurface.restart();
			return true;
		}
		return false;
	}
}