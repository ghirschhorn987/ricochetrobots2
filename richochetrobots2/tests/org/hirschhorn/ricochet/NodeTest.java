package org.hirschhorn.ricochet;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class NodeTest {

  @Test
  public void testAsMovesString() {
    Game game = new Game();
    game.createInitialState(0);
    Node parentNode = game.getRootPosition();
    Node node = game.createNewNode(parentNode, Color.Blue, Direction.South);
    node = game.createNewNode(node, Color.Blue, Direction.West);
    node = game.createNewNode(node, Color.Red, Direction.South);
    node = game.createNewNode(node, Color.Blue, Direction.North);
    
    String expected = "Red(0,15) Green(15,0) Blue(0,0) Yellow(15,15).  Move 1: Blue South to (0,5) --> Move 2: Blue West to (0,5) --> Move 3: Red South to (0,15) --> Move 4: Blue North to (0,0)";
    assertEquals(expected, node.toString());
  }

}
