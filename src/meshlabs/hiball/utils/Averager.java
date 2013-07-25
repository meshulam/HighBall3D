package meshlabs.hiball.utils;

/**
 * Interface for smoothing a sequence of numeric values. 
 *
 */
public interface Averager {
	public void initialize(float value);
	public void addValue(float value);
	public float getAverage();

}
