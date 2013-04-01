package net.meshlabs.yaam;

import java.util.ArrayList;
import java.util.List;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.convert.Convert;

import com.threed.jpct.SimpleVector;

@Root
public class GameState {
	@Element
	@Convert(StateHandler.VectorConverter.class)
	public SimpleVector marblePosition = new SimpleVector();
	
	public List<SimpleVector> apexes = new ArrayList<SimpleVector>();
	
	@Element
	public int maxHeightScore = 0;
	
	@Element
	public int score = 0;
	public int fps = 0;

}
