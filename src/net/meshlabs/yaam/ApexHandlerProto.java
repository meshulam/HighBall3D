package net.meshlabs.yaam;

import com.threed.jpct.SimpleVector;

public class ApexHandlerProto {
	
	public static final int APEX_MEMORY = 15;
	private final GameWorld world;
	private SimpleVector[] apexes = new SimpleVector[APEX_MEMORY];
	private int nextIndex = 0;
	
	public ApexHandlerProto(GameWorld world) {
		this.world = world;
	}
	
	public void addApex(SimpleVector point) {
		//apexes[nextIndex] = 
	}
	
	class Marker {
		public SimpleVector point;
		
		
	}

}
