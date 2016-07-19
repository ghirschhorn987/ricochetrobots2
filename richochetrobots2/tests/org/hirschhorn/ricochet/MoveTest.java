package org.hirschhorn.ricochet;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

public class MoveTest {

  @Test
  public void testAsMovesString() {
    List<Position> robotPositions = BoardState.createEmptyPositionList();
    robotPositions.set(Color.Red.ordinal(), Position.of(0, 1));
    robotPositions.set(Color.Green.ordinal(), Position.of(7, 0));
    robotPositions.set(Color.Blue.ordinal(), Position.of(0, 0));
    robotPositions.set(Color.Yellow.ordinal(),  Position.of(7, 7));    
    BoardState boardState = new BoardState(Target.getTarget(Color.Blue,  Shape.Moon), robotPositions);
    Move move = new Move(null, boardState, null);
    move = new Move(move, boardState, new MoveAction(Color.Red, Direction.East, 7));
    robotPositions.set(Color.Red.ordinal(), Position.of(7, 1));
    move = new Move(move, boardState, new MoveAction(Color.Blue, Direction.South, 7));
    robotPositions.set(Color.Blue.ordinal(), Position.of(0, 7));
    move = new Move(move, boardState, new MoveAction(Color.Blue, Direction.North, 7));
    robotPositions.set(Color.Blue.ordinal(), Position.of(0, 0));
       
    String expected = "Red(0,1) Green(7,0) Blue(0,0) Yellow(7,7).  Move 1: Red East to (7,1) --> Move 2: Blue South to (0,7) --> Move 3: Blue North to (0,0)";
    assertEquals(expected, move.toString());
  }

}
