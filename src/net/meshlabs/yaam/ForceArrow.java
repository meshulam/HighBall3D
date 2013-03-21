package net.meshlabs.yaam;

import net.meshlabs.yaam.util.GraphicsUtils;
import android.util.Log;

import com.threed.jpct.Matrix;
import com.threed.jpct.Mesh;
import com.threed.jpct.Object3D;
import com.threed.jpct.Primitives;
import com.threed.jpct.RGBColor;
import com.threed.jpct.SimpleVector;
import com.threed.jpct.Texture;
import com.threed.jpct.TextureManager;

public class ForceArrow {
	final private static String FORCE_TEXTURE = "forceArrowTexture";
	final private SimpleVector originalDir = new SimpleVector(0, 1, 0); 
	//final private SimpleVector offsetFromBall;
	
	final private static float SCALE = 0.12f;
	final private GameWorld world;
	private Object3D shaft;  

	/*
	 * Public factory method. 
	 */
	public static ForceArrow create(GameWorld world, float rotationRadius) {
		ForceArrow a = new ForceArrow(world, rotationRadius);
		
		return a;
	}
	
	public ForceArrow(GameWorld world, float marbleRadius) {
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
		
		
		world.reloadTexture(FORCE_TEXTURE, new Texture(32, 32, new RGBColor(0, 255, 0)));
		
		shaft = Object3D.mergeObjects(shaft, point);
		shaft.setTexture(FORCE_TEXTURE);

		shaft.build();
		world.addObject(shaft);
		
		shaft.setOrigin(new SimpleVector(0, -marbleRadius*2.25, 0));
		
	}
	
	public void updateArrow(final SimpleVector translation, final SimpleVector force) {
		if (force.length() < 0.1) {
			shaft.setVisibility(false);
		} else {
			shaft.setVisibility(true);

			float angle = force.calcAngleFast(originalDir);
			SimpleVector rotationAxis = force.calcCross(originalDir);
			
			shaft.clearRotation();
			Matrix rotm = shaft.getRotationMatrix();

			float oldValue = rotm.get(1, 1);
			rotm.set(1, 1, force.length()*SCALE - 1 + oldValue);
			shaft.rotateAxis(rotationAxis, angle);

			shaft.clearTranslation();
			shaft.translate(translation);
		}

	}
	
	public void addParent(Object3D object) {
		shaft.addParent(object);
	}

}
