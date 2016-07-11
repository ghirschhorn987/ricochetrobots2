package org.hirschhorn.ricochet;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.hirschhorn.ricochet.BoardState;
import org.hirschhorn.ricochet.Color;
import org.hirschhorn.ricochet.Direction;
import org.hirschhorn.ricochet.Game;
import org.hirschhorn.ricochet.Move;
import org.hirschhorn.ricochet.Node;
import org.hirschhorn.ricochet.Position;
import org.hirschhorn.ricochet.Target;
import org.junit.Test;

public class GameTest {

  @Test
  public void createNewNodeShouldCreateNewNode() {
    Game game = new Game();
    game.createInitialState(0);
    
    Node parentNode = game.getRootPosition();
    Node actualNode = game.createNewNode(parentNode, Color.Blue, Direction.South);
    
    BoardState expectedBoardState = new BoardState(Target.getTargets().get(0), createRobotToPosition());
    Move expectedMove = new Move(Color.Blue, Direction.South, 14);
    Node expectedNode = new Node(parentNode, expectedBoardState, expectedMove);
    assertEquals(expectedNode.toString(), actualNode.toString());
  }

  private Map<Color, Position> createRobotToPosition() {
    Map<Color, Position> map = new HashMap<>();
    map.put(Color.Blue, Position.of(0, 0));
    map.put(Color.Red, Position.of(0, (Game.MAX_Y-1)));
    map.put(Color.Green, Position.of((Game.MAX_X -1), 0));
    map.put(Color.Yellow, Position.of((Game.MAX_X-1), (Game.MAX_Y-1)));
    return map;
  }  
  
}
 