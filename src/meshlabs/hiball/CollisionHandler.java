package meshlabs.hiball;

import meshlabs.hiball.objects.Marble;
import android.util.Log;

import com.threed.jpct.CollisionEvent;
import com.threed.jpct.CollisionListener;
import com.threed.jpct.PolygonManager;

/**
 * Responsible for getting normal vectors on collision events, for drawing shadow and determining bounce orientation.
 *
 */
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
		
		PolygonManager pm = ce.getObject().getPolygonManager();
		int[] polys = ce.getPolygonIDs();
		
		if (ce.getAlgorithm() == CollisionEvent.ALGORITHM_RAY) { // It's for the shadow
			source.shadowNormal = pm.getTransformedNormal(polys[0]);
			source.shadowContact = ce.getFirstContact();
		} else {	// It's for a physical collision
			source.lastCollisionNormal.set(0, 0, 0);
			// Average the normal vectors of all polygons that were hit. Results in fewer random-looking bounces.
			for (int i=0; i<polys.length; i++) {
				source.lastCollisionNormal.add(pm.getTransformedNormal(polys[i]));
			}
			source.lastCollisionNormal.normalize(source.lastCollisionNormal);	// collision polygons might have origin vectors?
		}
		
	}

	@Override
	public boolean requiresPolygonIDs() {
		return true;
	}
	
	private static final long serialVersionUID = -2046393864388046042L;
}
