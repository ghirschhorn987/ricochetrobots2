package org.hirschhorn.ricochet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class Game {

  public static final int MAX_DEPTH = 2;
  public static final int MAX_Y = 16;
  public static final int MAX_X = 16;
  private static final int MAX_WINNER_SIZE = 10;
  
  private Node rootPosition;
  private Board board;  
  
  public static void main(String[] args) {
    Game game = new Game();
    game.createInitialState();
    game.play();
  }

  Node getRootPosition() {
    return rootPosition;
  }

  Board getBoard() {
    return board;
  }
  
  private void play() {
    List<Node> winners = new ArrayList<>();
    NodeCollection nodesToProcess = new NodeCollection();
    nodesToProcess.add(rootPosition);
      while (winners.size() <= MAX_WINNER_SIZE) {
        Node node = nodesToProcess.getFirst();
        List<Node> nextMoves = getNextMoves(node);
        node.addChildren(nextMoves);
        for (Node nextMove : nextMoves) {
          if (isWinner(nextMove)) {
            winners.add(nextMove);
          } else if (nextMove.getDepth() <= MAX_DEPTH) {
            nodesToProcess.add(nextMove);
          }
        }
        nodesToProcess.remove();
      }
    printNodes(winners);
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

  private List<Node> getNodesForDepth(int targetDepth) {
    List<Node> parentNodes = new ArrayList<>();
    List<Node> childrenNodes = null;
    parentNodes.add(rootPosition);
    if (targetDepth == 0) {
      return parentNodes;
    } else {
      for (int x=0; x < targetDepth; x++) {
        childrenNodes = new ArrayList<>();
        for (Node parentNode : parentNodes) {
          childrenNodes.addAll(parentNode.getChildren());
        }
        parentNodes = childrenNodes;
      }
    }  
    return childrenNodes;
  }
  
  void createInitialState() {
    board = new Board(createTargetsToPositions(), createBoardItems());
    rootPosition = new Node(null, createInitialBoardState(), null);
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
    Map<Target, Position> map = new HashMap<>();
    List<Target> targets = Target.getTargets();
    map.put(targets.get(0), Position.of(0, 0));
    return map;
  }

  private BoardState createInitialBoardState() {
    return new BoardState(selectChosenTarget(), createRobotsToPositions());
  }

  private Map<Color, Position> createRobotsToPositions() {
    Map<Color, Position> map = new HashMap<>();
    map.put(Color.Blue, Position.of(0, 0));
    map.put(Color.Red, Position.of(0, (MAX_Y-1)));
    map.put(Color.Green, Position.of((MAX_X-1), 0));
    map.put(Color.Yellow, Position.of((MAX_X-1), (MAX_Y-1)));
    return map;
  }

  private Target selectChosenTarget() {
    return Target.getTargets().get(0);
  }
  
  
  
	
}
