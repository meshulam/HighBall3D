package net.meshlabs.yaam.levels;

import java.util.HashSet;
import java.util.Set;

import net.meshlabs.yaam.GameWorld;
import net.meshlabs.yaam.util.GraphicsUtils;

import com.threed.jpct.CollisionListener;
import com.threed.jpct.Object3D;

public class Level2 {
	public final static String MAP_TEXTURE = "floor.png";
	private final static String MODEL_FILE = "halfpipe2.3ds";
	private final GameWorld world;
	private Object3D statics;
	
	private Set<Object3D> objects = new HashSet<Object3D>();
	
	public Level2(GameWorld world) {
		this.world = world;
		initialize();
	}
	
	public void initialize() {
		Object3D obj = world.load3DS(MODEL_FILE, 0.5f);
		
		objects.add(obj);
		GraphicsUtils.printPolyInfo(obj, 3, 0, true);
		loadToWorld();
	}
	
	private void loadToWorld() {
		for (Object3D obj: objects) {
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
