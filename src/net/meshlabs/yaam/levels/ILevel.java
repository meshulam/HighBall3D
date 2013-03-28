package net.meshlabs.yaam.levels;

import com.threed.jpct.CollisionListener;
import com.threed.jpct.SimpleVector;

public interface ILevel {
	public boolean isOutsideBoundaries(SimpleVector position);
	public SimpleVector getStartingBallPosition();
	public float getStartingCameraAngle();
}
