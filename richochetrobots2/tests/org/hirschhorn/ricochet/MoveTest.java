package org.hirschhorn.ricochet;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class MoveTest {

  @Test
  public void testAsMovesString() {
    Game game = new Game();
    game.createInitialState(0);
    Move parentMove = game.getRootPosition();
    Move move = game.createChildMove(parentMove, Color.Blue, Direction.South);
    move = game.createChildMove(move, Color.Blue, Direction.West);
    move = game.createChildMove(move, Color.Red, Direction.South);
    move = game.createChildMove(move, Color.Blue, Direction.North);
    
    String expected = "Red(0,15) Green(15,0) Blue(0,0) Yellow(15,15).  Move 1: Blue South to (0,5) --> Move 2: Blue West to (0,5) --> Move 3: Red South to (0,15) --> Move 4: Blue North to (0,0)";
    assertEquals(expected, move.toString());
  }

}
