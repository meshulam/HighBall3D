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
	protected Renderer renderer;
	private World graphicsWorld;
	
	final protected SimpleVector gravity = new SimpleVector(0, 6, 0);
	private Marble marble;
	private Camera camera;
	private Level1Map map;

	private float cameraElevation = 3;  // doesn't change now
	private float cameraDistance = 3;
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
		
		if (sumCamDist > 1 &&  sumCamDist < 15) {
			cameraDistance = sumCamDist;
		}

		//Log.i("gameworld", "moved camera by "+dYaw+" rotation and "+dDistance+"dist ");
		
	}

	
	private void pointCamera() {
		SimpleVector cameraPos = SimpleVector.create(cameraDistance*FloatMath.cos(cameraAngle),
				-cameraElevation, cameraDistance*FloatMath.sin(cameraAngle));
		cameraPos.add(marble.getTransformedCenter());
		
		camera.setPosition(cameraPos);
		camera.lookAt(marble.getTransformedCenter());
	}
	
	private static final float TIME_RAMP_START = 50;
	private static final float TIME_RAMP_END = 600;
	
	// The main method that gets called each game loop
	public void updateGame(float timeStep) {
		long timeSinceFrozen = SystemClock.uptimeMillis() - last2FingerTimestamp;
		
		if (timeSinceFrozen > TIME_RAMP_END) {	// normal time
			marble.timeStep(timeStep);
		} else if (timeSinceFrozen > TIME_RAMP_START) {  // ramp time
			float timeScaleFactor = (timeSinceFrozen-TIME_RAMP_START)*(timeSinceFrozen-TIME_RAMP_START) / (TIME_RAMP_END*TIME_RAMP_END);
			marble.timeStep(timeStep*timeScaleFactor);
		}
		pointCamera();
		applyForce(0,0);
	}
	
	public void renderAndDraw(FrameBuffer fb) {
		graphicsWorld.renderScene(fb);
		graphicsWorld.draw(fb);
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
		reloadTexture(R.raw.ball, false, Marble.TEXTURE);
		reloadTexture(R.raw.floor, false, Level2.MAP_TEXTURE);
		reloadTexture(R.raw.shadow_noalpha, false, BlobShadow.TEXTURE);
	}
	
	private void reloadTexture(int resourceID, boolean useAlpha, String textureName) {
		if (TextureManager.getInstance().containsTexture(textureName)) {
			TextureManager.getInstance().removeTexture(textureName);
		}
		Texture tex = new Texture(activity.getResources().openRawResource(resourceID), useAlpha);
		TextureManager.getInstance().addTexture(textureName, tex);
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
