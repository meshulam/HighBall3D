package net.meshlabs.yaam;

import android.util.Log;

import com.threed.jpct.CollisionEvent;
import com.threed.jpct.CollisionListener;
import com.threed.jpct.Object3D;
import com.threed.jpct.PolygonManager;
import com.threed.jpct.SimpleVector;

public class CollisionHandlerTest implements CollisionListener {

	@Override
	public void collision(CollisionEvent ce) {
		
		SimpleVector v = ce.getFirstContact();
		String algo;
		switch (ce.getAlgorithm()) {
		case CollisionEvent.ALGORITHM_ELLIPSOID:
			algo = "Ellipsoid";
			break;
		case CollisionEvent.ALGORITHM_RAY:
			algo = "Ray";
			break;
		case CollisionEvent.ALGORITHM_SPHERE:
			algo = "Sphere";
			break;
		default:
			algo = "unknown";
		}
		
		Log.i("Collision", "FirstContact:"+v+" Algo:"+algo);
	}

	@Override
	public boolean requiresPolygonIDs() {
		return false;
	}
	
	private static final long serialVersionUID = -2046393864388046042L;
}
