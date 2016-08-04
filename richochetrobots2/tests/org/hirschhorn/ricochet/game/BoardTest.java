package org.hirschhorn.ricochet.game;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.hirschhorn.ricochet.board.BoardItem;
import org.hirschhorn.ricochet.board.Color;
import org.hirschhorn.ricochet.board.Position;
import org.hirschhorn.ricochet.board.Shape;
import org.hirschhorn.ricochet.board.Target;
import org.junit.Before;
import org.junit.Test;

public class BoardTest {

  private Board board;
  
  @Before
  public void setUp() {
    LinkedHashMap<Target, Position> targetsToPositions = new LinkedHashMap<>();
    Target target = Target.getTarget(Color.Blue, Shape.Moon);
    targetsToPositions.put(target,  Position.of(1, 5));
    
    List<BoardItem> boardItems = new ArrayList<>();
    BoardItem boardItem = (new BoardItem(Position.of(1, 4))).setEastWall(true).setNorthWall(true);
    boardItems.add(boardItem);

    board = new Board(targetsToPositions, boardItems);    
  }

  @Test
  public void targetPositionShouldBeAsExpected() {
    Target target = Target.getTarget(Color.Blue, Shape.Moon);
    assertEquals(Position.of(1, 5), board.getTargetPosition(target));
  }

  
  @Test
  public void boardItemPositionShouldBeAsExpected() {
    BoardItem boardItem = board.getBoardItem(Position.of(1, 4));
    assertTrue(boardItem.hasEastWall());
    assertTrue(boardItem.hasNorthWall());
    assertFalse(boardItem.hasWestWall());
    assertFalse(boardItem.hasSouthWall());
  }

}
