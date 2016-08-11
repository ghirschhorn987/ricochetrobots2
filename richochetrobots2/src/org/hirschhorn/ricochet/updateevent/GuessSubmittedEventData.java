package org.hirschhorn.ricochet.updateevent;

import org.hirschhorn.ricochet.game.UpdateEventData;

public class GuessSubmittedEventData implements UpdateEventData {
  private String guesserId;
  private int guess;
  
  public GuessSubmittedEventData(String guesserId, int guess) {
    super();
    this.guesserId = guesserId;
    this.guess = guess;
  }
  
  public String getGuesserId() {
    return guesserId;
  }

  public int getGuess() {
    return guess;
  }
}
