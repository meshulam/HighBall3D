package net.meshlabs.yaam.utils;

import raft.glfont.android.AGLFont;
import android.graphics.Paint;
import android.graphics.Typeface;

import com.threed.jpct.FrameBuffer;
import com.threed.jpct.RGBColor;

/**
 * Blit the given int to a framebuffer without allocating a new string each time.
 * @author matt
 *
 */
public class IntBlitter {
	public final static int ALIGN_LEFT = 0;
	public final static int ALIGN_CENTER = 1;
	public final static int ALIGN_RIGHT = 2;
	
	private final static int LENGTH = 16;
	
	private final AGLFont font;
	private final char[] chars = new char[LENGTH];
	private final char[] reversedChars = new char[LENGTH];
	
	public IntBlitter(float fontSize) {
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setTypeface(Typeface.DEFAULT_BOLD);
		paint.setTextSize(fontSize);
		
		this.font = new AGLFont(paint);
	}
	
	public void blit(FrameBuffer fb, int value, int x, int y, int alignment, int transparency, RGBColor color) {
		int remaining = value;
		int digits = 0;
		
		if (remaining == 0) {
			reversedChars[0] = '0';
			digits++;
		}
		
		while(remaining > 0) {
			reversedChars[digits++] = (char) ('0' | (remaining % 10));
			remaining /= 10;
		} 
			
		for (int i=0; i<LENGTH; i++) {
			if (i < digits) {
				chars[i] = reversedChars[digits-i-1];
			} else {
				chars[i] = ' ';
			}
		}
		
		int width = font.getStringWidth(chars, digits);
		
		if (alignment == ALIGN_RIGHT) {
			x -= width;
		} else if (alignment == ALIGN_CENTER) {
			x -= (width/2);
		}
		
		font.blitString(fb, chars, x, y, transparency, color);
	}

}
