package net.meshlabs.yaam.objects;

import com.threed.jpct.Object3D;

public interface GameObject {
	
	public void timeStep(float timeMs);
	public Object3D getPrototype();

}
