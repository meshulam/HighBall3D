package net.meshlabs.yaam.objects;

import net.meshlabs.yaam.GameWorld;
import android.util.Log;

import com.threed.jpct.Object3D;
import com.threed.jpct.RGBColor;
import com.threed.jpct.Texture;

public class Coin extends Object3D {
	private static String MODEL_FILE = "coin.3ds";
	private static String TEXTURE = "coinTexture";
	private static Object3D prototype = null;
	private static GameWorld world;
	
	protected int id; // index in CoinKeeper array 
	
	public Coin() {
		super(prototype);
		this.setTexture(TEXTURE);
		this.strip();
		this.build();
	}
	
	public void collect() {
		this.setVisibility(false);
		world.state.score++;
		Log.i("Coin", "Collected coin at "+this.getTransformedCenter());
		
	}
	
	public static void createAt(float x, float y, float z) {
		Coin c = new Coin();
		world.keeper.addCoin(c);
		c.translate(x, y, z);
		
	}
	
	public static void registerPrototype(GameWorld world) {
		Coin.world = world;
		world.reloadTexture(TEXTURE, new Texture(32, 32, new RGBColor(180, 130, 40)));
		if (prototype == null) {
			prototype = world.load3DS(MODEL_FILE, 1);
		}
	}

}
