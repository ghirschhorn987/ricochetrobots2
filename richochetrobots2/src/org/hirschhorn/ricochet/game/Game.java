package org.hirschhorn.ricochet.game;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.hirschhorn.ricochet.board.Target;

public class Game implements Serializable {
  
  private Board board;
  private BoardState boardState;
  private List<Target> unusedTargets = Target.buildAllTargets();
  private int round;
  private int phase;
  private Map<String, Integer> guesses;

  public Game(Board board, BoardState boardState) {
    this.board = board;
    this.boardState = boardState;
  }

  public Board getBoard() {
    return board;
  }

  public BoardState getBoardState() {
    return boardState;
  }

  public void updateBoardState(BoardState newBoardState) {
    this.boardState = newBoardState;
  }
  
  public List<Target> getUnusedTargets() {
    return unusedTargets;
  }
  
  public void removeTarget(Target target){
    unusedTargets.remove(target);
  }

  public void addGuessToMap(String playerId, int guess) {
    guesses.put(playerId, guess);
  }
}
