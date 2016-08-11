package org.hirschhorn.ricochet.game;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hirschhorn.ricochet.board.Target;

public class Game implements Serializable {
  
  private Board board;
  private BoardState boardState;
  private List<Target> unusedTargets;
  private int round;
  private int phase;
  private Map<String, Integer> guesses;
  private List<String> playerIds;

  public Game(Board board, BoardState boardState) {
    this.board = board;
    this.boardState = boardState;
    playerIds = new ArrayList<>();
    guesses = new HashMap<>();
    unusedTargets = Target.buildAllTargets();
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
  
  public void addToPlayerIds(String playerId){
    playerIds.add(playerId);
  }
  
  public int getPhase() {
    return phase;
  }

  public void setPhase(int phase) {
    this.phase = phase;
  }
  
}
