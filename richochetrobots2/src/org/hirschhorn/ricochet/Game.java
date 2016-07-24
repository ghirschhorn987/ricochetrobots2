package org.hirschhorn.ricochet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

public class Game {

  private static final int NUMBER_OF_ITERATIONS = 200;
  private static final int START_ITERATION = 0;
  private static final boolean PAUSE_BEFORE_PLAY = false;
  private static final int MAX_DEPTH = 15;
  private static final int MAX_WINNERS = 1;

  private static Logger logger = Logger.getLogger(Game.class.getName());
  private Move rootMove;
  private Board board;
  private MoveStats moveStats;
  private Set<Integer> boardStateCache;

  public static void main(String[] args) throws IOException {

    GameFactory gameFactory = new GameFactory();
    UnprocessedMoves unprocessedMoves = UnprocessedMovesFactory.newBreadthFirstUnprocessedMoves();
    //UnprocessedMoves unprocessedMoves = UnprocessedMovesFactory.newPriorityQueueUnprocessedMoves();
    
    Move previousWinner = null;
    for (int iteration = START_ITERATION; iteration <= NUMBER_OF_ITERATIONS; iteration++) {
      logger.info("");
      logger.severe("======================================");
      logger.severe("NEW GAME. Iteration " + iteration);  
      logger.info("======================================");
      RobotPositions robotPositions = (previousWinner == null) ? null : previousWinner.getBoardState().getRobotPositions();
      Game game = gameFactory.createGame(iteration % 16, robotPositions);
      unprocessedMoves.clear();

      List<Move> winners = game.play(unprocessedMoves);
      previousWinner = winners.get(0);
    }
  }

  public Game(Board board, Move rootMove) {
    this.board = board;
    this.rootMove = rootMove;

    moveStats = new MoveStats(MAX_DEPTH, MAX_WINNERS);
    boardStateCache = new HashSet<>();
  }

  Move getRootMove() {
    return rootMove;
  }

  Board getBoard() {
    return board;
  }

  private List<Move> play(UnprocessedMoves unprocessedMoves) throws IOException {
    logger.severe("Target: " + rootMove.getBoardState().getChosenTarget() + " at position "
            + board.getTargetPosition(rootMove.getBoardState().getChosenTarget())
            + ". Robots: " + rootMove.getBoardState().asRobotPositionsString());
    
    if (PAUSE_BEFORE_PLAY) {
      logger.severe("Press a key to start.");
      System.in.read();
      logger.severe("Running...");
    }

    unprocessedMoves.add(rootMove);

    while (!(unprocessedMoves.isEmpty())) {
      Move move = unprocessedMoves.removeFirst();
      List<Move> nextMoves = createNextMoves(move);
      
      Iterator<Move> moveIter = nextMoves.iterator();
      while (moveIter.hasNext()) {
        Move nextMove = moveIter.next();
        if (isWinner(nextMove)) {
          moveStats.winnerFound(nextMove);
        } else if (shouldContinue(nextMove)) {
          int compressedMove = RobotPositions.compressRobotPositions(nextMove.getBoardState().getRobotPositions());
          boardStateCache.add(compressedMove);
          nextMove.getPotential().adjustIfMoveSameColorAsTarget(nextMove);
          unprocessedMoves.add(nextMove);
        } else {
          // Don't add to parent or unprocessedMoves if we aren't going to continue
          moveIter.remove();
        }
      }

      move.addChildren(nextMoves);
      moveStats.moveProcessed(move, nextMoves);
        
      if (moveStats.maxWinnersReached()) {
        unprocessedMoves.clear();
      }

    }
    moveStats.printWinners();
    return moveStats.getWinners();
  }

  private boolean shouldContinue(Move nextMove) {
    if (nextMove.getDepth() >= MAX_DEPTH) {
      return false;
    }
    //TODO: FIX THIS -- commented out for testing
    if (noRobotsHaveMoved(nextMove)) {
       return false;
    }
    if (boardStateHasPreviouslyExisted(nextMove)) {
      return false;
    }
    return true;
  }

  
  private boolean boardStateHasPreviouslyExisted(Move move) {
    int compressedMove = RobotPositions.compressRobotPositions(move.getBoardState().getRobotPositions());
    return boardStateCache.contains(compressedMove);
  }

  private boolean noRobotsHaveMoved(Move move) {
    return move.getMoveAction().getNumberOfSpaces() == 0;
  }

  private boolean isWinner(Move nextMove) {
    BoardState boardState = nextMove.getBoardState();
    Target chosenTarget = boardState.getChosenTarget();
    Color targetColor = chosenTarget.getColor();
    return boardState.getRobotPosition(targetColor).equals(board.getTargetPosition(chosenTarget));
  }

  //@VisibleForTesting
  List<Move> createNextMoves(Move parentMove) {
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
      if (board.hasWall(robotPosition, direction)) {
        hitObject = true;
      } else {
        Position potentialPosition = getAdjacentPosition(robotPosition, direction);
        if (hasRobot(potentialPosition, robotPositionsBuilder)) {
          hitObject = true;
        } else {
          robotPosition = potentialPosition;
          numberOfSpaces++;
          // Is this really needed? If this is only robot moving, do we need to
          // update positions? Won't collide with self.
          robotPositionsBuilder.setRobotPosition(robot, robotPosition);
        }
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
