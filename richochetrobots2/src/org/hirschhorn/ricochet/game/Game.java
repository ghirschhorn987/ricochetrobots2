package org.hirschhorn.ricochet.game;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;

import org.hirschhorn.ricochet.board.Target;

public class Game implements Serializable {
  
  private Board board;
  private BoardState boardState;
  private List<Target> unusedTargets;
  private int round;
  private Phase phase;
  private Map<String, Integer> playersToGuesses;
  private List<String> playerIds;
  private String playerToMove;

  public String getPlayerToMove() {
    return playerToMove;
  }

  public Game(Board board, BoardState boardState) {
    this.board = board;
    this.boardState = boardState;
    playerIds = new ArrayList<>();
    playersToGuesses = new LinkedHashMap<>();
    unusedTargets = Target.buildAllTargets();
    playerToMove = null;
  }

  public Board getBoard() {
    return board;
  }

  public BoardState getBoardState() {
    return boardState;
  }

  public void updateBoardState(BoardState newBoardState) {
    boardState = newBoardState;
  }
  
  public List<Target> getUnusedTargets() {
    return unusedTargets;
  }
  
  public void removeTarget(Target target){
    unusedTargets.remove(target);
  }

  public void addGuessToMap(String playerId, int guess) {
    if (!playersToGuesses.containsKey(playerId) || playersToGuesses.get(playerId) > guess) {
      playersToGuesses.put(playerId, guess);
    }
  }
  
  public void addToPlayerIds(String playerId){
    playerIds.add(playerId);
  }
  
  public Phase getPhase() {
    return phase;
  }

  public void setPhase(Phase phase) {
    this.phase = phase;
  }

  private void transitionToSolvingPhase() {
    if (!phase.equals(Phase.GUESSING)) {
      throw new AssertionError("Cannot transition to solving from " + phase);
    }
    setPhase(Phase.SOLVING);
    setAndRemoveNextPlayerToMove();
  }
  
  public void setAndRemoveNextPlayerToMove() {
    int lowestGuess = Integer.MAX_VALUE;
    String candidate = null;
    for(Entry<String, Integer> entry : playersToGuesses.entrySet()) {
      if (entry.getValue() < lowestGuess) {
        lowestGuess = entry.getValue();
        candidate = entry.getKey();
      }
    }
    playersToGuesses.remove(candidate);
    playerToMove = candidate;
  }

  public void startCountdownToChangePhase() {
    int interval = 30000; // 30 sec
    Date timeToRun = new Date(System.currentTimeMillis() + interval);
    Timer timer = new Timer();
     
    timer.schedule(new TimerTask() {
            public void run() {
               transitionToSolvingPhase();
            }
        }, timeToRun);
  }

  public boolean isFirstGuess() {
    return playersToGuesses.isEmpty();
  }

  public boolean playerSurpassedGuess() {
    // TODO Auto-generated method stub
    return false;
  }

  public void goToNextRound() {
    // TODO Auto-generated method stub
    
  }
  
}
