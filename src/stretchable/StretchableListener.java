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
	private Context c;
	private View v;
	private int w;
	private int h;
	private int sx;
	private int sy;
	private int lx;
	private int ly;
	public StretchableListener(Context c, View v) {
		this(c, v, 0, 0, 0, 0);
		DisplayMetrics dm = c.getResources().getDisplayMetrics();  
		this.w = dm.widthPixels;  
		this.h = dm.heightPixels;
	}
	public StretchableListener(Context c, View v, int screenWidth, int screenHeight) {
		this(c, v, screenWidth, screenHeight, 0, 0);
	}
	public StretchableListener(Context c, View v, int screenWidth, int screenHeight, int startX, int startY) {
		this.c = c;
		this.v = v;
		w = screenWidth;
		h = screenHeight;
		sx = startX;
		sy = startY;
	}
	private Timer timer = new Timer();
	private boolean first = true;
	private boolean continue_ = false;
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		int action=event.getAction();  
		if (action == MotionEvent.ACTION_DOWN) {
			if (first) {
				first = false;
				timer.schedule(new TimerTask() {
					@Override
					public void run() {
						first = true;
					}
				}, 250);
			}
			else 
				continue_ = true;
		}
		if (!continue_) return false;
        switch(action){  
        case MotionEvent.ACTION_DOWN:  
            lx = (int) event.getRawX();  
            ly = (int) event.getRawY();
            break;  
        case MotionEvent.ACTION_MOVE:  
            int dx =(int)event.getRawX() - lx;  
            int dy =(int)event.getRawY() - ly;  
          
            int left = v.getLeft() + dx;  
            int top = v.getTop() + dy;  
            int right = v.getRight() + dx;  
            int bottom = v.getBottom() + dy;                      
            if(left < 0){  
                left = 0;  
                right = v.getWidth();  
            }                     
            else if(right > w){  
                right = w;  
                left = right - v.getWidth();  
            }                     
            if(top < 0){  
                top = 0;  
                bottom = v.getHeight();  
            }
            else if(bottom > h){  
                bottom = h;  
                top = bottom - v.getHeight();  
            }                     
            v.layout(left, top, right, bottom);  
            lx = (int) event.getRawX();  
            ly = (int) event.getRawY();                    
            break;  
        case MotionEvent.ACTION_UP:
        	continue_ = false;
            break;                
        }
		return false;
	}
}