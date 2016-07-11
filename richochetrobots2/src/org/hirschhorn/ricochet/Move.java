package org.hirschhorn.ricochet;

public class Move {

  private Color robot;
  private Direction direction;
  private int numberOfSpaces;
  
  public Move(Color robot, Direction direction, int numberOfSpaces) {
    this.robot = robot;
    this.direction = direction;
    this.numberOfSpaces = numberOfSpaces;
  }
  
  public String toString() {
    return String.format("Robot: %s, Direction: %s, NumberOfSpaces: %d", robot, direction, numberOfSpaces);
  }
  
  public Color getRobot() {
    return robot;
  }

  public Direction getDirection() {
    return direction;
  }

  public int getNumberOfSpaces() {
    return numberOfSpaces;
  }
  
}
