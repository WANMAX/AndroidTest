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
import android.view.ViewParent;
import android.widget.Toast;

/**
 * @author wan
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
	private int sx;
	private int sy;
	private int w;
	private int h;
	private int lx;
	private int ly;
	private int xb = 0;//移动缓存，进行不可以的移动时，保存过度的移动，反向移动时用来抵消。
	private int yb = 0;

	
	public StretchableListener(Context c, int startX, int startY, int screenWidth, int screenHeight) {
		this(c, startX, startY, screenWidth, screenHeight, ALL);
	}
	public StretchableListener(Context c, int startX, int startY, int screenWidth, int screenHeight, int mode) {
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

	private float top = 0;
	private float left = 0;
	private boolean firstTop = true;
	private boolean firstLeft = true;
	private int getX(View v, MotionEvent event) {
		if (firstLeft) {
			left = event.getRawX() - event.getX() - v.getLeft();
			firstLeft = false;
		}
		int lx = (int)(event.getRawX() - left);
		if (lx < sx) lx = sx;
		else if(lx > w) lx = w;
		return  lx;
	}
	private int getY(View v, MotionEvent event) {
		if (firstTop) {
			top = event.getRawY() - event.getY() - v.getTop();
			firstTop = false;
		}
		int ly = (int)(event.getRawY() - top);
		if (ly < sy) ly = sy;
		else if(ly > h) ly = h;
		return  ly;
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
			lx = getX(v, event);
			ly = getY(v, event);
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
			
			int cx = getX(v, event);
			int dx = cx - lx;
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
			int cy = getY(v, event);
			int dy = cy - ly;
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
			lx = getX(v, event);
			ly = getY(v, event);
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