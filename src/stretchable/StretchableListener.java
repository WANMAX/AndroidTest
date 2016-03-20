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
import android.widget.Toast;

/**
 * @author wan
 *
 */
public class StretchableListener implements OnTouchListener {
	public static final int TOP = 1;
	public static final int LEFT = 2;
	public static final int BOTTOM = 3;
	public static final int RIGHT = 4;
	public static final int VERTICAL = TOP|BOTTOM;
	public static final int HORIZONTAL = LEFT|RIGHT;
	public static final int ALL = VERTICAL|HORIZONTAL;
	
	private boolean topS;
	private boolean leftS;
	private boolean bottomS;
	private boolean rightS;
	private boolean onTop;
	private boolean onLeft;
	private boolean onBottom;
	private boolean onRight;
	private final Context c;
	private int w;
	private int h;
	private int sx;
	private int sy;
	private int lx;
	private int ly;
	private int xb = 0;//移动缓存，进行不可以的移动时，保存过度的移动，反向移动时用来抵消。
	private int yb = 0;

	public StretchableListener(Context c) {
		this(c, ALL);
	}
	
	public StretchableListener(Context c, int mode) {
		this(c, 0, 0, mode);
		DisplayMetrics dm = c.getResources().getDisplayMetrics();
		this.w = dm.widthPixels;
		this.h = dm.heightPixels;
	}

	public StretchableListener(Context c, int screenWidth, int screenHeight) {
		this(c, screenWidth, screenHeight, ALL);
	}
	
	public StretchableListener(Context c, int screenWidth, int screenHeight, int mode) {
		this(c, screenWidth, screenHeight, 0, 0, mode);
	}

	public StretchableListener(Context c, int screenWidth, int screenHeight, int startX, int startY, int mode) {
		this.c = c;
		w = screenWidth;
		h = screenHeight;
		sx = startX;
		sy = startY;
		if ((mode&TOP)!=0)
			topS = true;
		if ((mode&LEFT)!=0)
			leftS = true;
		if ((mode&BOTTOM)!=0)
			bottomS = true;
		if ((mode&RIGHT)!=0)
			rightS = true;
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
			int x = (int)event.getX();
			int y = (int)event.getY();
			if (y < v.getHeight() / 5)
				onTop = true;
			else if (y > v.getHeight() * 4 /5)
				onBottom = true;
			if (x < v.getWidth() / 5)
				onLeft = true;
			else if (x > v.getWidth() * 4 /5)
				onRight = true;
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
			if (xb > 0 && dx > 0) {
				if (xb > dx) {
					xb -= dx;
					dx = 0;
				}
				else {
					dx -= xb;
					xb = 0;
				}
			}
			else if (xb < 0 && dx < 0) {
				if (dx > xb) {
					xb -= dx;
					dx = 0;
				}
				else {
					dx -= xb;
					xb = 0;
				}
			}
			int dy = (int) event.getRawY() - ly;
			if (yb > 0 && dy > 0) {
				if (yb > dy) {
					yb -= dy;
					dy = 0;
				}
				else {
					dy -= yb;
					yb = 0;
				}
			}
			else if (yb < 0 && dy < 0) {
				if (dy > yb) {
					yb -= dy;
					dy = 0;
				}
				else {
					dy -= yb;
					yb = 0;
				}
			}
			
			int left = v.getLeft();
			int top = v.getTop();
			int right = v.getRight();
			int bottom = v.getBottom();
			int minH = (int)v.getMinimumHeight();
			int minW = (int)v.getMinimumWidth();

			boolean verticalS = true;
			boolean horizontalS = true;
			int temp;
			if (topS && onTop) {
				top +=  dy;
				temp = top;
				if (bottom - top < minH) {
					top = bottom - minH;
					yb += top - temp;
				}
			}
			else if (bottomS && onBottom) {
				bottom +=  dy;
				temp = bottom;
				if (bottom - top < minH) {
					bottom = top + minH;
					yb += bottom - temp;
				}
			}
			else
				verticalS = false;
			if (leftS && onLeft) {
				left +=  dx;
				temp = left;
				if (right - left < minW) {
					left = right - minW;
					xb += left - temp;
				}
			}
			else if (rightS && onRight) {
				right +=  dx;
				temp = right;
				if (right - left < minW) {
					right = left + minW;
					xb += right - temp;
				}
			}
			else
				horizontalS = false;
			if (!verticalS && !horizontalS) {
				top +=  dy;
				bottom +=  dy;
				left +=  dx;
				right +=  dx;
			}
			
			int width = right - left;
			if (left < sx) {
				left = sx;
				right = left + width;
			} else if (right > w) {
				right = w;
				left = right - width;
			}
			int height = bottom - top;
			if (top < sy) {
				top = sy;
				bottom = top + height;
			} else if (bottom > h) {
				bottom = h;
				top = bottom - height;
			}
			v.layout(left, top, right, bottom);
			lx = (int) event.getRawX();
			ly = (int) event.getRawY();
			break;
		case MotionEvent.ACTION_UP:
			if (dbclick)
				onClick(v, event);
			onTop = false;
			onLeft = false;
			onBottom = false;
			onRight = false;
			xb = 0;
			yb = 0;
			continue_ = false;
			break;
		}
		return true;
	}
}