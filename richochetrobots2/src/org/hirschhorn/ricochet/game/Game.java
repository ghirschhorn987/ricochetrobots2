package org.hirschhorn.ricochet.game;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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
  private int phase;
  private Map<String, Integer> playersToGuesses;
  private List<String> playerIds;
  private Map<Integer, String> firstGuessToPlayer;

  public Game(Board board, BoardState boardState) {
    this.board = board;
    this.boardState = boardState;
    playerIds = new ArrayList<>();
    playersToGuesses = new HashMap<>();
    unusedTargets = Target.buildAllTargets();
    firstGuessToPlayer = new HashMap<>();
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
    playersToGuesses.put(playerId, guess);
    if (!firstGuessToPlayer.containsKey(guess)){
      firstGuessToPlayer.put(guess, playerId);
    }
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

  public String getPlayerAllowedToMove() {
    int lowestGuess = Integer.MAX_VALUE;
    String playerToMove = null;
    for(Entry<String, Integer> entry : playersToGuesses.entrySet()) {
      if (entry.getValue() < lowestGuess) {
        lowestGuess = entry.getValue();
        playerToMove = entry.getKey();
      } else if (entry.getValue() == lowestGuess) {
        playerToMove = firstGuessToPlayer.get(lowestGuess);
      }
    }
    return playerToMove;
  }

  public void startCountdownToChangePhase() {
    int interval = 30000; // 30 sec
    Date timeToRun = new Date(System.currentTimeMillis() + interval);
    Timer timer = new Timer();
     
    timer.schedule(new TimerTask() {
            public void run() {
               setPhase(2);
            }
        }, timeToRun);
  }

  public boolean isFirstGuess() {
    return playersToGuesses.isEmpty();
  }
  
}
