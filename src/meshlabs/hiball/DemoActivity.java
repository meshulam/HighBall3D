package meshlabs.hiball;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.GoogleAnalytics;
import com.google.analytics.tracking.android.Tracker;

/**
 * Main (only) Android activity.
 *
 */
public class DemoActivity extends Activity {
	public final static String TAG = "Activity";
	 
	private TouchHandlerGLView mGLView;
	//private RendererImpl renderer;
	private GameWorld gameWorld;

    @Override
    public void onCreate(Bundle savedInstanceState) {
    	Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        
        mGLView = new TouchHandlerGLView(getApplication());
        
        gameWorld = new GameWorld(this);
        //mGLView.setRenderer(gameWorld.renderer);
        mGLView.setWorld(gameWorld);
        setContentView(mGLView);
    }
    
    @Override
    protected void onStart() {
    	Log.i(TAG, "onStart");
    	super.onStart();
    	EasyTracker.getInstance().activityStart(this);	// Google analytics
    }
    
	@Override
	protected void onPause() {
		Log.i("Activity", "onPause");
		super.onPause();
		mGLView.onPause();
	}

	@Override
	protected void onResume() {
		Log.i(TAG, "onResume");
		super.onResume();
		mGLView.onResume();
		gameWorld.onResume();
	}

	@Override
	protected void onStop() {
		Log.i(TAG, "onStop");
		super.onStop();
		Tracker tracker = GoogleAnalytics.getInstance(this).getDefaultTracker();
		tracker.sendEvent("Gameplay", "Level1", "Score", (long) gameWorld.state.score);
		tracker.sendEvent("Gameplay", "Level1", "MaxHeight", (long) gameWorld.state.maxHeightScore);
		
		EasyTracker.getInstance().activityStop(this); // Google Analytics
	}
	
	@Override
	protected void onDestroy() {
		Log.i(TAG, "onDestroy");
		super.onDestroy();
		gameWorld.onDestroy();
	}
	
	@Override
	public void onConfigurationChanged(Configuration c) {
		Log.i(TAG, "onConfigurationChanged:"+c.toString());
	}
}
