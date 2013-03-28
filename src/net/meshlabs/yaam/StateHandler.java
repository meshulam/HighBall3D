package net.meshlabs.yaam;

import java.io.FileInputStream;
import java.io.FileOutputStream;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.convert.Converter;
import org.simpleframework.xml.core.Persister;
import org.simpleframework.xml.stream.InputNode;
import org.simpleframework.xml.stream.OutputNode;

import android.content.Context;
import android.util.Log;

import com.threed.jpct.SimpleVector;

public class StateHandler {
	public final static String TAG = "StateHandler";
	public final static String GAME_STATE_FILE = "gameState.xml";
	
	public static void saveGameState(GameState state, Context context) {
		Serializer serializer = new Persister();

		try {
			FileOutputStream outFile = context.openFileOutput(GAME_STATE_FILE, 0);
			serializer.write(state, outFile);
		} catch (Exception e) {
			Log.w(TAG, "Could not save game data!");
		}
	}
	
	public static GameState loadGameState(Context context) {
		Serializer serializer = new Persister();
		FileInputStream inFile = null;
		
		try {
			inFile = context.openFileInput(GAME_STATE_FILE);
			GameState state = serializer.read(GameState.class, inFile);
			return state;
		} catch (Exception e) {
			Log.i(TAG, "No valid saved game found. ");
		}
		
		return null;
	}
	
	class VectorConverter implements Converter<SimpleVector> {

		   public SimpleVector read(InputNode node) throws Exception {
		      float x = Float.parseFloat(node.getAttribute("x").getValue());
		      float y = Float.parseFloat(node.getAttribute("y").getValue());
		      float z = Float.parseFloat(node.getAttribute("z").getValue());

		      return new SimpleVector(x, y, z);
		   }

		   public void write(OutputNode node, SimpleVector vector) {

		      node.setAttribute("x", Float.toString(vector.x));
		      node.setAttribute("y", Float.toString(vector.y));
		      node.setAttribute("z", Float.toString(vector.z));
		   }
		}

}
