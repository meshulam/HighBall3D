package net.meshlabs.yaam.levels;

import net.meshlabs.yaam.GameWorld;

import com.threed.jpct.Object3D;
import com.threed.jpct.OcTree;
import com.threed.jpct.SimpleVector;

public class Level2 implements ILevel {
	public final static String MAP_TEXTURE = "floor.png";
	private final static String MODEL_FILE = "bowl.3ds";
	private final static SimpleVector STARTING_BALL_POSITION = new SimpleVector(0, 0, 0);
	private final static float STARTING_CAMERA_ANGLE = 0; // Radians around the y axis, 0=looking toward -x
	
	// These two vectors create the killer boundary box. 
	private final static SimpleVector BOUNDARY_MAX = new SimpleVector(500, 10, 500);
	private final static SimpleVector BOUNDARY_MIN = new SimpleVector(-500, -10000, -500);
	private final GameWorld world;
	
	//private Set<Object3D> statics = new HashSet<Object3D>();
	
	public Level2(GameWorld world) {
		this.world = world;
		initialize();
	}
	
	private void initialize() {
		//Object3D obj = world.load3DS(MODEL_FILE, 0.5f);
		Object3D obj = world.load3DS(MODEL_FILE, 8f);
		obj.setTexture(MAP_TEXTURE);
		loadStatic(obj);

	}
	
	
	
	private void loadStatic(Object3D obj) {
		obj.setCollisionMode(Object3D.COLLISION_CHECK_OTHERS);
		obj.setCollisionOptimization(true);
		OcTree tree = new OcTree(obj, 500, OcTree.MODE_OPTIMIZED);
		tree.setCollisionUse(true);
		tree.setRenderingUse(false);
		obj.setOcTree(tree);
		obj.strip();
		obj.build();
		world.addStatic(obj);
	}
	
	public boolean isOutsideBoundaries(SimpleVector position) {
		if (position.x > BOUNDARY_MAX.x || position.x < BOUNDARY_MIN.x ||
			position.y > BOUNDARY_MAX.y || position.y < BOUNDARY_MIN.y ||
			position.z > BOUNDARY_MAX.z || position.z < BOUNDARY_MIN.z ) {
			//Log.i("Level2", "Pos "+position+" violates bounds min="+BOUNDARY_MIN+" max="+BOUNDARY_MAX);
			return true;
		}
		return false;
	}
	
	public SimpleVector getStartingBallPosition() {
		return STARTING_BALL_POSITION;
	}
	
	public float getStartingCameraAngle() {
		return STARTING_CAMERA_ANGLE;
	}

}
