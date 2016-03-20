/**
 * 
 */
package stretchable;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

/**
 * @author wan
 *
 */
public class StretchableListener implements OnTouchListener {
	private int w;
	private int h;
	private int sx;
	private int sy;
	private int lx;
	private int ly;

	public StretchableListener(Context c) {
		this(0, 0, 0, 0);
		DisplayMetrics dm = c.getResources().getDisplayMetrics();
		this.w = dm.widthPixels;
		this.h = dm.heightPixels;
	}

	public StretchableListener(int screenWidth, int screenHeight) {
		this(screenWidth, screenHeight, 0, 0);
	}

	public StretchableListener(int screenWidth, int screenHeight, int startX, int startY) {
		w = screenWidth;
		h = screenHeight;
		sx = startX;
		sy = startY;
	}

	private Timer timer = new Timer();
	private boolean first = true;
	private boolean continue_ = false;
	private Timer timer2 = new Timer();
	private boolean dbclick = false;

	public void onClick(View v, MotionEvent event) {}
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		int action = event.getAction();
		if (action == MotionEvent.ACTION_DOWN) {
			if (first) {
				first = false;
				timer.schedule(new TimerTask() {
					@Override
					public void run() {
						first = true;
					}
				}, 250);
			} else
				continue_ = true;
		}
		if (!continue_)
			return false;
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			lx = (int) event.getRawX();
			ly = (int) event.getRawY();
			dbclick = true;
			timer2.schedule(new TimerTask() {
				@Override
				public void run() {
					dbclick = false;
				}
			}, 250);
			break;
		case MotionEvent.ACTION_MOVE:
			int dx = (int) event.getRawX() - lx;
			int dy = (int) event.getRawY() - ly;

			int left = v.getLeft() + dx;
			int top = v.getTop() + dy;
			int right = v.getRight() + dx;
			int bottom = v.getBottom() + dy;
			if (left < sx) {
				left = sx;
				right = left + v.getWidth();
			} else if (right > w) {
				right = w;
				left = right - v.getWidth();
			}
			if (top < sy) {
				top = sy;
				bottom = top + v.getHeight();
			} else if (bottom > h) {
				bottom = h;
				top = bottom - v.getHeight();
			}
			v.layout(left, top, right, bottom);
			lx = (int) event.getRawX();
			ly = (int) event.getRawY();
			break;
		case MotionEvent.ACTION_UP:
			if (dbclick)
				onClick(v, event);
			continue_ = false;
			break;
		}
		return true;
	}
}