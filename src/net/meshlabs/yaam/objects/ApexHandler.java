package net.meshlabs.yaam.objects;

import raft.glfont.android.AGLFont;
import net.meshlabs.yaam.GameState;
import net.meshlabs.yaam.GameWorld;
import net.meshlabs.yaam.R;

import android.graphics.Paint;
import android.graphics.Typeface;

import com.threed.jpct.FrameBuffer;
import com.threed.jpct.Interact2D;
import com.threed.jpct.Object3D;
import com.threed.jpct.Primitives;
import com.threed.jpct.RGBColor;
import com.threed.jpct.SimpleVector;

/**
 * Drop a labeled point at each apex.
 * @author matt
 *
 */
public class ApexHandler {
	private final static int NUM_TRACES = 20;
	private final static float DECAY_SECS = 5f;
	private final static float SIZE = 1f;
	
	private final static String TEXTURE = "ApexObjectTexture";
	private final Object3D[] traces = new Object3D[NUM_TRACES];
	private final AGLFont font;
	private final GameWorld world;
	private final Flasher flasher = new Flasher();
	private int nextTraceIndex = 0;
	
	public ApexHandler(GameWorld world) {
		this.world = world;
		world.reloadTextureResource(R.raw.trace, true, TEXTURE);
		//world.reloadTexture(TRACE_TEXTURE, new Texture(32, 32, new RGBColor(100, 192, 100)));
		
		for (int i=0; i<NUM_TRACES; i++) {
			traces[i] = initializeTrace();
			world.addObject(traces[i]);
		}
		
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		//paint.setTypeface(Typeface.create((String)null, Typeface.BOLD));
		paint.setTypeface(Typeface.DEFAULT_BOLD);
		paint.setTextSize(36);
		
		this.font = new AGLFont(paint);
	}
	
	public void addApex(SimpleVector point) {
		dropTrace(traces[nextTraceIndex], point);
		nextTraceIndex++;
		nextTraceIndex %= NUM_TRACES;
		
		int score = (int) -point.y*20;
		world.state.score += score;
		flasher.drop(point, score);
		
		if (world.state.maxHeight < -point.y) {
			world.state.maxHeight = -point.y;
		}
	}
	
	public void timeStep(float step) {
		flasher.timeStep(step);
	}
	
	public void drawTo(FrameBuffer fb) {
		flasher.draw(fb);
	}
	
	public void resetTraces() {
		for (int i=0; i<NUM_TRACES; i++) {
			traces[i].setVisibility(false);
		}
	}
	
	private void dropTrace(Object3D trace, SimpleVector point) {
		trace.clearTranslation();
		trace.translate(point);
		trace.setTransparency(180);
		trace.setVisibility(true);
	}
	
	private Object3D initializeTrace() {
		Object3D trace = Primitives.getPlane(1, SIZE);
		trace.setTexture(TEXTURE);
		trace.setBillboarding(true);
		trace.build();
		trace.strip();
		trace.setVisibility(false);
		//trace.setTransparency(180);
		return trace;
	}
	
	class Flasher {
		private static final float FADEOUT_SECS = 1.3f;
		SimpleVector position3D = new SimpleVector();
		SimpleVector position2D = new SimpleVector();
		private int transparency = 255;
		
		String score = "0";
		boolean active = false;
		
		public void timeStep(float stepMS) {
			if (!active) { return; }
			transparency -= (int) (stepMS*255/(FADEOUT_SECS*1000));
			
			if (transparency <=0 ) {
				reset();
			}
		}
		
		public void drop(SimpleVector pos3D, int score) {
			this.position3D.set(pos3D);
			this.score = Integer.toString(score);
			active = true;
		}
		
		public void reset() {
			active = false;
			transparency = 255;
		}
		
		public void draw(FrameBuffer fb) {
			if (!active) { return; }
			
			Interact2D.project3D2D(world.camera, fb, position3D, position2D);
			
			font.blitString(fb, score, (int)position2D.x-40, (int)position2D.y, transparency, RGBColor.WHITE);
		}
		
		
	}
}
