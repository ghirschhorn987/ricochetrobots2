package org.hirschhorn.ricochet;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

public class Game {

  private static final int MAX_DEPTH = 7;
  private static final int MAX_WINNER_SIZE = 1;

  private static Logger logger = Logger.getLogger(Game.class.getName());
  private Move rootMove;
  private Board board;
  private MoveStats moveStats;

  public static void main(String[] args) {

    GameFactory gameFactory = new GameFactory();
    UnprocessedMoves unprocessedMoves = UnprocessedMovesFactory.newBreadthFirstUnprocessedMoves();
    // UnprocessedMoves unprocessedMoves =
    // UnprocessedMovesFactory.newPriorityQueueUnprocessedMoves();

    for (int iteration = 1; iteration <= 1; iteration++) {
      logger.info("======================================");
      Game game = gameFactory.createGame(iteration);
      unprocessedMoves.clear();
      game.play(unprocessedMoves);
    }
  }

  public Game(Board board, Move rootMove) {
    this.board = board;
    this.rootMove = rootMove;

    moveStats = new MoveStats(MAX_DEPTH);
  }

  Move getRootMove() {
    return rootMove;
  }

  Board getBoard() {
    return board;
  }

  private void play(UnprocessedMoves unprocessedMoves) {
    List<Move> winners = new ArrayList<>();

    logger.info("Target: " + rootMove.getBoardState().getChosenTarget() + " at position "
            + board.getTargetPosition(rootMove.getBoardState().getChosenTarget()));

    unprocessedMoves.add(rootMove);

    while (!(unprocessedMoves.isEmpty())) {
      Move move = unprocessedMoves.removeFirst();
      List<Move> nextMoves = createNextMoves(move);
      
      Iterator<Move> moveIter = nextMoves.iterator();
      while (moveIter.hasNext()) {
        Move nextMove = moveIter.next();
        if (isWinner(nextMove)) {
          winners.add(nextMove);
        } else if (shouldContinue(nextMove)) {
          nextMove.getPotential().adjustIfMoveSameColorAsTarget(nextMove);
          unprocessedMoves.add(nextMove);
        } else {
          // Don't add to parent or unprocessedMoves if we aren't going to continue
          moveIter.remove();
        }
      }

      move.addChildren(nextMoves);
      moveStats.moveProcessed(move, nextMoves);
      
      
      if (winners.size() >= MAX_WINNER_SIZE) {
        unprocessedMoves.clear();
      }

    }

    printMoves(winners);
  }

  private boolean shouldContinue(Move nextMove) {
    if (nextMove.getDepth() >= MAX_DEPTH) {
      return false;
    }
    //TODO: FIX THIS -- commented out for testing
    //    if (noRobotsHaveMoved(nextMove)) {
    //      return false;
    //    }
    // if (boardStateHasPreviouslyExisted(nextMove)) {
    // return false;
    // }
    // if(nextMove.getParent() != null
    // &&
    // nextMove.getParent().getBoardState().getRobotPosition(nextMove.getMove().getRobot()).equals(Position.of(0,
    // 0))){
    // return true;
    // }
    // if (!nextMove.getMove().getRobot().equals(Color.Red)) {
    // return false;
    // }
    return true;
  }

  private boolean boardStateHasPreviouslyExisted(Move move) {
    // TODO: Need to implement
    return false;
  }

  private boolean noRobotsHaveMoved(Move move) {
    return move.getMoveAction().getNumberOfSpaces() == 0;
  }

  private void printMoves(List<Move> moves) {
    for (Move move : moves) {
      logger.info(move.asMovesString());
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
    int numberOfSpaces = 0;
    BoardState parentBoardState = parentMove.getBoardState();
    RobotPositions.Builder robotPositionsBuilder = new RobotPositions.Builder(parentBoardState.getRobotPositions());
    Position robotPosition = robotPositionsBuilder.getRobotPosition(robot);
    boolean hitObject = false;
    while (!hitObject) {
      Position potentialPosition = getAdjacentPosition(robotPosition, direction);
      if (hasRobot(potentialPosition, robotPositionsBuilder)) {
        hitObject = true;
      } else if (board.hasWall(robotPosition, direction)) {
        hitObject = true;
      } else {
        robotPosition = potentialPosition;
        numberOfSpaces++;
        // Is this really needed? If this is only robot moving, do we need to
        // update positions? Won't collide withself.
        robotPositionsBuilder.setRobotPosition(robot, robotPosition);
      }
    }

    MoveAction moveAction = new MoveAction(robot, direction, numberOfSpaces);
    BoardState childBoardState = new BoardState(parentMove.getChosenTarget(), robotPositionsBuilder.build());
    Move nextMove = new Move(parentMove, childBoardState, moveAction);
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

  private boolean hasRobot(Position potentialPosition, RobotPositions.Builder positions) {
    for (Color robot : Color.values()) {
      if (potentialPosition.equals(positions.getRobotPosition(robot))) {
        return true;
      }
    }
    return false;
  }

}
