package net.meshlabs.yaam;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import net.meshlabs.yaam.levels.Level2;
import android.app.Activity;
import android.opengl.GLSurfaceView.Renderer;
import android.os.SystemClock;
import android.util.FloatMath;
import android.util.Log;

import com.threed.jpct.Camera;
import com.threed.jpct.FrameBuffer;
import com.threed.jpct.Light;
import com.threed.jpct.Loader;
import com.threed.jpct.Matrix;
import com.threed.jpct.Object3D;
import com.threed.jpct.Polyline;
import com.threed.jpct.SimpleVector;
import com.threed.jpct.Texture;
import com.threed.jpct.TextureManager;
import com.threed.jpct.World;
import com.threed.jpct.util.MemoryHelper;

public class GameWorld {
	
	private Activity activity;
	protected RendererImpl renderer;
	private World graphicsWorld;
	
	final protected SimpleVector gravity = new SimpleVector(0, 6, 0);
	private Marble marble;
	private Camera camera;
	private Level1Map map;

	private SimpleVector cameraPos = new SimpleVector(); // to reduce allocations
	private float cameraElevation = 3;  // doesn't change now
	private float cameraDistance = 0.2f;	// a value in [0,1]
	private float cameraAngle = -3.141592f / 2;		// Angle around the y axis
	private ArrayList<Object3D> staticObjects = new ArrayList<Object3D>();
	
	private long lastTimestamp = 0;
	private long last2FingerTimestamp = 0;
	
	public GameWorld(Activity parent) {
		this.activity = parent;
		this.renderer = new RendererImpl(this);
	}
	
	// angle and magnitude of force vector on the screen. turns into world coords for the marble. 
	public void applyForce(float screenPathAngle, float screenPathMagnitude) {
		
		marble.setForce(cameraAngle + 3.141592f/2 -screenPathAngle, -screenPathMagnitude);
		//marble.setForce(-x, -y, 0); // just for testing up/down collisions
	}
	
	// in normalized screen coords. 
	public void moveCamera(float dYaw, float dDistance) {
		last2FingerTimestamp = SystemClock.uptimeMillis();
		cameraAngle += dYaw;
		
		float sumCamDist = cameraDistance + dDistance;
		
		Log.i("MoveCamera", "Distance "+sumCamDist+"  after trying to add "+dDistance);
		if (sumCamDist > 0 &&  sumCamDist < 1) {
			cameraDistance = sumCamDist;
		} else {
			
		}

		//Log.i("gameworld", "moved camera by "+dYaw+" rotation and "+dDistance+"dist ");
		
	}
	
	private final static float CAMERA_MIN_DISTANCE=2f;
	private final static float CAMERA_MAX_DISTANCE=7f;
	private final static float CAMERA_CURVATURE=0.3f;
	
	private void pointCamera() {
		float adjustedCameraDistance = CAMERA_MIN_DISTANCE+cameraDistance*(CAMERA_MAX_DISTANCE-CAMERA_MIN_DISTANCE);
		float xPos = adjustedCameraDistance*FloatMath.cos(cameraAngle);
		float zPos = adjustedCameraDistance*FloatMath.sin(cameraAngle);
		
		float yPos = -CAMERA_CURVATURE*adjustedCameraDistance*adjustedCameraDistance;	// Camera traces out a parabola as its distance increases
		
		cameraPos.set(xPos, yPos, zPos);
		Log.i("Camera", "Setting camera pos to "+cameraPos+" relative");
		cameraPos.add(marble.getTransformedCenter());
		camera.setPosition(cameraPos);
		camera.lookAt(marble.getTransformedCenter());
	}
	
	private static final float TIME_RAMP_START = 50;
	private static final float TIME_RAMP_END = 600;
	
