package org.hirschhorn.ricochet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Game {

  public static final int MAX_DEPTH = 10;
  public static final int MAX_Y = 16;
  public static final int MAX_X = 16;
  private static final int MAX_WINNER_SIZE = 1;

  private Move rootPosition;
  private Board board;

  public static void main(String[] args) {
    for (int i = 0; i <= 15; i++) {
      System.out.println("======================================");
      Game game = new Game();
      game.createInitialState(i);
      game.play();
    }
  }

  Move getRootPosition() {
    return rootPosition;
  }

  Board getBoard() {
    return board;
  }

  private void play() {
    List<Move> winners = new ArrayList<>();
    UnprocessedMoves movesToProcess = new UnprocessedMoves(UnprocessedMoves.SearchMode.BFS);
    movesToProcess.add(rootPosition);
    int lastDepth = -1;
    System.out.println("Target: " + rootPosition.getBoardState().getChosenTarget() + " at position "
        + board.getTargetPosition(rootPosition.getBoardState().getChosenTarget()));
    while (winners.size() < MAX_WINNER_SIZE && !(movesToProcess.isEmpty())) {
      Move move = movesToProcess.removeFirst();
      if (move.getDepth() != lastDepth) {
//        System.out.println(move);
        System.out.println(move.getDepth());
//        System.out.println("Winners size: " + winners.size());
        lastDepth = move.getDepth();
      }
      List<Move> nextMoves = getNextMoves(move);
      move.addChildren(nextMoves);
      for (Move nextMove : nextMoves) {
        if (isWinner(nextMove)) {
          winners.add(nextMove);
        } else if (shouldContinue(nextMove)) {
          movesToProcess.add(nextMove);
        }
      }
    }
    printMoves(winners);
  }

  private boolean shouldContinue(Move nextMove) {
    if (nextMove.getDepth() > MAX_DEPTH) {
      return false;
    }
    if (noRobotsHaveMoved(nextMove)) {
      return false;
    }
    if (boardStateHasPreviouslyExisted(nextMove)) {
      return false;
    }
//    if(nextMove.getParent() != null 
//        && nextMove.getParent().getBoardState().getRobotPosition(nextMove.getMove().getRobot()).equals(Position.of(0, 0))){
//      return true;
//    }
//    if (!nextMove.getMove().getRobot().equals(Color.Red)) {
//      return false;
//    }
    return true;
  }

  private boolean boardStateHasPreviouslyExisted(Move move) {
   // TODO: Need to implement
    return false;
  }

  private boolean noRobotsHaveMoved(Move move) {
    return move.getMove().getNumberOfSpaces() == 0;
  }

  private void printMoves(List<Move> moves) {
    for (Move move : moves) {
      System.out.println(move.asMovesString());
    }
  }

  private boolean isWinner(Move nextMove) {
    BoardState boardState = nextMove.getBoardState();
    Target chosenTarget = boardState.getChosenTarget();
    Color targetColor = chosenTarget.getColor();
    return boardState.getRobotPosition(targetColor).equals(board.getTargetPosition(chosenTarget));
  }

  private List<Move> getNextMoves(Move parentMove) {
    List<Move> nextMoves = new ArrayList<>();
    for (Color color : Color.values()) {
      for (Direction direction : Direction.values()) {
        Move nextMove = createChildMove(parentMove, color, direction);
        nextMoves.add(nextMove);
      }
    }
    return nextMoves;
  }

  Move createChildMove(Move parentMove, Color robot, Direction direction) {
    BoardState currentBoardState = new BoardState(parentMove.getBoardState());
    int numberOfSpaces = 0;
    Position robotPosition = parentMove.getBoardState().getRobotPosition(robot);
    boolean hitObject = false;
    while (!hitObject) {
      Position potentialPosition = getAdjacentPosition(robotPosition, direction);
      if (hasRobot(potentialPosition, currentBoardState)) {
        hitObject = true;
      } else if (board.hasWall(robotPosition, direction)) {
        hitObject = true;
      } else {
        robotPosition = potentialPosition;
        numberOfSpaces++;
        currentBoardState.setRobotPosition(robot, robotPosition);
      }
    }
    MoveAction moveAction = new MoveAction(robot, direction, numberOfSpaces);
    Move nextMove = new Move(parentMove, currentBoardState, moveAction);
    return nextMove;
  }

  private Position getAdjacentPosition(Position robotPosition, Direction direction) {
    int newX = robotPosition.getX();
    int newY = robotPosition.getY();
    switch (direction) {
    case North:
      newY--;
      break;
    case South:
      newY++;
      break;
    case East:
      newX++;
      break;
    case West:
      newX--;
      break;
    default:
      throw new AssertionError("Unknown direction: " + direction);
    }
    Position adjacentPosition = Position.of(newX, newY);
    return adjacentPosition;
  }

  private boolean hasRobot(Position potentialPosition, BoardState currentBoardState) {
    for (Color robot : Color.values()) {
      if (potentialPosition.equals(currentBoardState.getRobotPosition(robot))) {
        return true;
      }
    }
    return false;
  }

  void createInitialState(int iteration) {
    board = new Board(createTargetsToPositions(), createBoardItems());
    rootPosition = new Move(null, createInitialBoardState(iteration), null);
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

  private BoardState createInitialBoardState(int iteration) {
    return new BoardState(selectChosenTarget(iteration), createRobotsToPositions());
  }

  private Map<Color, Position> createRobotsToPositions() {
    Map<Color, Position> map = new HashMap<>();
    map.put(Color.Blue, Position.of(0, 0));
    map.put(Color.Red, Position.of(0, (MAX_Y - 1)));
    map.put(Color.Green, Position.of((MAX_X - 1), 0));
    map.put(Color.Yellow, Position.of((MAX_X - 1), (MAX_Y - 1)));
    return map;
  }

  private Target selectChosenTarget(int position) {
    return Target.getTargets().get(position);
  }

}
