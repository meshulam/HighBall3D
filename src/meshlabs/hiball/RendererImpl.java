package meshlabs.hiball;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import meshlabs.hiball.utils.Averager;
import meshlabs.hiball.utils.TimeSmoother;
import android.opengl.GLSurfaceView;
import android.util.Log;

import com.threed.jpct.FrameBuffer;
import com.threed.jpct.RGBColor;

/**
 * Thin class which determines the (smoothed) time since last game loop
 * and then steps the game forward and re-renders everything.
 *
 */
public class RendererImpl implements GLSurfaceView.Renderer {
	
	public Averager timeSmoother;
	long lastDrawTime = 0;
	
	private long lastTime = 0;
	private FrameBuffer fb = null;
	private final GameWorld gameWorld;
	private RGBColor backColor = new RGBColor(50, 50, 50);
	
	private int fps = 30;
	public int lastFps = fps;
	
	public RendererImpl(GameWorld game) {
		super();
		Log.i("Renderer", "Created renderer");
		this.gameWorld = game;
		
		timeSmoother = new TimeSmoother(0.1f, 40);
		timeSmoother.initialize(17.5f);
	}
	
	// The main thing that gets called
	@Override
	public void onDrawFrame(GL10 gl) {
		long newTime = System.currentTimeMillis();
		if (lastDrawTime > 0) {
			timeSmoother.addValue(newTime-lastDrawTime);
		} 
		lastDrawTime = newTime;
		
		gameWorld.updateGame(timeSmoother.getAverage());
		
		fb.clear(backColor);
		gameWorld.renderAndDraw(fb);
		fb.display();
		
		// Uncomment stuff below to count FPS for logging or HUD printing
//		if (newTime - lastTime >=1000) {
//			gameWorld.state.fps = fps;
//			fps = 0;
//			lastTime = newTime;
//		}
//		fps++;
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		Log.i("Renderer", "onSurfaceChanged");
		if (fb != null) {
			fb.dispose();
		}
		fb = new FrameBuffer(gl, width, height);
		gameWorld.resyncRenderer();
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		Log.i("Renderer", "onSurfaceCreated");
		lastDrawTime = 0;
	}
	
	protected FrameBuffer getFrameBuffer() {
		return fb;
	}

}
