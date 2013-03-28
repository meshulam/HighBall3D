package net.meshlabs.yaam;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.convert.Convert;

import com.threed.jpct.SimpleVector;

@Root
public class GameState {
	@Element
	@Convert(StateHandler.VectorConverter.class)
	public SimpleVector marblePosition = new SimpleVector();
	
	@Element
	public float maxHeight = 0;
	
	@Element
	public int score = 0;
	public float fps = 0;

}