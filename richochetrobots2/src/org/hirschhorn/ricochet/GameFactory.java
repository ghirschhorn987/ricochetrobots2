package org.hirschhorn.ricochet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameFactory {

  public static final int MAX_Y = 16;
  public static final int MAX_X = 16;
  
  public Game createGame(int iteration, UnprocessedMovesType unprocessedMovesType) {
    return createGame(iteration, null, unprocessedMovesType);
  }

  public Game createGame(int iteration, RobotPositions robotPositions, UnprocessedMovesType unprocessedMovesType) {
    Board board = new Board(createTargetsToPositions(), createBoardItems());
    
    if (robotPositions == null) {
      robotPositions = createRobotsToPositions();
    }
    
    Move rootMove = new Move(null, createInitialBoardState(iteration, robotPositions), null);
    Game game = new Game(board, rootMove, unprocessedMovesType);
    return game;
  }
  
  private Map<Target, Position> createTargetsToPositions() {
    Map<Target, Position> targetsToPosition = new HashMap<>();
    BoardSection upperLeft = BoardSection.createBoardSectionA1();
    BoardSection upperRight = BoardSection.createBoardSectionB1().shiftRight();
    BoardSection bottomRight = BoardSection.createBoardSectionC1().shiftRight().shiftDown();
    BoardSection bottomLeft = BoardSection.createBoardSectionD1().shiftDown();
    
    targetsToPosition.putAll(upperLeft.getTargetsToPosition());
    targetsToPosition.putAll(upperRight.getTargetsToPosition());
    targetsToPosition.putAll(bottomRight.getTargetsToPosition());
    targetsToPosition.putAll(bottomLeft.getTargetsToPosition());

    return targetsToPosition;
  }

  private List<BoardItem> createBoardItems() {
    List<BoardItem> boardItems = new ArrayList<>();
    BoardSection upperLeft = BoardSection.createBoardSectionA1();
    BoardSection upperRight = BoardSection.createBoardSectionB1().shiftRight();
    BoardSection bottomRight = BoardSection.createBoardSectionC1().shiftRight().shiftDown();
    BoardSection bottomLeft = BoardSection.createBoardSectionD1().shiftDown();

    boardItems.addAll(upperLeft.getBoardItems());
    boardItems.addAll(upperRight.getBoardItems());
    boardItems.addAll(bottomRight.getBoardItems());
    boardItems.addAll(bottomLeft.getBoardItems());

    return boardItems;
  }
  
  private BoardState createInitialBoardState(int iteration, RobotPositions robotPositions) {
    return new BoardState(chooseTarget(iteration), robotPositions);
  }

  private Target chooseTarget(int position) {
    return Target.getTargets().get(position);
  }
  
  private RobotPositions createRobotsToPositions() {
    RobotPositions.Builder builder = new RobotPositions.Builder();
    builder.setRobotPosition(Color.Blue, Position.of(0, 0));
    builder.setRobotPosition(Color.Red, Position.of(0, (MAX_Y - 1)));
    builder.setRobotPosition(Color.Green, Position.of((MAX_X - 1), 0));
    builder.setRobotPosition(Color.Yellow, Position.of((MAX_X - 1), (MAX_Y - 1)));
    return builder.build();
  }

}
