package org.hirschhorn.ricochet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Game {

  private static final int MAX_DEPTH = 5;
  private static final int MAX_WINNER_SIZE = 1;

  private Move rootMove;
  private Board board;

  public static void main(String[] args) {
    GameFactory gameFactory = new GameFactory();
    for (int iteration = 0; iteration <= 15; iteration++) {
      System.out.println("======================================");
      Game game = gameFactory.createGame(iteration);
      game.play();
    }
  }

  public Game(Board board, Move rootMove) {
    this.board = board;
    this.rootMove = rootMove;
  }
  
  Move getRootMove() {
    return rootMove;
  }

  Board getBoard() {
    return board;
  }

  private void play() {
	  List<Move> winners = new ArrayList<>();
    int mostRecentDepth = -1;
    System.out.println("Target: " + rootMove.getBoardState().getChosenTarget() + " at position "
        + board.getTargetPosition(rootMove.getBoardState().getChosenTarget()));

    UnprocessedMoves unprocessedMoves = new UnprocessedMoves(UnprocessedMoves.SearchMode.BFS);
    unprocessedMoves.add(rootMove);

    int movesProcessed = 0;
    while (!(unprocessedMoves.isEmpty())) {
      Move move = unprocessedMoves.removeFirst();
      movesProcessed++;
      
      if (move.getDepth() != mostRecentDepth) {
//        System.out.println(move);
        System.out.println(move.getDepth());
//        System.out.println("Winners size: " + winners.size());
        mostRecentDepth = move.getDepth();
      }
      List<Move> nextMoves = createNextMoves(move);
      move.addChildren(nextMoves);
      for (Move nextMove : nextMoves) {
        if (isWinner(nextMove)) {
          winners.add(nextMove);
        } else if (shouldContinue(nextMove)) {
          unprocessedMoves.add(nextMove);
        }
      }
      if (winners.size() >= MAX_WINNER_SIZE) {
        unprocessedMoves.clear();
      }
    }

    System.out.println("movesProcessed: " + movesProcessed + ", depth: " + mostRecentDepth);
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

  private List<Move> createNextMoves(Move parentMove) {
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



  

}