	// The main method that gets called each game loop
	public void updateGame(float timeStep) {
		long timeSinceFrozen = SystemClock.uptimeMillis() - last2FingerTimestamp;
		float timeScaleFactor = 0;
		
		if (timeSinceFrozen > TIME_RAMP_END) {	// normal time
			timeScaleFactor = 1f;
		} else if (timeSinceFrozen > TIME_RAMP_START) {  // ramp time
			timeScaleFactor = (timeSinceFrozen-TIME_RAMP_START)*(timeSinceFrozen-TIME_RAMP_START) / (TIME_RAMP_END*TIME_RAMP_END);
		}
		marble.timeStep(timeStep, timeScaleFactor);
		pointCamera();
		applyForce(0,0);
	}
	
	public void renderAndDraw(FrameBuffer fb) {
		graphicsWorld.renderScene(fb);
		graphicsWorld.draw(fb);
	}
	
	public void resyncRenderer() {
		reloadTextures();
	}
	
	public void createWorld() {
		if (graphicsWorld != null) {
			Log.i("GameWorld", "Destroying old graphicsWorld");
			graphicsWorld.dispose();
		}
		graphicsWorld = new World();
		graphicsWorld.setAmbientLight(180, 180, 180); 	// Default 100,100,100
		
		Light sun = new Light(graphicsWorld);
		sun.setIntensity(100, 100, 100);
		SimpleVector sv = new SimpleVector(0f, -150f, 0f);
		sun.setPosition(sv);
		
		reloadTextures();
		
		marble = new Marble(this, 0.5f);
		marble.translate(0, -1.5f, 0);
		CollisionHandler ch = new CollisionHandler(marble);
		
		//Level1Map map = new Level1Map(this);
		Level2 map = new Level2(this);
		map.addCollisionLister(ch);

		
		camera = graphicsWorld.getCamera();
		
		//graphicsWorld.buildAllObjects();
		
		MemoryHelper.compact();
		
		// just some stuff to test reflect methods
		
		
	}
	
	public void reloadTextures() {
		reloadTextureResource(R.raw.ball, false, Marble.TEXTURE);
		reloadTextureResource(R.raw.floor, false, Level2.MAP_TEXTURE);
		reloadTextureResource(R.raw.shadow_noalpha, false, BlobShadow.TEXTURE);
	}
	
	public void reloadTexture(String textureName, Texture texture) {
		if (TextureManager.getInstance().containsTexture(textureName)) {
			//TextureManager.getInstance().removeAndUnload(textureName, renderer.getFrameBuffer());
			return;
		}
		TextureManager.getInstance().addTexture(textureName, texture);
	}
	
	private void reloadTextureResource(int resourceID, boolean useAlpha, String textureName) {
		Texture tex = new Texture(activity.getResources().openRawResource(resourceID), useAlpha);
		reloadTexture(textureName, tex);
	}
	
	public Object3D load3DS(String filename, float scale) {
		Object3D[] model;
		InputStream is;
		try {
			is = activity.getAssets().open(filename);
		} catch (IOException e) {
			Log.e("GameWorld", "Unable to open asset "+filename);
			e.printStackTrace();
			return null;
		}
		model = Loader.load3DS(is, scale);
		
		try {
			is = activity.getAssets().open(filename);
		} catch (IOException e) {
			Log.e("GameWorld", "Unable to open asset "+filename);
			e.printStackTrace();
			return null;
		}
		String[] texs = Loader.readTextureNames3DS(is);
		for (int i = 0; i<texs.length; i++) {
			Log.i("ModelLoader", "Contains texture: "+texs[i]);
		}
		
		for (int i = 0; i<model.length; i++) {
			Log.i("ModeLoader", "Got object #"+i);
			model[i].setCenter(SimpleVector.ORIGIN);
			model[i].rotateX((float)(-.5*Math.PI));
			model[i].rotateMesh();
			model[i].setRotationMatrix(new Matrix());
		}
		Object3D obj3d = Object3D.mergeAll(model);
		obj3d.calcNormals();
		obj3d.build();
		return obj3d;
	}
	
	protected void addPolyline(Polyline pl) {
		graphicsWorld.addPolyline(pl);
	}
	
	public void addObject(Object3D obj) {
		graphicsWorld.addObject(obj);
	}

}
