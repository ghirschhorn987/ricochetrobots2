package org.hirschhorn.ricochet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameFactory {

  public static final int MAX_Y = 16;
  public static final int MAX_X = 16;
  
  public Game createGame(int iteration) {
    Board board = new Board(createTargetsToPositions(), createBoardItems());
    Move rootMove = new Move(null, createInitialBoardState(iteration), null);
    Game game = new Game(board, rootMove);
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
  
  private BoardState createInitialBoardState(int iteration) {
    return new BoardState(chooseTarget(iteration), createRobotsToPositions());
  }

  private Target chooseTarget(int position) {
    return Target.getTargets().get(position);
  }
  
  private Map<Color, Position> createRobotsToPositions() {
    Map<Color, Position> map = new HashMap<>();
    map.put(Color.Blue, Position.of(0, 0));
    map.put(Color.Red, Position.of(0, (MAX_Y - 1)));
    map.put(Color.Green, Position.of((MAX_X - 1), 0));
    map.put(Color.Yellow, Position.of((MAX_X - 1), (MAX_Y - 1)));
    return map;
  }

}
