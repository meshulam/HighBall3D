package net.meshlabs.yaam;

import net.meshlabs.yaam.utils.IntBlitter;
import raft.glfont.android.AGLFont;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.Log;

import com.threed.jpct.FrameBuffer;
import com.threed.jpct.Interact2D;
import com.threed.jpct.Object3D;
import com.threed.jpct.Primitives;
import com.threed.jpct.RGBColor;
import com.threed.jpct.SimpleVector;

public class ScoringHandler {
	public final static String TAG = "Scorer";
	private final static float MIN_SCORE_HEIGHT = -1.5f;
	private final static String TEXTURE = "ApexObjectTexture";
	private final IntBlitter realTimeBlitter;
	
	
	private final GameWorld world;
	private final Flasher flasher;
	private final SimpleVector lastPoint = new SimpleVector();
	public boolean ascending = false;
	public boolean inFlight = false;
	
	
	public ScoringHandler(GameWorld world) {
		this.world = world;
		
		flasher = new Flasher();
		realTimeBlitter = new IntBlitter(32);
	}
	
	public void timeStep(float ms) {
		flasher.timeStep(ms);
		SimpleVector position = world.state.marblePosition;
		if (position.y > MIN_SCORE_HEIGHT) {
			return;
		}
		
		int score = scoreForHeight(lastPoint.y);
		if (score > world.state.maxHeightScore) {
			world.state.maxHeightScore = score;
		}
		
		if (ascending && 
				position.y > lastPoint.y) {	// just hit the peak
			ascending = false;
			inFlight = true;
			handlePeak(scoreForHeight(lastPoint.y));
		} else if (position.y < lastPoint.y) {	
			ascending = true;
			
		}
		
		lastPoint.set(position);
	}
	
	private static int scoreForHeight(float height) {
		return (int)(-height*4) * 5;
	}
	
	private void handlePeak(int score) {
		flasher.drop(lastPoint, score);
		world.state.score += score;
	}
	
	
	/**
	 * Score the landing of a jump. 
	 * @param quality 0-1f where 1 is tangential to the normal and 0 is aligned (brick!)
	 */
	public void land(float quality) {
		if (!inFlight) { return; }
		inFlight = false;
		
		if (flasher.score < 100) { return; }
		int score = (int) (quality*100);
		flasher.drop(world.state.marblePosition, score);
	}
	
	public void draw(FrameBuffer fb) {
		flasher.draw(fb);
		
		if (ascending && lastPoint.y < 0) {
			realTimeBlitter.blit(fb, scoreForHeight(lastPoint.y), (fb.getWidth()/2), fb.getHeight()/2, IntBlitter.ALIGN_CENTER, 210, RGBColor.WHITE);
		}
	}
	
	class Flasher {
		private static final float FADEOUT_SECS = 1.3f;
		private Object3D outline;
		private final IntBlitter blitter;
		SimpleVector position3D = new SimpleVector();
		SimpleVector position2D = new SimpleVector();
		private int transparency = 255;
		
		int score = 0;
		boolean active = false;
		
		Flasher() {
			world.reloadTextureResource(R.raw.trace, true, TEXTURE);
			
			Object3D trace = Primitives.getPlane(1, 1f);
			trace.setTexture(TEXTURE);
			trace.setBillboarding(true);
			trace.build();
			trace.strip();
			trace.setVisibility(false);
			this.outline = trace;
			
			this.blitter = new IntBlitter(36);
			
			world.addObject(outline);
		}
		
		public void timeStep(float stepMS) {
			if (!active) { return; }
			transparency -= (int) (stepMS*255/(FADEOUT_SECS*1000));
			outline.setTransparency(transparency);
			if (transparency <=0 ) {
				reset();
			}
		}
		
		public void drop(SimpleVector pos3D, int score) {
			reset();
			this.position3D.set(pos3D);
			this.score = score;
			
			outline.translate(position3D);
			active = true;
		}
		
		public void reset() {
			active = false;
			transparency = 255;
			outline.setVisibility(false);
			outline.clearTranslation();
			outline.setTransparency(transparency);
		}
		
		public void draw(FrameBuffer fb) {
			if (!active) { return; }
			
			Interact2D.project3D2D(world.camera, fb, position3D, position2D);
			
			blitter.blit(fb, score, (int)position2D.x, (int)position2D.y, IntBlitter.ALIGN_CENTER, transparency, RGBColor.WHITE);
		}
		
	}

}
