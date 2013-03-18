package net.meshlabs.yaam.util;

public interface Averager {
	public void initialize(float value);
	public void addValue(float value);
	public float getAverage();

}
