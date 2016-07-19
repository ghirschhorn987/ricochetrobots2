package org.hirschhorn.ricochet;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class BoardStateTest {

	@Test
	public void testCompressingPositions() {
	    List<Position> positions = new ArrayList<>();
	    positions.add(Position.of(5, 4));
	    positions.add(Position.of(7, 2));	    
	    positions.add(Position.of(4, 3));	    
	    positions.add(Position.of(1, 2));
//	    BoardState boardState = new BoardState(Target.getTarget(Color.Red, Shape.Sawblade), positions);
	    assertEquals(positions, BoardState.expandRobotToPosition(BoardState.compressRobotToPosition(positions)));
	}

}
