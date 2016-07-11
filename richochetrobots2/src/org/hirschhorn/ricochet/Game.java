package org.hirschhorn.ricochet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Game {

  public static final int MAX_DEPTH = 5;
  public static final int MAX_Y = 16;
  public static final int MAX_X = 16;
  private static final int MAX_WINNER_SIZE = 1;

  private Node rootPosition;
  private Board board;

  public static void main(String[] args) {
    for (int i = 0; i <= 15; i++) {
      System.out.println("======================================");
      Game game = new Game();
      game.createInitialState(i);
      game.play();
    }
  }

  Node getRootPosition() {
    return rootPosition;
  }

  Board getBoard() {
    return board;
  }

  private void play() {
    List<Node> winners = new ArrayList<>();
    NodeCollection nodesToProcess = new NodeCollection(NodeCollection.SearchMode.BFS);
    nodesToProcess.add(rootPosition);
    int lastDepth = -1;
    System.out.println("Target: " + rootPosition.getBoardState().getChosenTarget() + " at position "
        + board.getTargetPosition(rootPosition.getBoardState().getChosenTarget()));
    while (winners.size() < MAX_WINNER_SIZE && !(nodesToProcess.isEmpty())) {
      Node node = nodesToProcess.removeFirst();
      if (node.getDepth() != lastDepth) {
//        System.out.println(node);
        System.out.println(node.getDepth());
//        System.out.println("Winners size: " + winners.size());
        lastDepth = node.getDepth();
      }
      List<Node> nextMoves = getNextMoves(node);
      node.addChildren(nextMoves);
      for (Node nextMove : nextMoves) {
        if (isWinner(nextMove)) {
          winners.add(nextMove);
        } else if (shouldContinue(nextMove)) {
          nodesToProcess.add(nextMove);
        }
      }
    }
    printNodes(winners);
  }

  private boolean shouldContinue(Node nextMove) {
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

  private boolean boardStateHasPreviouslyExisted(Node move) {
   // TODO: Need to implement
    return false;
  }

  private boolean noRobotsHaveMoved(Node node) {
    return node.getMove().getNumberOfSpaces() == 0;
  }

  private void printNodes(List<Node> nodes) {
    for (Node node : nodes) {
      System.out.println(node.asMovesString());
    }
  }

  private boolean isWinner(Node nextMove) {
    BoardState boardState = nextMove.getBoardState();
    Target chosenTarget = boardState.getChosenTarget();
    Color targetColor = chosenTarget.getColor();
    return boardState.getRobotPosition(targetColor).equals(board.getTargetPosition(chosenTarget));
  }

  private List<Node> getNextMoves(Node parentNode) {
    List<Node> nextMoves = new ArrayList<>();
    for (Color color : Color.values()) {
      for (Direction direction : Direction.values()) {
        Node nextMove = createNewNode(parentNode, color, direction);
        nextMoves.add(nextMove);
      }
    }
    return nextMoves;
  }

  Node createNewNode(Node parentNode, Color robot, Direction direction) {
    BoardState currentBoardState = new BoardState(parentNode.getBoardState());
    int numberOfSpaces = 0;
    Position robotPosition = parentNode.getBoardState().getRobotPosition(robot);
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
    Move move = new Move(robot, direction, numberOfSpaces);
    Node nextMove = new Node(parentNode, currentBoardState, move);
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
    rootPosition = new Node(null, createInitialBoardState(iteration), null);
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
