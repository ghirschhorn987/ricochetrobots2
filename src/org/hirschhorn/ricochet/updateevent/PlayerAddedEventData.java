package org.hirschhorn.ricochet.updateevent;

import java.util.ArrayList;
import java.util.List;

import org.hirschhorn.ricochet.game.UpdateEventData;

public class PlayerAddedEventData implements UpdateEventData{
  private List<PlayerAndScore> playersAndScores;
  
  public PlayerAddedEventData(List<PlayerAndScore> playersAndScores) {
    super();
    this.playersAndScores = playersAndScores;

  }

  public List<PlayerAndScore> getPlayersAndScores() {
    return playersAndScores;
  }
}
