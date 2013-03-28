package net.meshlabs.yaam;

import java.util.HashSet;
import java.util.Set;

import net.meshlabs.yaam.utils.GraphicsUtils;

import com.threed.jpct.CollisionListener;
import com.threed.jpct.Object3D;
import com.threed.jpct.Primitives;
import com.threed.jpct.SimpleVector;

public class Level1Map {
	public final static String MAP_TEXTURE = "Map1Texture";
	private final GameWorld world;
	
	private Set<Object3D> objects = new HashSet<Object3D>();
	
	public Level1Map(GameWorld world) {
		this.world = world;
		initialize();
	}
	
	public void initialize() {
		Object3D floor = Primitives.getPlane(20, 1);
		objects.add(floor);
		GraphicsUtils.tileTexture(floor, 10);
		floor.rotateX((float) Math.PI/2f);
		
		
		Object3D ramp = Primitives.getBox(1, 3);
		objects.add(ramp);
		ramp.calcTextureWrapSpherical();
		GraphicsUtils.tileTexture(ramp, 6);
		ramp.rotateY((float) Math.PI/4);
		ramp.rotateX((float) Math.PI*0.35f);
		ramp.setOrigin(new SimpleVector(-4, 1, 0));
		
		Object3D cone = Primitives.getCone(90, 5, 0.2f);
		objects.add(cone);
		cone.calcTextureWrapSpherical();
		GraphicsUtils.tileTexture(cone, 4);
		cone.setOrigin(new SimpleVector(5, 0, 3));
		
		loadToWorld();
	}
	
	private void loadToWorld() {
		for (Object3D obj: objects) {
			obj.setTexture(MAP_TEXTURE);
			obj.setCollisionMode(Object3D.COLLISION_CHECK_OTHERS);
			obj.strip();
			obj.build();
			world.addObject(obj);
		}
	}
	
	public void addCollisionLister(CollisionListener listener) {
		for (Object3D obj : objects) {
			obj.addCollisionListener(listener);
		}
	}

}
