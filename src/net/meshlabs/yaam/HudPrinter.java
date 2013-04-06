package net.meshlabs.yaam;

import net.meshlabs.yaam.utils.IntBlitter;
import raft.glfont.android.AGLFont;
import raft.glfont.android.Rectangle;
import android.graphics.Paint;
import android.graphics.Typeface;

import com.threed.jpct.FrameBuffer;
import com.threed.jpct.RGBColor;

public class HudPrinter {
	private final AGLFont font;
	private final GameState state;
	private final IntBlitter intBlitter;
	
	
	private String scoreString = "Score: ";
	private String maxHeightString = "Highest: ";
	private int scoreOffset = 0;
	private int maxHeightOffset = 0;
	private String fpsString = "FPS:"; 
	
	public HudPrinter(GameState state) {
		this.state = state;
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		//paint.setTypeface(Typeface.create((String)null, Typeface.BOLD));
		paint.setTypeface(Typeface.DEFAULT_BOLD);
		paint.setTextSize(24);
		
		intBlitter = new IntBlitter(24);
		
		this.font = new AGLFont(paint);
		
		Rectangle rect = new Rectangle();
		font.getStringBounds(scoreString, rect);
		scoreOffset = rect.width;
		
		font.getStringBounds(maxHeightString, rect);
		maxHeightOffset = rect.width;
	}
	
	public void printHud(FrameBuffer fb) {
		int height = fb.getHeight();
		int width = fb.getWidth();
		//font.blitString(fb, fpsString, 10, height-10, 255, RGBColor.WHITE);
		//intBlitter.blit(fb, (int)state.fps, 70, height-10, IntBlitter.ALIGN_LEFT, 255, RGBColor.WHITE);

		font.blitString(fb, scoreString, 10, 30, 255, RGBColor.WHITE);
		intBlitter.blit(fb, state.score, 10+scoreOffset, 30, IntBlitter.ALIGN_LEFT, 255, RGBColor.WHITE);
		
		font.blitString(fb, maxHeightString, 10, 70, 255, RGBColor.WHITE);
		intBlitter.blit(fb, (int)state.maxHeightScore, 10+maxHeightOffset, 70, IntBlitter.ALIGN_LEFT, 255, RGBColor.WHITE);
	}
	

}
