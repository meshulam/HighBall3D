package net.meshlabs.yaam.objects;

import net.meshlabs.yaam.GameState;
import net.meshlabs.yaam.GameWorld;
import net.meshlabs.yaam.R;

import com.threed.jpct.Object3D;
import com.threed.jpct.Primitives;
import com.threed.jpct.SimpleVector;

/**
 * Draw a path of n spheres behind a moving object.
 * @author matt
 *
 */
public class PathTracer {
	private final static int NUM_TRACES = 100;
	private final static float DECAY_SECS = 5f;
	private final static float RADIUS = 0.2f;
	
	private final static String TRACE_TEXTURE = "traceObjectTexture";
	private final Object3D[] traces = new Object3D[NUM_TRACES];
	private final GameState state;
	private SimpleVector lastPoint;
	private float runningTime = 0;
	private int nextTraceIndex = 0;
	
	public PathTracer(GameWorld world) {
		this.state = world.state;
		world.reloadTextureResource(R.raw.trace, true, TRACE_TEXTURE);
		//world.reloadTexture(TRACE_TEXTURE, new Texture(32, 32, new RGBColor(100, 192, 100)));
		
		for (int i=0; i<NUM_TRACES; i++) {
			traces[i] = initializeTrace();
			world.addObject(traces[i]);
		}
	}
	
	public void timeStep(float dTimeS) {
//		int dTransparency = (int) (dTimeS/(DROP_INTERVAL_SEC*NUM_TRACES)*256);
//		for (int i=0; i<NUM_TRACES; i++) {
//			int newTrans = traces[i].getTransparency() - dTransparency;
//			if (newTrans > 0) {
//				traces[i].setTransparency(newTrans);
//			}
//		}
	
		dropTrace(traces[nextTraceIndex]);
		nextTraceIndex++;
		nextTraceIndex %= NUM_TRACES;
		runningTime = 0;
	}
	
	public void resetTraces() {
		for (int i=0; i<NUM_TRACES; i++) {
			traces[i].setVisibility(false);
		}
	}
	
	private void dropTrace(Object3D trace) {
		trace.clearTranslation();
		trace.translate(state.marblePosition);
		//trace.setTransparency(255);
		trace.setVisibility(true);
	}
	
	private Object3D initializeTrace() {
		Object3D trace = Primitives.getPlane(1, RADIUS);
		trace.setTexture(TRACE_TEXTURE);
		trace.setBillboarding(true);
		trace.build();
		trace.strip();
		trace.setVisibility(false);
		trace.setTransparency(180);
		return trace;
	}
}
