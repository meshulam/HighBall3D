package meshlabs.hiball;

import com.threed.jpct.SimpleVector;

/**
 * Holds non-physics-related game state. Also keeps the marble's coordinates for convenience.
 *
 */
public class GameState {
	public SimpleVector marblePosition = new SimpleVector();
	
	public int maxHeightScore = 0;
	
	public int score = 0;
	public int fps = 0;

}
