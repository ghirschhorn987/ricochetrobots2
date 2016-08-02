package org.hirschhorn.ricochet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.logging.Logger;

public class Game {

  private static final int NUMBER_OF_ITERATIONS = 15;
  private static final int START_ITERATION = 0;
  private static final boolean PAUSE_BEFORE_PLAY = false;
  private static final int MAX_DEPTH = 15;
  private static final int MAX_WINNERS = 1;

  private static Logger logger = Logger.getLogger(Game.class.getName());
  private Move rootMove;
  private Board board;
  private MoveStats moveStats;
  private Set<Integer> boardStateCache;
  private UnprocessedMovesType unprocessedMovesType;

  public static void main(String[] args) throws IOException {

    GameFactory gameFactory = new GameFactory();
    UnprocessedMovesType movesType = UnprocessedMovesType.BREADTH_FIRST_SEARCH;
    
    Move previousWinner = null;
    for (int iteration = START_ITERATION; iteration <= NUMBER_OF_ITERATIONS; iteration++) {
      logger.info("");
      logger.severe("======================================");
      logger.severe("NEW GAME. Iteration " + iteration);  
      logger.info("======================================");
      RobotPositions robotPositions = null;
//      RobotPositions robotPositions = (previousWinner == null) ? null : previousWinner.getBoardState().getRobotPositions();
      Game game = gameFactory.createGame(iteration % 16, robotPositions, movesType);
      
      BoardState boardState = game.getRootMove().getBoardState();
      logger.severe("Target: " + boardState.getChosenTarget() + " at position "
              + game.getBoard().getTargetPosition(boardState.getChosenTarget())
              + ". Robots: " + boardState.asRobotPositionsString());
      
      if (PAUSE_BEFORE_PLAY) {
        logger.severe("Press any key to continue...");
        (new Scanner(System.in)).nextLine();
        //logger.severe("Running...");
      }
      
      List<Move> winners = game.play();
      previousWinner = winners.get(0);
    }
  }

  public Game(Board board, Move rootMove, UnprocessedMovesType unprocessedMovesType) {
    this.board = board;
    this.rootMove = rootMove;
    this.unprocessedMovesType = unprocessedMovesType;

    moveStats = new MoveStats(MAX_DEPTH, MAX_WINNERS);
    boardStateCache = new HashSet<>();
  }

  public Move getRootMove() {
    return rootMove;
  }

  public Board getBoard() {
    return board;
  }

  public List<Move> play() throws IOException {
    moveStats.playStarted();
    UnprocessedMoves unprocessedMoves = UnprocessedMovesFactory.newUnprocessedMoves(unprocessedMovesType);
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
    RobotPositions robotPositions = parentMove.getBoardState().getRobotPositions();
    Position oldPosition = robotPositions.getRobotPosition(robot); 
    Position newPosition = MoveCalculator.calculateRobotPosition(parentMove.getBoardState(), board, robot, direction);
    int spacesMoved = Math.abs(newPosition.getX() - oldPosition .getX()) + Math.abs(newPosition.getY() - oldPosition.getY());
    
    MoveAction moveAction = new MoveAction(robot, direction, spacesMoved);    
    RobotPositions newRobotPositions = (new RobotPositions.Builder(robotPositions)).setRobotPosition(robot, newPosition).build();
    BoardState childBoardState = new BoardState(parentMove.getChosenTarget(), newRobotPositions);
    return new Move(parentMove, childBoardState, moveAction);
  }
  
}
