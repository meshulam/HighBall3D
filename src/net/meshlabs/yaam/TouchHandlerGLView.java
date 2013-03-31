package net.meshlabs.yaam;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.FloatMath;
import android.util.Log;
import android.view.MotionEvent;

public class TouchHandlerGLView extends GLSurfaceView {
	
	//private final GLSurfaceView.Renderer renderer;
	private GameWorld world;
	
	public TouchHandlerGLView (Context context) {
		super(context);
		this.setKeepScreenOn(true);
	}
	
	protected void setWorld(GameWorld w) {
		world = w;
		setRenderer(world.renderer);
	}
	
	public boolean onTouchEvent(MotionEvent e) {
		int pointerCount = e.getPointerCount();
		
		if (pointerCount == 1) {  // 1 finger, apply a force
			if (e.getActionMasked() == MotionEvent.ACTION_MOVE) {
				handle1Pointer(e);
			} else {
				handle1PointerInit(e);
			}

		} else if (pointerCount == 2) { // 2 fingers, move camera
			if (e.getActionMasked() == MotionEvent.ACTION_MOVE) {
				handle2Pointer(e);
			} else {
				handle2PointerInit(e);	// TODO: don't init on up events I guess
			}
		} 

		return true;
	}
	
	/* Stuff for 1 pointer events (applying force) */
	private float lastX = 0;
	private float lastY = 0;
	
	private void handle1Pointer(MotionEvent e) {
		
		float dX = e.getX() - lastX;
		float dY = e.getY() - lastY;
		
		float angle = (float) Math.atan2(dY, dX);
		float magnitude = FloatMath.sqrt(dX*dX + dY*dY);
		
		queueEvent(new Force(world, angle, magnitude ));
		lastX = e.getX();
		lastY = e.getY();
	}
	
	private void handle1PointerInit(MotionEvent e) {
		lastX = e.getX();
		lastY = e.getY();
		
		queueEvent(new Force(world, 0, 0));  // why not?
	}
	
	/* Stuff specific to 2-pointer movement (camera move) */
	private int p0id;
	private int p1id;
	
	private float lastDistance2 =  0;
	private float lastAngle2 = 0;
	
	private final static float CAMERA_MOVE_MULTIPLIER = 500;
	private void handle2Pointer(MotionEvent e) {
		int p0 = e.findPointerIndex(p0id);
		int p1 = e.findPointerIndex(p1id);
		
		float angle = (float) Math.atan2(e.getY(p0)-e.getY(p1), e.getX(p0)-e.getX(p1));
		float distance = FloatMath.sqrt((e.getX(0)-e.getX(1))*(e.getX(0)-e.getX(1)) + 
							(e.getY(0)-e.getY(1))*(e.getY(0)-e.getY(1)) ); 
		
		queueEvent(new CameraMove(world, angle-lastAngle2, 
					(lastDistance2-distance)*CAMERA_MOVE_MULTIPLIER/(getHeight()*getWidth())));
		
		lastDistance2 = distance;
		lastAngle2 = angle;
	}
	
	private void handle2PointerInit(MotionEvent e) {
		p0id = e.getPointerId(0);
		p1id = e.getPointerId(1);
		
		lastAngle2 = (float) Math.atan2(e.getY(0)-e.getY(1), e.getX(0)-e.getX(1));
		lastDistance2 = FloatMath.sqrt((e.getX(0)-e.getX(1))*(e.getX(0)-e.getX(1)) + 
						(e.getY(0)-e.getY(1))*(e.getY(0)-e.getY(1))); 
		
		queueEvent(new CameraMove(world, 0, 0));
	}

	private void printEventDebug(MotionEvent e) {
		String msg = "";
		switch (e.getActionMasked()) {
		case MotionEvent.ACTION_DOWN:
			msg += "ACTION_DOWN on ind"+e.getActionIndex();
			break;
		case MotionEvent.ACTION_POINTER_DOWN:
			msg += "ACTION_PTR_DOWN on ind"+e.getActionIndex();
			break;
		case MotionEvent.ACTION_MOVE:
			msg += "ACTION_MOVE hist size="+e.getHistorySize();
			break;
		case MotionEvent.ACTION_UP:
			msg += "ACTION_UP on ind"+e.getActionIndex();
			break;
		case MotionEvent.ACTION_POINTER_UP:
			msg += "ACTION_PTR_UP on ind"+e.getActionIndex();
			break;
		}
		
		msg += ", "+e.getPointerCount()+"ptrs";
		Log.i("TouchHandler", msg);
		
		for (int i=0; i< e.getPointerCount(); i++) {
			String s = " p"+i+" x="+e.getX(i)+" y="+e.getY(i)+" pID="+e.getPointerId(i);
			Log.i("TouchHandler", s);
		}
		
	}
	
	class Force implements Runnable{
		final float x;
		final float y;
		final GameWorld world;
		
		Force(GameWorld w, float x, float y) {
			this.x = x;
			this.y = y;
			this.world = w;
			
			//Log.d("TouchHandler", "Sending Force x="+x+" y="+y);
		}
		
		public void run() {
			world.applyForce(x, y);
		}
	}
	
	class CameraMove implements Runnable {
		final float dYaw;
		final float distance;
		final GameWorld world;
		
		CameraMove(GameWorld w, float dYaw, float dDistance) {
			this.dYaw = dYaw;
			this.distance = dDistance;
			this.world = w;
			
			//Log.d("TouchHandler", "Sending CameraMove dYaw="+dYaw+" dDist="+dDistance);
		}
		
		public void run() {
			world.moveCamera(dYaw, distance);
		}
	}

}
