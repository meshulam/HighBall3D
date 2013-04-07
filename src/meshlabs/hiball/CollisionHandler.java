package meshlabs.hiball;

import meshlabs.hiball.objects.Marble;
import meshlabs.hiball.R;
import android.util.Log;

import com.threed.jpct.CollisionEvent;
import com.threed.jpct.CollisionListener;
import com.threed.jpct.PolygonManager;

public class CollisionHandler implements CollisionListener {
	private final Marble source;
	
	public CollisionHandler(Marble object) {
		this.source = object;
	}

	@Override
	public void collision(CollisionEvent ce) {
		if (ce.getSource() != source) {
			Log.w("CollisionHandler", "Got a wrong collision event");
			return;
		}
		
		//SimpleVector v = ce.getFirstContact();
		//source.collisionPoint = v;
		
		PolygonManager pm = ce.getObject().getPolygonManager();
		int[] polys = ce.getPolygonIDs();
		
		if (ce.getAlgorithm() == CollisionEvent.ALGORITHM_RAY) { // It's for the shadow
			source.shadowNormal = pm.getTransformedNormal(polys[0]);
			source.shadowContact = ce.getFirstContact();
		} else {	// It's for a collision
			source.lastCollisionNormal.set(0, 0, 0);
			for (int i=0; i<polys.length; i++) {
				source.lastCollisionNormal.add(pm.getTransformedNormal(polys[i]));
			}
			source.lastCollisionNormal.normalize(source.lastCollisionNormal);	// collision polygons might have origin vectors?
		}
		
		
		// just to see if this ever happens
//		if (polys.length > 1) {
//			for (int i=0; i<polys.length; i++) {
//				Log.i("CollisionHandler", "Poly"+i+" normal:"+pm.getTransformedNormal(polys[i]));
//			}
//		}
	}

	@Override
	public boolean requiresPolygonIDs() {
		return true;
	}
	
	private static final long serialVersionUID = -2046393864388046042L;
}
