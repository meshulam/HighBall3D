package net.meshlabs.yaam;

import com.threed.jpct.Object3D;
import com.threed.jpct.Primitives;
import com.threed.jpct.RGBColor;
import com.threed.jpct.Texture;

/**
 * Draw a path of n spheres behind a moving object.
 * @author matt
 *
 */
public class PathTracer {
	private final static int NUM_TRACES = 10;
	private final static float DROP_INTERVAL_SEC = 0.15f;
	private final static float RADIUS = 0.3f;
	
	private final static String TRACE_TEXTURE = "traceObjectTexture";
	private final Object3D[] traces = new Object3D[NUM_TRACES];
	private final Object3D parent;
	private float runningTime = 0;
	private int nextTraceIndex = 0;
	
	public PathTracer(Object3D parent, GameWorld world) {
		this.parent = parent;
		
		world.reloadTexture(TRACE_TEXTURE, new Texture(32, 32, new RGBColor(0, 200, 40)));
		
		for (int i=0; i<NUM_TRACES; i++) {
			traces[i] = initializeTrace();
			world.addObject(traces[i]);
		}
	}
	
	public void timeStep(float dTimeS) {
		runningTime += dTimeS;
		
		float scaleFactor = 1 - dTimeS/(DROP_INTERVAL_SEC*NUM_TRACES);
		for (int i=0; i<NUM_TRACES; i++) {
			traces[i].scale(scaleFactor);
		}
		
		
		if (runningTime > DROP_INTERVAL_SEC) {
			dropTrace(traces[nextTraceIndex]);
			nextTraceIndex++;
			nextTraceIndex %= NUM_TRACES;
			runningTime = 0;
		}
	}
	
	public void resetTraces() {
		for (int i=0; i<NUM_TRACES; i++) {
			traces[i].setVisibility(false);
		}
	}
	
	private void dropTrace(Object3D trace) {
		trace.clearTranslation();
		trace.translate(parent.getTransformedCenter());
		trace.setScale(1);
		trace.setVisibility(true);
	}
	
	private Object3D initializeTrace() {
		Object3D trace = Primitives.getSphere(8, RADIUS);
		trace.setTexture(TRACE_TEXTURE);
		trace.calcTextureWrapSpherical();
		trace.build();
		trace.strip();
		trace.setVisibility(false);
		trace.setTransparency(11);
		return trace;
	}
}
