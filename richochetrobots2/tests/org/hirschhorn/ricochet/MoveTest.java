package org.hirschhorn.ricochet;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class MoveTest {

  @Test
  public void testAsMovesString() {
    Map<Color, Position> robotToPosition = new HashMap<>();
    robotToPosition.put(Color.Red,  Position.of(0, 1));
    robotToPosition.put(Color.Green,  Position.of(7, 0));
    robotToPosition.put(Color.Blue,  Position.of(0, 0));
    robotToPosition.put(Color.Yellow,  Position.of(7, 7));    
    BoardState boardState = new BoardState(Target.getTarget(Color.Blue,  Shape.Moon), robotToPosition );
    Move move = new Move(null, boardState, null);
    move = new Move(move, boardState, new MoveAction(Color.Red, Direction.East, 7));
    robotToPosition.put(Color.Red, Position.of(7, 1));
    move = new Move(move, boardState, new MoveAction(Color.Blue, Direction.South, 7));
    robotToPosition.put(Color.Blue, Position.of(0, 7));
    move = new Move(move, boardState, new MoveAction(Color.Blue, Direction.North, 7));
    robotToPosition.put(Color.Blue, Position.of(0, 0));
       
    String expected = "Red(0,1) Green(7,0) Blue(0,0) Yellow(7,7).  Move 1: Red East to (7,1) --> Move 2: Blue South to (0,7) --> Move 3: Blue North to (0,0)";
    assertEquals(expected, move.toString());
  }

}
