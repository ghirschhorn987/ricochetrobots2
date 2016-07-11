package org.hirschhorn.ricochet;

public class BoardItem {
  
  private Position position;
  private boolean hasNorthWall;
  private boolean hasSouthWall;
  private boolean hasEastWall;
  private boolean hasWestWall;

  public BoardItem(Position position) {
    this.position = position;
  }

  public boolean hasNorthWall() {
    return hasNorthWall;
  }

  public boolean hasEastWall() {
    return hasEastWall;
  }

  public boolean hasSouthWall() {
    return hasSouthWall;
  }

  public boolean hasWestWall() {
    return hasWestWall;
  }

  public Position getPosition() {
    return position;
  }

  public BoardItem setEastWall(boolean b) {
    hasEastWall = b;
    return this;
  }
  
  public BoardItem setNorthWall(boolean b) {
    hasNorthWall = b;
    return this;
  }
  
  public BoardItem setWestWall(boolean b) {
    hasWestWall = b;
    return this;
  }
  
  public BoardItem setSouthWall(boolean b) {
    hasSouthWall = b;
    return this;
  }

  @Override
  public String toString() {
    return "BoardItem [position=" + position.asSimpleString() + ", hasNorthWall=" + hasNorthWall + ", hasSouthWall=" + hasSouthWall
        + ", hasEastWall=" + hasEastWall + ", hasWestWall=" + hasWestWall + "]";
  }  
}
