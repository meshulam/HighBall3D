package net.meshlabs.yaam;

import net.meshlabs.yaam.util.VectorAverager;
import android.util.FloatMath;
import android.util.Log;

import com.threed.jpct.Matrix;
import com.threed.jpct.Object3D;
import com.threed.jpct.Primitives;
import com.threed.jpct.SimpleVector;

public class Marble extends Object3D {
	public final static String TEXTURE = "marbleTexture";
	private final GameWorld world;
	private final ForceArrow arrow;
	private final BlobShadow shadow;
	
	private float ELASTICITY = 0.6f;
	
	private final float radius;
	private final SimpleVector ellipsoid;
	private SimpleVector position = new SimpleVector();
	private SimpleVector velocity = new SimpleVector();
	private SimpleVector force = new SimpleVector();
	
	private final VectorAverager vecSmoother = new VectorAverager(.4f, true);
	private SimpleVector smoothedForce = new SimpleVector();
	
	// For movement
	private SimpleVector dPosition = new SimpleVector(); // create once and reuse each tick
	private SimpleVector dVelocity = new SimpleVector();
	//public SimpleVector collisionPoint = new SimpleVector(); // hooks into CollisionHandler
	public SimpleVector lastCollisionNormal = new SimpleVector();
	private SimpleVector lastTangentVelocity = new SimpleVector(); // For "rotational inertia"
	
	public SimpleVector shadowNormal = new SimpleVector();
	public SimpleVector shadowContact = new SimpleVector();
	
	private int inContact = 0;
	private float mass = 4f;
	
	public Marble(GameWorld w, float radius) {
		// Start with a copy of a sphere
		super(Primitives.getSphere(40, radius));
		
		this.world = w;
		this.radius = radius;
		this.ellipsoid = new SimpleVector(radius, radius, radius);
		this.arrow = ForceArrow.create(world, radius);
		//arrow.addParent(this);
		
		this.shadow = new BlobShadow(world, radius*2f);
		
		this.setTexture(TEXTURE);
		this.calcTextureWrapSpherical();
		this.setCollisionMode(COLLISION_CHECK_SELF);
		
		this.build();
		this.strip();
		world.addObject(this);
		
		//this.che
	}
	
	private static final float FORCE_MULTIPLE = 1;
	public void setForce(float yaw, float magnitude) {
		//SimpleVector temp = SimpleVector.create(FORCE_MULTIPLE*magnitude*FloatMath.cos(yaw), 0, 
		//										FORCE_MULTIPLE*magnitude*FloatMath.sin(yaw));
		force.x += FORCE_MULTIPLE*magnitude*FloatMath.cos(yaw);
		force.z += FORCE_MULTIPLE*magnitude*FloatMath.sin(yaw);
		
		//Log.i("Marble", "Set force to ["+x+", "+y+", "+z+"]");
	}
	
	double lastAngle= 0;
	public void timeStep(float stepMS) {
		float step = stepMS/1000.0f;
		dPosition.set(velocity);
		dPosition.scalarMul(step);
		
		vecSmoother.add(force);
		smoothedForce = vecSmoother.getAverage(smoothedForce);
		
		Log.i("marble", "force="+force+" smoothed="+smoothedForce);
		
		//if (inContact > 0) {
		//	dVelocity.set(force);
		//} else {
		dVelocity.set(force);
		dVelocity.scalarMul(1/mass);		// accel = f/m
		dVelocity.add(world.gravity);	// add gravity
					
			
		//}
		dVelocity.scalarMul(step);
		
		//SimpleVector adjPosition = this.checkForCollisionEllipsoid(dPosition, ellipsoid, 2); // this will set collisionPoint
		SimpleVector adjPosition = this.checkForCollisionSpherical(dPosition, radius);

		translate(adjPosition); // TODO: see if can just keep a reference to the origin
		
		arrow.updateArrow(getTranslation(), smoothedForce); // this is the real one
		//arrow.updateArrow(getTranslation(), velocity);
		
		// TODO: bug here; need to do fuzzy compare -- no?
		if (!adjPosition.equals(dPosition)) { // we have a collision
			//inContact++;

			SimpleVector normalV = SimpleVector.create(lastCollisionNormal); // from the CollisionListener
			normalV.scalarMul(normalV.calcDot(velocity));
			SimpleVector tangentV = velocity.calcSub(normalV);
			lastTangentVelocity  = tangentV;
			
			normalV.scalarMul(-1.0f*ELASTICITY);
			velocity = normalV.calcAdd(tangentV);
			
			
			//Log.i("Marble", "Collision! normal:"+normal+" oldV:"+velocity+" newV:"+normalV.calcAdd(tangentV));

			Matrix rm = this.getRotationMatrix();
			float trace = rm.get(0, 0) + rm.get(1, 1) + rm.get(2, 2);
			float calcAng = (float) Math.acos((trace-1)/2);
			//Log.i("Marble", "adjPos:"+adjPosition+" leng:"+adjPosition.length()+" rotat="+rotation+" calc="+(calcAng-lastAngle));
			lastAngle = calcAng;
			
			
		} else {	// no collision
			inContact = 0;
			//this.disableCollisionListeners();
			velocity.add(dVelocity);
		}
		
		// this kind of looks like rotational inertia but it's not
		float rotation = lastTangentVelocity.length()*step * 1f / radius;
		this.rotateAxis(lastCollisionNormal.calcCross(lastTangentVelocity), -rotation);
		
		castShadow();
		
		force.set(0,0,0);

	}
	
	private void castShadow() {
		int object = checkForCollision(SimpleVector.create(0, 1f, 0), 20);
		if (object != Object3D.NO_OBJECT) { // Gotta draw a shadow
			shadow.drawAt(shadowContact, shadowNormal);
		} else {
			shadow.setVisibility(false);
		}
	}
	


}
