package net.meshlabs.yaam;

import net.meshlabs.yaam.util.GraphicsUtils;
import android.util.Log;

import com.threed.jpct.Object3D;
import com.threed.jpct.Primitives;
import com.threed.jpct.RGBColor;
import com.threed.jpct.SimpleVector;

public class BlobShadow extends Object3D {
	public static final String TEXTURE = "shadowTexture";
	private final SimpleVector originalOrientation = new SimpleVector(0, 0, -1);
	
	private final float size;
	
	public BlobShadow(GameWorld world, float size) {
		super(Primitives.getPlane(1, size));
		this.size = size;
		this.setTexture(BlobShadow.TEXTURE);
		this.setVisibility(false);
		this.setTransparency(5);
		this.strip();
		this.build();
		
		world.addObject(this);
	}
	
	public void drawAt(SimpleVector position, SimpleVector normal) {
		this.clearTranslation();
		this.translate(position.x, position.y-0.1f, position.z);
		//Log.i("BLobShadow", "At "+position+" normal"+normal);
		// TODO: un-hardcode this
		this.clearRotation();
		GraphicsUtils.rotateObject(this, originalOrientation, normal);
		this.setVisibility(true);
	}

}
