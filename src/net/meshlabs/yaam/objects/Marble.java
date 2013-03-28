package net.meshlabs.yaam.objects;

import net.meshlabs.yaam.GameWorld;
import net.meshlabs.yaam.utils.VectorAverager;
import android.util.FloatMath;
import android.util.Log;

import com.threed.jpct.Matrix;
import com.threed.jpct.Object3D;
import com.threed.jpct.Primitives;
import com.threed.jpct.SimpleVector;

public class Marble extends Object3D {
	public final static String TEXTURE = "marbleTexture";
	private final GameWorld world;
	private final ForceArrow fArrow;

	private final BlobShadow shadow;
	private final PathTracer pathTracer;
	
	private final float elasticity = 0.65f;
	private final float mass = 4f;
	
	private final float radius;
	private final SimpleVector ellipsoid;
	private SimpleVector velocity = new SimpleVector();
	private SimpleVector force = new SimpleVector();
	
	private final VectorAverager vecSmoother = new VectorAverager(.4f, true);
	private SimpleVector smoothedForce = new SimpleVector();
	
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
		this.pathTracer = new PathTracer(world);
		
		this.shadow = new BlobShadow(world, radius*2f);
		
		this.setTexture(TEXTURE);
		this.calcTextureWrapSpherical();
		this.setCollisionMode(COLLISION_CHECK_SELF);
		this.setSpecularLighting(true);
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
	public void timeStep(float stepMS, float timeScaleFactor) {
		float step = stepMS*timeScaleFactor/1000.0f;
		
		dPosition.set(velocity);
		dPosition.scalarMul(step);
		
		dVelocity.set(force);
		dVelocity.scalarMul(1/mass);		// accel = f/m
		dVelocity.add(world.gravity);	// add gravity
		dVelocity.scalarMul(step);		// divide by time
		
		SimpleVector adjPosition = this.checkForCollisionEllipsoid(dPosition, ellipsoid, 1); // this will set collisionPoint
		//SimpleVector adjPosition = this.checkForCollisionSpherical(dPosition, radius);
			
		if (!adjPosition.equals(dPosition)) { // we have a collision
			// Decompose tangential+normal velocity
			SimpleVector normalV = SimpleVector.create(lastCollisionNormal); // from the CollisionListener
			normalV.scalarMul(normalV.calcDot(velocity));
			SimpleVector tangentV = velocity.calcSub(normalV);
			lastTangentVelocity  = tangentV;
			
			// then invert normal V to bounce off the surface
			normalV.scalarMul(-1.0f*elasticity);
			velocity = normalV.calcAdd(tangentV);
			
			//Log.i("Marble", "Coll n="+lastCollisionNormal+" vel="+velocity+" adj="+adjPosition+" dp="+dPosition);
			
		} else {	// no collision
			velocity.add(dVelocity);
		}
		checkCoinCollisions(adjPosition);
		
		
		this.translate(adjPosition);
		this.getTransformedCenter(world.state.marblePosition);
		
		if (world.state.maxHeight < -world.state.marblePosition.y) 
			world.state.maxHeight = world.state.marblePosition.y;
		
		// this kind of looks like rotational inertia but it's not
		float rotation = lastTangentVelocity.length()*step * 1f / radius;
		this.rotateAxis(lastCollisionNormal.calcCross(lastTangentVelocity), -rotation);
		
		castShadow();
		updateArrow();
		pathTracer.timeStep(step);
		
		force.set(0,0,0);
	}
	
	private void checkCoinCollisions(final SimpleVector move) {
		CoinKeeper keeper = world.keeper;
		keeper.setCollisionMode(COLLISION_CHECK_OTHERS);
		this.checkForCollisionSpherical(move, radius);
		keeper.setCollisionMode(COLLISION_CHECK_NONE);
	}
	
	public void resetState(final SimpleVector position) {
		velocity.set(SimpleVector.ORIGIN);
		force.set(SimpleVector.ORIGIN);
		lastCollisionNormal.set(SimpleVector.ORIGIN);
		lastTangentVelocity.set(SimpleVector.ORIGIN);
		pathTracer.resetTraces();
		this.clearRotation();
		this.clearTranslation();
		this.translate(position);
		
		this.getTransformedCenter(world.state.marblePosition);
		world.state.maxHeight = world.state.marblePosition.y;
		
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
		int object = checkForCollision(SimpleVector.create(0, 1f, 0), 100);
		if (object != Object3D.NO_OBJECT) { // Gotta draw a shadow
			shadow.drawAt(shadowContact, shadowNormal);
		} else {
			shadow.setVisibility(false);
		}
	}
	


}
