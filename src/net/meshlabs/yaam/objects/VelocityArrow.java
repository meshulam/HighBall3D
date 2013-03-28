package net.meshlabs.yaam.objects;

import net.meshlabs.yaam.GameWorld;
import net.meshlabs.yaam.utils.GraphicsUtils;

import com.threed.jpct.Matrix;
import com.threed.jpct.Object3D;
import com.threed.jpct.Primitives;
import com.threed.jpct.RGBColor;
import com.threed.jpct.SimpleVector;
import com.threed.jpct.Texture;

public class VelocityArrow {
	final private static String VEL_TEXTURE = "velocityArrowTexture";
	final private SimpleVector originalDir = new SimpleVector(0, 1, 0); 
	//final private SimpleVector offsetFromBall;
	
	final private static float SCALE = 0.25f;
	final private GameWorld world;
	private Object3D shaft;  

	final private SimpleVector[] points = new SimpleVector[2]; 
	
	/*
	 * Public factory method. 
	 */
	public static VelocityArrow create(GameWorld world, float rotationRadius) {
		VelocityArrow a = new VelocityArrow(world, rotationRadius);
		
		return a;
	}
	
	public VelocityArrow(GameWorld world, float marbleRadius) {
		this.world = world;

		shaft = Primitives.getCylinder(4, .05f, 5);
		shaft.setCenter(SimpleVector.ORIGIN);
		shaft.translate(0, 0.25f, 0);
		shaft.translateMesh();
		
		Object3D point = Primitives.getCone(4, 0.2f, 0.5f); // radius of 0.2, height of 0.2
		point.rotateX((float)(Math.PI));
		point.rotateMesh();
		point.setRotationMatrix(new Matrix());
		
		point.translate(0, 0.6f, 0);
		point.translateMesh();
		point.setTranslationMatrix(new Matrix());
		
		
		world.reloadTexture(VEL_TEXTURE, new Texture(32, 32, new RGBColor(180, 80, 50)));
		
		shaft = Object3D.mergeObjects(shaft, point);
		shaft.setTexture(VEL_TEXTURE);

		shaft.build();
		world.addObject(shaft);
		shaft.setRotationPivot(new SimpleVector(0, 0, 0));
		shaft.setOrigin(new SimpleVector(0, 0, 0));
		
	}
	
	public void updateArrow(final SimpleVector translation, final SimpleVector velocity, final float alpha) {
		if (velocity.length() < 0.1 || alpha > 0.9) {
			setVisibility(false);
		} else {
			setVisibility(true);
			//Log.i("VelArrow", "Transparency="+(1-alpha)*100);
			//shaft.setTransparency((int) (1-alpha)*100);
			
			shaft.clearRotation();
			
			Matrix rotm = shaft.getRotationMatrix();

			float oldValue = rotm.get(1, 1);
			rotm.set(1, 1, velocity.length()*SCALE - 1 + oldValue);	
			GraphicsUtils.rotateObject(shaft, originalDir, velocity);
			
			shaft.clearTranslation();
			shaft.translate(translation);
		}

	}
	
	public void setVisibility(boolean isVisible) {
		shaft.setVisibility(isVisible);
	}
	
	public void addParent(Object3D object) {
		shaft.addParent(object);
	}

}
