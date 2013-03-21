package net.meshlabs.yaam;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;

public class DemoActivity extends Activity {
	 
	private TouchHandlerGLView mGLView;
	//private RendererImpl renderer;
	private GameWorld gameWorld;

    @Override
    public void onCreate(Bundle savedInstanceState) {
    	Log.i("Activity", "onCreate");
        super.onCreate(savedInstanceState);
        
        
        mGLView = new TouchHandlerGLView(getApplication());
        // Uncomment here and change the framebuffer call to use ogl2
        // mGLView.setEGLContextClientVersion(2);
        
        gameWorld = new GameWorld(this);
        //mGLView.setRenderer(gameWorld.renderer);
        mGLView.setWorld(gameWorld);
        setContentView(mGLView);
    }
    
	@Override
	protected void onPause() {
		Log.i("Activity", "onPause");
		super.onPause();
		mGLView.onPause();
	}

	@Override
	protected void onResume() {
		Log.i("Activity", "onResume");
		super.onResume();
		Log.i("Activity", "did super.resume");
		mGLView.onResume();
		Log.i("Activity", "did glview.resume");
	}

	@Override
	protected void onStop() {
		Log.i("Activity", "onStop");
		super.onStop();
	}
	
	@Override
	public void onConfigurationChanged(Configuration c) {
		Log.i("Activity", "onConfigurationChanged:"+c.toString());
	}
	
	private float mPreviousX = 0;
	private float mPreviousY = 0;

}
