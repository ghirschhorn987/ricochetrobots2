package org.hirschhorn.ricochet;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

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
  
  private static Logger logger = Logger.getLogger(GameTest.class.getName());

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
  
  @Test 
  public void createChildMoveShouldCreateChildMove2() {
    Target target = Target.getTarget(Color.Red, Shape.Moon);
    RobotPositions.Builder robotPositions = new RobotPositions.Builder();
    robotPositions.setRobotPosition(Color.Red, Position.of(3, 7));
    BoardState boardState = new BoardState(target, robotPositions.build());
    
    Game game = (new GameFactory()).createGame(0);
    Move rootMove = new Move(null, boardState, null);
    Move actualMove = game.createChildMove(rootMove, Color.Red, Direction.North);
    MoveAction expectedMoveAction = new MoveAction(Color.Red, Direction.North, 2);
    Move expectedMove = new Move(rootMove, boardState, expectedMoveAction);
    assertEquals(expectedMove.toString(), actualMove.toString());   
  }
  
  @Test
  public void createChildMovesShouldContainCorrectMoves() {
    Game game = (new GameFactory()).createGame(2);
    Move rootMove = game.getRootMove();
    
    int moveNum = 1;
    
    MoveAction expectedMoveAction = new MoveAction(Color.Red, Direction.East, 5);
    Move nextMove = testNextMoveInChain(game, rootMove, expectedMoveAction, moveNum++);
    
    expectedMoveAction = new MoveAction(Color.Red, Direction.North, 6);
    nextMove = testNextMoveInChain(game, nextMove, expectedMoveAction, moveNum++);
    
    expectedMoveAction = new MoveAction(Color.Yellow, Direction.North, 6);
    nextMove = testNextMoveInChain(game, nextMove, expectedMoveAction, moveNum++);
    
    expectedMoveAction = new MoveAction(Color.Red, Direction.East, 9);
    nextMove = testNextMoveInChain(game, nextMove, expectedMoveAction, moveNum++);
    
    expectedMoveAction = new MoveAction(Color.Red, Direction.North, 7);
    nextMove = testNextMoveInChain(game, nextMove, expectedMoveAction, moveNum++);
    
    expectedMoveAction = new MoveAction(Color.Red, Direction.West, 12);
    nextMove = testNextMoveInChain(game, nextMove, expectedMoveAction, moveNum++);
    
    expectedMoveAction = new MoveAction(Color.Red, Direction.South, 6);
    nextMove = testNextMoveInChain(game, nextMove, expectedMoveAction, moveNum++);
    
    expectedMoveAction = new MoveAction(Color.Red, Direction.East, 2);
    nextMove = testNextMoveInChain(game, nextMove, expectedMoveAction, moveNum++);
    
    expectedMoveAction = new MoveAction(Color.Red, Direction.North, 7);
    nextMove = testNextMoveInChain(game, nextMove, expectedMoveAction, moveNum++);
    
  }

  private Move testNextMoveInChain(Game game, Move previousMove, MoveAction expectedMoveAction, int moveNumber) {
    List<Move> nextMoves = game.createNextMoves(previousMove);
    Move nextMove = getMatchingMove(nextMoves, expectedMoveAction);
    if (nextMove == null) {
      logger.severe("Potential Moves " + nextMoves);
      fail("NextMoves does not contain expectedMoveAction: " + expectedMoveAction + " at move number " + moveNumber);
    }
    return nextMove;
  }
  
  private Move getMatchingMove(List<Move> nextMoves, MoveAction expectedMoveAction) {
    for (Move move : nextMoves) {
      MoveAction actualMoveAction = move.getMoveAction();
      if (actualMoveAction.equals(expectedMoveAction)) {
        return move;
      }
    }
    return null;
  }
  
  
  
}
 