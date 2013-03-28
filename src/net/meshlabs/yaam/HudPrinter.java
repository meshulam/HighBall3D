package net.meshlabs.yaam;

import raft.glfont.android.AGLFont;

import android.R.integer;
import android.graphics.Paint;
import android.graphics.Typeface;

import com.threed.jpct.FrameBuffer;
import com.threed.jpct.RGBColor;

public class HudPrinter {
	private final AGLFont font;
	private final GameState state;
	
	private char[] heightString = "Height:        ".toCharArray();	// start writing at i=8
	private char[] maxHeightString = "Max:         ".toCharArray();  // start writing at i=5
	private char[] fpsString = "FPS:     ".toCharArray();   		// start writing at i=5
	
	public HudPrinter(GameState state) {
		this.state = state;
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		//paint.setTypeface(Typeface.create((String)null, Typeface.BOLD));
		paint.setTypeface(Typeface.DEFAULT_BOLD);
		paint.setTextSize(20);
		
		this.font = new AGLFont(paint);
	}
	
	public void printHud(FrameBuffer fb) {
		int height = fb.getHeight();
		int width = fb.getWidth();
		font.blitString(fb, "FPS: "+state.fps, 10, height-10, 255, RGBColor.WHITE);

		//String max = String.format("Max: %4.1f", state.maxHeight);
		font.blitString(fb, "Height: "+(int)(-state.marblePosition.y), 10, 30, 255, RGBColor.WHITE);
		font.blitString(fb, "Max: "+(int)(-state.maxHeight), 10, 60, 255, RGBColor.WHITE);
	}
	

}
