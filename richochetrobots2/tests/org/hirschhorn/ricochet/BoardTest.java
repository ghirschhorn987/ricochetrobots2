package org.hirschhorn.ricochet;

import static org.junit.Assert.*;

import org.hirschhorn.ricochet.BoardItem;
import org.hirschhorn.ricochet.Game;
import org.junit.Test;

public class BoardTest {

  @Test
  public void boardItemsShouldNotHaveBothEastAndWestWall() {
    Game game = new Game();
    game.createInitialState();
    for (BoardItem boardItem : game.getBoard().getBoardItems()) {
      if (boardItem.hasEastWall()) {
        assertFalse(boardItem.toString(), boardItem.hasWestWall());
      }
      if (boardItem.hasWestWall()) {
        assertFalse(boardItem.toString(), boardItem.hasEastWall());
      }
    }
  }

  @Test
  public void boardItemsShouldNotHaveBothNorthAndSouthWall() {
    Game game = new Game();
    game.createInitialState();
    for (BoardItem boardItem : game.getBoard().getBoardItems()) {
      if (boardItem.hasNorthWall()) {
        assertFalse(boardItem.toString(), boardItem.hasSouthWall());
      }
      if (boardItem.hasSouthWall()) {
        assertFalse(boardItem.toString(), boardItem.hasNorthWall());
      }
    }
  }
}
