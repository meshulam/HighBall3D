package net.meshlabs.yaam.util;

public class ExponentialAverage implements Averager {
	private final float alpha;
	private float average;
	
	public ExponentialAverage(float alpha) {
		this.alpha = alpha;
	}

	@Override
	public void initialize(float value) {
		this.average = value;
	}

	@Override
	public void addValue(float value) {
		this.average += alpha*(value-this.average);
	}

	@Override
	public float getAverage() {
		return average;
	}

}
