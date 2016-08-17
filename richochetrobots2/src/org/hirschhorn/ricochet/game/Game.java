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
  private long guessingPhaseCountdownStartedTime;

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
    guessingPhaseCountdownStartedTime = 0;
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

  public synchronized void startCountdownToChangePhase() {
    //because its already started and only one person can start it
    if(guessingPhaseCountdownStartedTime != 0){
      return;
    }
    guessingPhaseCountdownStartedTime = System.currentTimeMillis();
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

  public synchronized long updateGuessingPhaseCountdown() {
    if(guessingPhaseCountdownStartedTime == 0) {
      return -1;
    }
    long millisecondsElapsed = System.currentTimeMillis() - guessingPhaseCountdownStartedTime;
    if(millisecondsElapsed > 30000) {
      transitionToSolvingPhase();
      guessingPhaseCountdownStartedTime = 0;
    }
    return millisecondsElapsed;
  }
  
}
