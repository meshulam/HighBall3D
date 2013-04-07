package meshlabs.hiball.objects;

import meshlabs.hiball.GameWorld;
import meshlabs.hiball.utils.GraphicsUtils;

import com.threed.jpct.Object3D;
import com.threed.jpct.Polyline;
import com.threed.jpct.Primitives;
import com.threed.jpct.SimpleVector;

public class BlobShadow extends Object3D {
	public static final String TEXTURE = "shadowTexture";
	private final SimpleVector originalOrientation = new SimpleVector(0, 0, -1);
	
	private final static Object3D prototype = Primitives.getPlane(1, 1);
	
	public BlobShadow(GameWorld world, float size) {
		super(BlobShadow.prototype);
		this.setScale(size);
		this.setTexture(BlobShadow.TEXTURE);
		this.setVisibility(false);
		this.setTransparency(128);
		this.strip();
		this.build();
		
		world.addObject(this);
	}
	
	public void drawAt(SimpleVector position, SimpleVector normal) {
		this.clearTranslation();
		this.translate(position.x, position.y-0.1f, position.z);

		this.clearRotation();
		GraphicsUtils.rotateObject(this, originalOrientation, normal);
		this.setVisibility(true);
	}

}
