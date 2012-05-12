package project.alwaystry;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

public class AvatarBDConnectingPointAndroid {
	private boolean in;
	private boolean out;
	private int x;
	private int y;
	private int width = 10;
	private int height = 10;
	private double w ;
	private double h ;
	private View panel;
	private AvatarBDBlockAndroid container;
	private Paint cpPaint;
	
	public AvatarBDConnectingPointAndroid(int _x, int _y, boolean _in, boolean _out, double _w, double _h,AvatarBDBlockAndroid _container,View _panel){
		x = _x;
		y = _y;
		in = _in;
		out = _out;
		w = _w;
		h = _h;
		container = _container;
		panel = _panel;
		
		cpPaint = new Paint();
		cpPaint.setColor(Color.RED);
		cpPaint.setAntiAlias(true);
	}
	
	public int getX(){
		return x +container.getX()+(int)(container.getWidth()*w);
	}
	
	public int getY(){
		return y +container.getY()+(int)(container.getHeight()*h);
	}
	
	public int getWidth(){
		return width;
	}
	
	public int getHeight(){
		return height;
	}
	public boolean isOnMe(int x1, int y1){
		if ((x1 >= getX()-width/2) && ((getX() + width/2) >= x1) && (y1 >= getY()-height/2) && ((getY() + height/2) >= y1)) {
            return true;
       }
        return false;
	}
	
	protected void internalDrawing(Canvas canvas){
		canvas.drawRect(getX()-width/2, getY()-height/2, getX() + width/2, getY() + height/2, cpPaint);
	}
}
