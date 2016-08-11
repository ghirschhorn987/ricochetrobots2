package org.hirschhorn.ricochet.updateevent;

import org.hirschhorn.ricochet.game.UpdateEventData;

public class PlayerAddedEventData implements UpdateEventData{
  private String playerId;
  
  public PlayerAddedEventData(String playerId) {
    super();
    this.playerId = playerId;

  }

  public String getPlayerId() {
    return playerId;
  }
}
