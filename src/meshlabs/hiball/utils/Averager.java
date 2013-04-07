package meshlabs.hiball.utils;

public interface Averager {
	public void initialize(float value);
	public void addValue(float value);
	public float getAverage();

}
