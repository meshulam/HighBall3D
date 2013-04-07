package meshlabs.hiball.utils;

/*
 * Simple smoothing function. 
 * time smoothing a la http://stackoverflow.com/questions/10648325/android-smooth-game-loop
 */
public class TimeSmoother implements Averager {
	private final float smoothFactor;
	private final float windowSize;
	
	private float movingAverage = 0;
	private float smoothedValue = 0;
	
	public TimeSmoother(float smoothing, float size) {
		this.smoothFactor = smoothing;
		this.windowSize = size;
	}
	
	public void initialize() {
		movingAverage = 0;
		smoothedValue = 0;
	}
	
	public void initialize(float entry) {
		movingAverage = entry;
		smoothedValue = entry;
	}
	
	public void addValue(float entry) {
		movingAverage = (entry+movingAverage*(windowSize-1))/windowSize;
		smoothedValue = smoothedValue + (movingAverage-smoothedValue)*smoothFactor;
	}
	
	public float getAverage() {
		return smoothedValue;
	}

}
