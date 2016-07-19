package org.hirschhorn.ricochet;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hirschhorn.ricochet.BoardState;
import org.hirschhorn.ricochet.Color;
import org.hirschhorn.ricochet.Direction;
import org.hirschhorn.ricochet.Game;
import org.hirschhorn.ricochet.MoveAction;
import org.hirschhorn.ricochet.Move;
import org.hirschhorn.ricochet.Position;
import org.hirschhorn.ricochet.Target;
import org.junit.Before;
import org.junit.Test;

public class GameTest {

  private Board board;
  
  @Before
  public void setUp() {
    Map<Target, Position> targetsToPositions = new HashMap<>();
    Target target = Target.getTarget(Color.Blue, Shape.Moon);
    targetsToPositions.put(target,  Position.of(0, 5));
    
    List<BoardItem> boardItems = new ArrayList<>();
    for (int x = 0; x < 8; x++) {
      for (int y = 0; y < 8; y++) {
    	BoardItem boardItem = new BoardItem(Position.of(x, y));
    	if (x == 0 && y == 5) {
          boardItem.setSouthWall(true);    		
    	}
        boardItems.add(boardItem);       
      }
    }
    board = new Board(targetsToPositions, boardItems);    
  } 
  
  @Test
  public void createChildMoveShouldCreateChildMove() {
    Target target = Target.getTarget(Color.Blue, Shape.Moon);
    RobotPositions.Builder robotPositions = new RobotPositions.Builder();
    robotPositions.setRobotPosition(Color.Blue, Position.of(0, 0));
    BoardState boardState = new BoardState(target, robotPositions.build());
    
    Move rootMove = new Move(null, boardState, null);

    Game game = new Game(board, rootMove);
    
    Move parentMove = game.getRootMove();
    Move actualMove = game.createChildMove(parentMove, Color.Blue, Direction.South);
    robotPositions = new RobotPositions.Builder();
    robotPositions.setRobotPosition(Color.Blue, Position.of(0, 5));
    
    BoardState expectedBoardState = new BoardState(target, robotPositions.build());
    MoveAction expectedMoveAction = new MoveAction(Color.Blue, Direction.South, 5);
    Move expectedMove = new Move(parentMove, expectedBoardState, expectedMoveAction);
    assertEquals(expectedMove.toString(), actualMove.toString());
  }
  
}
 