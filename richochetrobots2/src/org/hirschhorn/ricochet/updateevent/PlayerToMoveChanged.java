package org.hirschhorn.ricochet.updateevent;

import org.hirschhorn.ricochet.game.UpdateEventData;

public class PlayerToMoveChanged implements UpdateEventData {
  
  String player = null;
  
  public PlayerToMoveChanged(String player){
    this.player = player;
  }
  
}
