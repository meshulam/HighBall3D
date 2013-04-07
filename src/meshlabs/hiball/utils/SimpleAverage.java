package meshlabs.hiball.utils;

public class SimpleAverage implements Averager {
	private final float windowSize;
	private float average = 0;
	
	public SimpleAverage(float windowSize) {
		this.windowSize = windowSize;
	}

	@Override
	public void initialize(float value) {
		this.average = value;
	}

	@Override
	public void addValue(float value) {
		average = (value + average*(windowSize-1))/windowSize;
	}

	@Override
	public float getAverage() {
		return average;
	}

}
