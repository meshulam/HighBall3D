package net.meshlabs.yaam.objects;

import net.meshlabs.yaam.GameWorld;
import raft.glfont.android.AGLFont;
import android.graphics.Paint;
import android.graphics.Typeface;

import com.threed.jpct.FrameBuffer;
import com.threed.jpct.Interact2D;
import com.threed.jpct.RGBColor;
import com.threed.jpct.SimpleVector;

/**
 * Blit the ball's height onto the screen.
 * @author matt
 *
 */
public class DynamicHeightMarker {
	private final GameWorld world;
	private final AGLFont font;
	public SimpleVector position3d = new SimpleVector();
	public SimpleVector position2d = new SimpleVector();
	public boolean showGhost = false;
	
	public DynamicHeightMarker(GameWorld world) {
		this.world = world;
		
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		//paint.setTypeface(Typeface.create((String)null, Typeface.BOLD));
		paint.setTypeface(Typeface.DEFAULT_BOLD);
		paint.setTextSize(24);
		
		this.font = new AGLFont(paint);
	}
	
	public void draw(FrameBuffer fb) {
		position3d.set(world.state.marblePosition);
		position3d.y -= 0.75;
		Interact2D.project3D2D(world.camera, fb, position3d, position2d);
		
		String display = Float.toString(-world.state.marblePosition.y); 
		
		font.blitString(fb, display, (int)position2d.x, (int)position2d.y, 256, RGBColor.WHITE);
	}

}
