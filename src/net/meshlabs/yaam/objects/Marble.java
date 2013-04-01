package net.meshlabs.yaam.objects;

import net.meshlabs.yaam.GameWorld;
import net.meshlabs.yaam.utils.VectorAverager;
import android.util.FloatMath;
import android.util.Log;

import com.threed.jpct.Matrix;
import com.threed.jpct.Object3D;
import com.threed.jpct.Polyline;
import com.threed.jpct.Primitives;
import com.threed.jpct.RGBColor;
import com.threed.jpct.SimpleVector;

public class Marble extends Object3D {
	public final static String TAG = "Marble";
	public final static String TEXTURE = "marbleTexture";
	private final GameWorld world;
	private final ForceArrow fArrow;

	private final BlobShadow shadow;
	private final SimpleVector[] altitudeArray = {new SimpleVector(), new SimpleVector()};
	private final Polyline altitudeMarker;
	
	private final float elasticity = 0.55f;
	private final float mass = 4f;
	
	private final float radius;
	private final SimpleVector ellipsoid;
	private SimpleVector velocity = new SimpleVector();
	private SimpleVector force = new SimpleVector();
	
	private final VectorAverager vecSmoother = new VectorAverager(.4f, true);
	private SimpleVector smoothedForce = new SimpleVector();
	private boolean lastWasAscending = false;
	
	// For movement
	private SimpleVector dPosition = new SimpleVector(); // create once and reuse each tick
	private SimpleVector dVelocity = new SimpleVector();
	public SimpleVector lastCollisionNormal = new SimpleVector();
	private SimpleVector lastTangentVelocity = new SimpleVector(); // For "rotational inertia"
	
	public SimpleVector shadowNormal = new SimpleVector();
	public SimpleVector shadowContact = new SimpleVector();
	
	public Marble(GameWorld w, float radius) {
		// Start with a copy of a sphere
		super(Primitives.getSphere(40, radius));
		
		this.world = w;
		this.radius = radius;
		this.ellipsoid = new SimpleVector(radius, radius, radius);
		this.fArrow = ForceArrow.create(world, radius);
		
		this.altitudeMarker = new Polyline(altitudeArray, RGBColor.GREEN);
		this.altitudeMarker.setWidth(3);
		this.altitudeMarker.setVisible(true);
		world.addPolyline(this.altitudeMarker);
		
		this.shadow = new BlobShadow(world, radius*2f);
		
		this.setTexture(TEXTURE);
		this.calcTextureWrapSpherical();
		this.setCollisionMode(COLLISION_CHECK_SELF);

		this.build();
		this.strip();
		world.addObject(this);
		
	}
	
	private static final float FORCE_MULTIPLE = 1;
	public void setForce(float yaw, float magnitude) {
		force.x += FORCE_MULTIPLE*magnitude*FloatMath.cos(yaw);
		force.z += FORCE_MULTIPLE*magnitude*FloatMath.sin(yaw);
	}
	
	double lastAngle= 0;
	public void timeStep(float stepMS) {
		float step = stepMS/1000.0f;
		
		dPosition.set(velocity);
		dPosition.scalarMul(step);
		
		dVelocity.set(force);
		dVelocity.scalarMul(1/mass);		// accel = f/m
		dVelocity.add(world.gravity);	// add gravity
		dVelocity.scalarMul(step);		// divide by time
		
		SimpleVector adjPosition = this.checkForCollisionEllipsoid(dPosition, ellipsoid, 1); // this will set collisionPoint
		//SimpleVector adjPosition = this.checkForCollisionSpherical(dPosition, radius);
			
		if (!adjPosition.equals(dPosition)) { // we have a collision
			if (world.scoringHandler.inFlight) {
				float q = 2 - lastCollisionNormal.calcAngleFast(velocity) / (3.14159f/2);
				Log.i(TAG, "Landed with q="+q+" normal="+lastCollisionNormal+" v="+velocity);
				world.scoringHandler.land(q);
			}
			
			// Decompose tangential+normal velocity
			SimpleVector normalV = SimpleVector.create(lastCollisionNormal); // from the CollisionListener
			normalV.scalarMul(normalV.calcDot(velocity));
			SimpleVector tangentV = velocity.calcSub(normalV);
			lastTangentVelocity  = tangentV;
			
			// then invert normal V to bounce off the surface
			normalV.scalarMul(-1.0f*elasticity);
			velocity = normalV.calcAdd(tangentV);
			
		} else {	// no collision
			velocity.add(dVelocity);
		}
		
		this.translate(adjPosition);
		this.getTransformedCenter(world.state.marblePosition);
		
		// this kind of looks like rotational inertia but it's not
		float rotation = lastTangentVelocity.length()*step * 1f / radius;
		this.rotateAxis(lastCollisionNormal.calcCross(lastTangentVelocity), -rotation);
		
		castShadow();
		updateArrow();
		//pathTracer.timeStep(step);
		
		force.set(0,0,0);
	}
	
	public void resetState(final SimpleVector position) {
		velocity.set(SimpleVector.ORIGIN);
		force.set(SimpleVector.ORIGIN);
		lastCollisionNormal.set(SimpleVector.ORIGIN);
		lastTangentVelocity.set(SimpleVector.ORIGIN);

		this.clearRotation();
		this.clearTranslation();
		this.translate(position);
		
		this.getTransformedCenter(world.state.marblePosition);
		
	}
	
	private final static float DEATH_LENGTH = 800;
	private float deathTime = 0;
	public boolean deathSequence(float timeStep) {
		if (deathTime > DEATH_LENGTH) { 
			deathTime = 0;
			return true; 
		}

		if (deathTime < 0.01) {
			this.clearRotation();
		}
		deathTime += timeStep;
		Matrix rotm = this.getRotationMatrix();
		
		float oldValue = rotm.get(1, 1);
		rotm.set(1, 1, -(timeStep/DEATH_LENGTH) + oldValue);
		
		return false;
	}
	
	private void updateArrow() {
		vecSmoother.add(force);
		smoothedForce = vecSmoother.getAverage(smoothedForce);
		fArrow.updateArrow(getTranslation(), smoothedForce); 
	}
	
	private void castShadow() {
		altitudeArray[0].set(world.state.marblePosition);
		altitudeArray[1].set(world.state.marblePosition);
		
		int object = checkForCollision(world.down, 100);
		if (object != Object3D.NO_OBJECT) { // Gotta draw a shadow
			shadow.drawAt(shadowContact, shadowNormal);
			altitudeArray[1].y = shadowContact.y;
		} else {
			shadow.setVisibility(false);
			altitudeArray[1].y = 10;
		}
		altitudeMarker.update(altitudeArray);
	}
	


}
