package net.meshlabs.yaam;

import net.meshlabs.yaam.utils.IntBlitter;
import raft.glfont.android.AGLFont;
import android.graphics.Paint;
import android.graphics.Typeface;

import com.threed.jpct.FrameBuffer;
import com.threed.jpct.Interact2D;
import com.threed.jpct.RGBColor;
import com.threed.jpct.SimpleVector;

public class ScoringHandler {
	public final static String TAG = "Scorer";
	private final static float MIN_SCORE_HEIGHT = -1.5f;
	private final static String TEXTURE = "ApexObjectTexture";
	private final IntBlitter realTimeBlitter;
	
	
	private final GameWorld world;
	private final ScoreFlasher scoreFlasher;
	private final StringFlasher stringFlasher;
	
	protected final SimpleVector lastPoint = new SimpleVector();
	public boolean ascending = false;
	public boolean inFlight = false;
	
	
	public ScoringHandler(GameWorld world) {
		this.world = world;
		
		scoreFlasher = new ScoreFlasher();
		stringFlasher = new StringFlasher();
		realTimeBlitter = new IntBlitter(32);
	}
	
	public void timeStep(float ms) {
		scoreFlasher.timeStep(ms);
		stringFlasher.timeStep(ms);
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
	
	private void reset() {
		ascending = false;
		inFlight = false;
		stringFlasher.reset();
	}
	
	private void handlePeak(int score) {
		scoreFlasher.drop(score);
		world.state.score += score;
	}
	
	/**
	 * Force JIT compilation and stuff like that.
	 */
	public void warmup() {
		for (int i = 1; i<10; i++) {
			int score = scoreForHeight(i*25);
			handlePeak(score);
			world.state.score -= score;
			realTimeBlitter.blit(null, scoreForHeight(i*25), 50, 50, IntBlitter.ALIGN_CENTER, 210, RGBColor.WHITE);
			reset();
		}
	}
	
	
	/**
	 * Score the landing of a jump. 
	 * @param quality 0-1f where 1 is tangential to the normal and 0 is aligned (brick!)
	 */
	public void land(float quality) {
		if (!inFlight) { return; }
		inFlight = false;
		
		if (scoreFlasher.score < 100) { return; }
		
		if (quality > 0.85) {
			stringFlasher.drop("Perfect! x3");
			world.state.score += scoreFlasher.score*2;
		} else if (quality > 0.62) {
			stringFlasher.drop("Good! x2");
			world.state.score += scoreFlasher.score;
		}
	}
	
	public void draw(FrameBuffer fb) {
		scoreFlasher.draw(fb);
		stringFlasher.draw(fb);
		if (ascending && lastPoint.y < 0) {
			realTimeBlitter.blit(fb, scoreForHeight(lastPoint.y), (fb.getWidth()/2), fb.getHeight()/2-40, IntBlitter.ALIGN_CENTER, 210, RGBColor.WHITE);
		}
	}
	
	class StringFlasher {
		private static final float FLOAT_MS = 750f;
		private static final float FADEOUT_MS = 500f;
		
		private final AGLFont font;
		
		SimpleVector position3D = new SimpleVector();
		SimpleVector position2D = new SimpleVector();
		private int transparency = 255;
		
		String message;
		boolean active = false;
		float timePassed = 0;
		
		StringFlasher() {
			Paint paint = new Paint();
			paint.setAntiAlias(true);
			paint.setTypeface(Typeface.DEFAULT_BOLD);
			paint.setTextSize(28);
			
			this.font = new AGLFont(paint);
		}
		
		public void timeStep(float stepMS) {
			if (!active) { return; }
			timePassed += stepMS;
			
			if (timePassed < FLOAT_MS) {
				position3D.set(world.state.marblePosition);
			} else {
				transparency -= (int) (stepMS*255/(FADEOUT_MS));
			}
			
			if (transparency <=0 ) {
				reset();
			}
		}
		
		public void drop(String message) {
			reset();
			active = true;
			position3D.set(world.state.marblePosition);
			this.message = message;
		}
		
		void reset() {
			active = false;
			timePassed = 0;
			transparency = 255;
		}
		
		public void draw(FrameBuffer fb) {
			if (!active) { return; }
			Interact2D.project3D2D(world.camera, fb, position3D, position2D);
			font.blitString(fb, message, (int)position2D.x-50, (int)position2D.y-40, transparency, RGBColor.WHITE);
			
		}
		
	}
	
	class ScoreFlasher {
		private static final float FADEOUT_MS = 1300f;
		private final IntBlitter blitter;
		SimpleVector position3D = new SimpleVector();
		SimpleVector position2D = new SimpleVector();
		private int transparency = 255;
		
		int score = 0;
		boolean active = false;
		
		ScoreFlasher() {
			this.blitter = new IntBlitter(36);
		}
		
		public void timeStep(float stepMS) {
			if (!active) { return; }
			transparency -= (int) (stepMS*255/(FADEOUT_MS));
			if (transparency <=0 ) {
				reset();
			}
		}
		
		public void drop(int score) {
			reset();
			this.position3D.set(world.state.marblePosition);
			this.score = score;
			this.active = true;
		}
		
		public void reset() {
			active = false;
			transparency = 255;
		}
		
		public void draw(FrameBuffer fb) {
			if (!active) { return; }
			
			Interact2D.project3D2D(world.camera, fb, position3D, position2D);
			blitter.blit(fb, score, (int)position2D.x, (int)position2D.y-40, IntBlitter.ALIGN_CENTER, transparency, RGBColor.WHITE);
		}
	}

}
