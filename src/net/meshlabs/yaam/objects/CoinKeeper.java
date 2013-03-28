package net.meshlabs.yaam.objects;

import net.meshlabs.yaam.GameWorld;
import android.util.Log;

import com.threed.jpct.CollisionEvent;
import com.threed.jpct.CollisionListener;

public class CoinKeeper implements CollisionListener {
	private static final long serialVersionUID = 695488171777466627L;

	private final static int MAX_COINS = 500;
	
	private final Coin[] coins = new Coin[MAX_COINS];
	private final GameWorld world;
	private int size = 0;
	
	public CoinKeeper(GameWorld world) {
		this.world = world;
	}
	
	public void addCoin(Coin c) {
		world.addObject(c);
		c.addCollisionListener(this);
		coins[size] = c;
		c.id = size;
		size++;
	}
	
	public void setCollisionMode(int mode) {
		for (int i=0; i<size; i++) {
			coins[i].setCollisionMode(mode);
		}
	}

	@Override
	public void collision(CollisionEvent ce) {
		Coin c = (Coin) ce.getObject();
		c.collect();
		
		// just to see if this ever happens
		if (!c.getVisibility()) {
			Log.i("CoinKeeper", "collision with invisible coin!");
		}
		
	}

	@Override
	public boolean requiresPolygonIDs() {
		return false;
	}

}
