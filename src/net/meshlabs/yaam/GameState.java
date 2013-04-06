package net.meshlabs.yaam;

import java.util.ArrayList;
import java.util.List;

import com.threed.jpct.SimpleVector;

public class GameState {
	public SimpleVector marblePosition = new SimpleVector();
	
	public List<SimpleVector> apexes = new ArrayList<SimpleVector>();
	
	public int maxHeightScore = 0;
	
	public int score = 0;
	public int fps = 0;

}
