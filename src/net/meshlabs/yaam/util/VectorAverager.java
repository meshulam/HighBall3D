package net.meshlabs.yaam.util;

import com.threed.jpct.SimpleVector;

public class VectorAverager {
	private final Averager xAvg;
	private final Averager yAvg;
	private final Averager zAvg;
	
	public VectorAverager(float smoothFactor, boolean useExponential) {
		if (useExponential) {
			xAvg = new ExponentialAverage(smoothFactor);
			yAvg = new ExponentialAverage(smoothFactor);
			zAvg = new ExponentialAverage(smoothFactor);
		} else {
			xAvg = new SimpleAverage(smoothFactor);
			yAvg = new SimpleAverage(smoothFactor);
			zAvg = new SimpleAverage(smoothFactor);
		}
	}
	
	public void add(SimpleVector entry) {
		xAvg.addValue(entry.x);
		yAvg.addValue(entry.y);
		zAvg.addValue(entry.z);
	}
	
	public void initialize(float x, float y, float z) {
		xAvg.initialize(x);
		yAvg.initialize(y);
		zAvg.initialize(z);
	}
	
	public SimpleVector getAverage() {
		return getAverage(SimpleVector.create());
	}
	
	/*
	 * Populate the given SimpleVector and return it.
	 */
	public SimpleVector getAverage(SimpleVector output) {
		output.x = xAvg.getAverage();
		output.y = yAvg.getAverage();
		output.z = zAvg.getAverage();
		return output;
	}
	

}
