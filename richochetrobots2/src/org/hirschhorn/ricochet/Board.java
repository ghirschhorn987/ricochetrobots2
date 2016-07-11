package org.hirschhorn.ricochet;

import java.util.List;
import java.util.Map;

public class Board {
  
  private Map<Target, Position> targetsToPositions;
  private List<BoardItem> boardItems;
  
  public Board(Map<Target, Position> targetsToPositions, List<BoardItem> boardItems) { 
    this.targetsToPositions = targetsToPositions;
    this.boardItems = boardItems;
  }
  
  public boolean hasWall(Position position, Direction direction) {
    BoardItem boardItem = getBoardItem(position);
    switch(direction) {
      case North:
        return boardItem.hasNorthWall();
      case East:
        return boardItem.hasEastWall();
      case South:
        return boardItem.hasSouthWall(); 
      case West:
        return boardItem.hasWestWall();
      default:
        throw new AssertionError("unknown direction: " + direction);    
    }
  }

  public BoardItem getBoardItem(Position position) {
    for (BoardItem boardItem : boardItems) {
      if (boardItem.getPosition().equals(position)) {
        return boardItem;
      }
    }
    throw new AssertionError ("Invalid position: " + position);
  }

  public Position getTargetPosition(Target target) {
    return targetsToPositions.get(target);
  }

  List<BoardItem> getBoardItems() {
    return boardItems;
  }
  
//Board (walls, target positions)

}
