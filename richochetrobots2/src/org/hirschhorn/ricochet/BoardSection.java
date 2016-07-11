package org.hirschhorn.ricochet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BoardSection {

  private static final int MAX_X = 8;
  private static final int MAX_Y = 8;
  private static final int SHIFT_SIZE = 8;
  
  private Map<Target, Position> targetsToPositions;
  private List<BoardItem> boardItems;
  
  public BoardSection(List<BoardItem> boardItems, Map<Target, Position> targetToPositions) {
    this.boardItems = boardItems;
    this.targetsToPositions = targetToPositions;
  }
  
  //upper left
  public static BoardSection createBoardSectionA1() {
    List<BoardItem> boardItems = new ArrayList<>();
    for (int x=0; x < MAX_X; x++) {
      for (int y=0; y < MAX_Y; y++) {
        boolean north = (y == 0);
        boolean south = false;
        boolean east = false;
        boolean west = (x == 0);
        BoardItem boardItem = (new BoardItem(Position.of(x, y))).setNorthWall(north).setSouthWall(south).setEastWall(east).setWestWall(west);
        boardItems.add(boardItem);
      }
    }
    get(0,5,boardItems).setSouthWall(true);
    get(0,6,boardItems).setNorthWall(true);
    get(1,0,boardItems).setEastWall(true);
    get(1,1,boardItems).setSouthWall(true);
    get(1,2,boardItems).setNorthWall(true).setEastWall(true);
    get(2,0,boardItems).setWestWall(true);
    get(2,2,boardItems).setWestWall(true);
    get(2,6,boardItems).setEastWall(true);
    get(3,1,boardItems).setEastWall(true);
    get(3,6,boardItems).setWestWall(true).setSouthWall(true);
    get(3,7,boardItems).setEastWall(true);
    get(4,0,boardItems).setSouthWall(true);
    get(4,1,boardItems).setEastWall(true).setNorthWall(true);
    get(6,3,boardItems).setSouthWall(true).setEastWall(true);
    get(6,4,boardItems).setNorthWall(true);
    get(6,7,boardItems).setWestWall(true);
    get(7,3,boardItems).setWestWall(true);
    get(7,6,boardItems).setSouthWall(true);
    get(7,7,boardItems).setNorthWall(true).setWestWall(true);
    return new BoardSection(boardItems, new HashMap<>());
  }
  
  //upper right
  public static BoardSection createBoardSectionB1() {
    List<BoardItem> boardItems = new ArrayList<>();
    for (int x=0; x < MAX_X; x++) {
      for (int y=0; y < MAX_Y; y++) {
        boolean north = (y == 0);
        boolean south = false;
        boolean east = (x == MAX_X - 1);
        boolean west = false;
        BoardItem boardItem = (new BoardItem(Position.of(x, y))).setNorthWall(north).setSouthWall(south).setEastWall(east).setWestWall(west);
        boardItems.add(boardItem);
      }
    }
    get(0,2,boardItems).setEastWall(true);
    get(0,6,boardItems).setSouthWall(true);
    get(0,7,boardItems).setNorthWall(true).setEastWall(true);
    get(1,1,boardItems).setWestWall(true).setSouthWall(true);
    get(1,2,boardItems).setNorthWall(true);
    get(1,7,boardItems).setEastWall(true);
    get(2,0,boardItems).setEastWall(true);
    get(2,4,boardItems).setSouthWall(true).setEastWall(true);
    get(2,5,boardItems).setNorthWall(true);
    get(3,0,boardItems).setWestWall(true);
    get(3,4,boardItems).setWestWall(true);
    get(3,6,boardItems).setEastWall(true);
    get(4,5,boardItems).setSouthWall(true);
    get(4,6,boardItems).setNorthWall(true).setWestWall(true);
    get(6,1,boardItems).setSouthWall(true);
    get(6,2,boardItems).setNorthWall(true).setEastWall(true);
    get(7,2,boardItems).setWestWall(true);
    get(7,4,boardItems).setSouthWall(true);
    get(7,5,boardItems).setNorthWall(true);
    return new BoardSection(boardItems, new HashMap<>());
  }
 
  //bottom right
  public static BoardSection createBoardSectionC1() {
    List<BoardItem> boardItems = new ArrayList<>();
    for (int x=0; x < MAX_X; x++) {
      for (int y=0; y < MAX_Y; y++) {
        boolean north = false;
        boolean south = (y == MAX_Y - 1);
        boolean east = (x == MAX_X - 1);
        boolean west = false;
        BoardItem boardItem = (new BoardItem(Position.of(x, y))).setNorthWall(north).setSouthWall(south).setEastWall(east).setWestWall(west);
        boardItems.add(boardItem);
      }
    }
    get(0,0,boardItems).setEastWall(true).setSouthWall(true);
    get(0,1,boardItems).setNorthWall(true).setSouthWall(true);
    get(0,2,boardItems).setNorthWall(true).setWestWall(true);
    get(1,0,boardItems).setWestWall(true);
    get(1,5,boardItems).setSouthWall(true);
    get(1,6,boardItems).setNorthWall(true).setEastWall(true);
    get(2,3,boardItems).setSouthWall(true).setEastWall(true);
    get(2,4,boardItems).setSouthWall(true);
    get(2,6,boardItems).setWestWall(true);
    get(3,3,boardItems).setWestWall(true);
    get(3,7,boardItems).setEastWall(true);
    get(4,2,boardItems).setEastWall(true);
    get(4,7,boardItems).setWestWall(true);
    get(5,1,boardItems).setSouthWall(true);
    get(5,2,boardItems).setNorthWall(true).setWestWall(true);
    get(5,4,boardItems).setEastWall(true);
    get(6,4,boardItems).setWestWall(true).setSouthWall(true);
    get(6,5,boardItems).setNorthWall(true);
    get(7,0,boardItems).setSouthWall(true);
    get(7,1,boardItems).setNorthWall(true);
    return new BoardSection(boardItems, new HashMap<>());
  }
  
  //bottom left
  public static BoardSection createBoardSectionD1() {
    List<BoardItem> boardItems = new ArrayList<>();
    for (int x=0; x < MAX_X; x++) {
      for (int y=0; y < MAX_Y; y++) {
        boolean north = false;
        boolean south = (y == MAX_Y - 1);
        boolean east = false;
        boolean west = (x == 0);
        BoardItem boardItem = (new BoardItem(Position.of(x, y))).setNorthWall(north).setSouthWall(south).setEastWall(east).setWestWall(west);
        boardItems.add(boardItem);
      }
    }
    get(0,3,boardItems).setSouthWall(true);
    get(0,4,boardItems).setNorthWall(true);
    get(1,1,boardItems).setEastWall(true);
    get(1,6,boardItems).setSouthWall(true).setEastWall(true);
    get(1,7,boardItems).setNorthWall(true);
    get(2,0,boardItems).setSouthWall(true);
    get(2,1,boardItems).setNorthWall(true).setWestWall(true);
    get(2,6,boardItems).setWestWall(true);
    get(4,0,boardItems).setEastWall(true);
    get(4,4,boardItems).setSouthWall(true);
    get(4,5,boardItems).setNorthWall(true).setEastWall(true);
    get(5,0,boardItems).setWestWall(true).setSouthWall(true);
    get(5,1,boardItems).setNorthWall(true);
    get(5,7,boardItems).setEastWall(true);
    get(6,0,boardItems).setEastWall(true);
    get(6,7,boardItems).setWestWall(true);
    get(7,0,boardItems).setWestWall(true).setSouthWall(true);
    get(7,1,boardItems).setNorthWall(true);
    return new BoardSection(boardItems, new HashMap<>());
  }
  
  private static BoardItem get(int x, int y, List<BoardItem> boardItems) {
    Position position = Position.of(x, y);
    for (BoardItem boardItem : boardItems) {
      if (boardItem.getPosition().equals(position)) {
        return boardItem;
      }
    }
    throw new AssertionError ("Invalid position: " + position);
  }

  public List<BoardItem> getBoardItems() {
    return new ArrayList<>(boardItems);
  }

  public BoardSection shiftRight() {
    List<BoardItem> shiftedBoardItems = new ArrayList<>();
    for (BoardItem boardItem : boardItems) {
      int shiftedX = boardItem.getPosition().getX() + SHIFT_SIZE;
      int y = boardItem.getPosition().getY();
      BoardItem shifted = new BoardItem(Position.of(shiftedX, y))
          .setNorthWall(boardItem.hasNorthWall())
          .setEastWall(boardItem.hasEastWall())
          .setSouthWall(boardItem.hasSouthWall())
          .setWestWall(boardItem.hasWestWall());
      shiftedBoardItems.add(shifted);
    }
    return new BoardSection(shiftedBoardItems, new HashMap<>());
  }
  
  public BoardSection shiftDown() {
    List<BoardItem> shiftedBoardItems = new ArrayList<>();
    for (BoardItem boardItem : boardItems) {
      int x = boardItem.getPosition().getX();
      int shiftedY = boardItem.getPosition().getY() + SHIFT_SIZE;
      BoardItem shifted = new BoardItem(Position.of(x, shiftedY))
          .setNorthWall(boardItem.hasNorthWall())
          .setEastWall(boardItem.hasEastWall())
          .setSouthWall(boardItem.hasSouthWall())
          .setWestWall(boardItem.hasWestWall());
      shiftedBoardItems.add(shifted);
    }
    return new BoardSection(shiftedBoardItems, new HashMap<>());
  }

}