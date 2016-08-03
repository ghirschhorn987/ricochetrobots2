package org.hirschhorn.ricochet.solver;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.hirschhorn.ricochet.board.BoardItem;
import org.hirschhorn.ricochet.board.Color;
import org.hirschhorn.ricochet.board.Direction;
import org.hirschhorn.ricochet.board.Position;
import org.hirschhorn.ricochet.board.Shape;
import org.hirschhorn.ricochet.board.Target;
import org.hirschhorn.ricochet.game.Board;
import org.hirschhorn.ricochet.game.BoardState;
import org.hirschhorn.ricochet.game.Move;
import org.hirschhorn.ricochet.game.RobotPositions;
import org.junit.Before;
import org.junit.Test;

public class SolverTest {
  
  private static Logger logger = Logger.getLogger(SolverTest.class.getName());

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
    
    MoveNode rootMove = new MoveNode(null, boardState, null);

    Solver solver = new Solver(board, rootMove, UnprocessedMovesType.BREADTH_FIRST_SEARCH);
    
    MoveNode parentMove = solver.getRootMove();
    MoveNode actualMove = solver.createChildMoveNode(parentMove, Color.Blue, Direction.South);
    robotPositions = new RobotPositions.Builder();
    robotPositions.setRobotPosition(Color.Blue, Position.of(0, 5));
    
    BoardState expectedBoardState = new BoardState(target, robotPositions.build());
    Move expectedMoveAction = new Move(Color.Blue, Direction.South, 5);
    MoveNode expectedMove = new MoveNode(parentMove, expectedBoardState, expectedMoveAction);
    assertEquals(expectedMove.toString(), actualMove.toString());
  }
  
  @Test 
  public void createChildMoveShouldCreateChildMove2() {
    Target target = Target.getTarget(Color.Red, Shape.Moon);
    RobotPositions.Builder robotPositions = new RobotPositions.Builder();
    robotPositions.setRobotPosition(Color.Red, Position.of(3, 7));
    BoardState boardState = new BoardState(target, robotPositions.build());
    
    Solver solver = (new SolverFactory()).createSolver(0, UnprocessedMovesType.BREADTH_FIRST_SEARCH);
    MoveNode rootMove = new MoveNode(null, boardState, null);
    MoveNode actualMove = solver.createChildMoveNode(rootMove, Color.Red, Direction.North);
    Move expectedMoveAction = new Move(Color.Red, Direction.North, 2);
    MoveNode expectedMove = new MoveNode(rootMove, boardState, expectedMoveAction);
    assertEquals(expectedMove.toString(), actualMove.toString());   
  }
  
  @Test
  public void createChildMovesShouldContainCorrectMoves() {
    Solver solver = (new SolverFactory()).createSolver(2, UnprocessedMovesType.BREADTH_FIRST_SEARCH);
    MoveNode rootMove = solver.getRootMove();
    
    int moveNum = 1;
    
    Move expectedMoveAction = new Move(Color.Red, Direction.East, 5);
    MoveNode nextMove = testNextMoveInChain(solver, rootMove, expectedMoveAction, moveNum++);
    
    expectedMoveAction = new Move(Color.Red, Direction.North, 6);
    nextMove = testNextMoveInChain(solver, nextMove, expectedMoveAction, moveNum++);
    
    expectedMoveAction = new Move(Color.Yellow, Direction.North, 6);
    nextMove = testNextMoveInChain(solver, nextMove, expectedMoveAction, moveNum++);
    
    expectedMoveAction = new Move(Color.Red, Direction.East, 9);
    nextMove = testNextMoveInChain(solver, nextMove, expectedMoveAction, moveNum++);
    
    expectedMoveAction = new Move(Color.Red, Direction.North, 7);
    nextMove = testNextMoveInChain(solver, nextMove, expectedMoveAction, moveNum++);
    
    expectedMoveAction = new Move(Color.Red, Direction.West, 12);
    nextMove = testNextMoveInChain(solver, nextMove, expectedMoveAction, moveNum++);
    
    expectedMoveAction = new Move(Color.Red, Direction.South, 6);
    nextMove = testNextMoveInChain(solver, nextMove, expectedMoveAction, moveNum++);
    
    expectedMoveAction = new Move(Color.Red, Direction.East, 2);
    nextMove = testNextMoveInChain(solver, nextMove, expectedMoveAction, moveNum++);
    
    expectedMoveAction = new Move(Color.Red, Direction.North, 7);
    nextMove = testNextMoveInChain(solver, nextMove, expectedMoveAction, moveNum++);
    
  }

  private MoveNode testNextMoveInChain(Solver solver, MoveNode previousMove, Move expectedMoveAction, int moveNumber) {
    List<MoveNode> nextMoves = solver.createNextMoveNodes(previousMove);
    MoveNode nextMove = getMatchingMove(nextMoves, expectedMoveAction);
    if (nextMove == null) {
      logger.severe("Potential Moves " + nextMoves);
      fail("NextMoves does not contain expectedMoveAction: " + expectedMoveAction + " at move number " + moveNumber);
    }
    return nextMove;
  }
  
  private MoveNode getMatchingMove(List<MoveNode> nextMoves, Move expectedMoveAction) {
    for (MoveNode moveNode : nextMoves) {
      Move actualMoveAction = moveNode.getMove();
      if (actualMoveAction.equals(expectedMoveAction)) {
        return moveNode;
      }
    }
    return null;
  }
  
  
  
}
 