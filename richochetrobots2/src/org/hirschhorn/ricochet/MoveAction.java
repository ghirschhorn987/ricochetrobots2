package org.hirschhorn.ricochet;

public class MoveAction {

  private Color robot;
  private Direction direction;
  private int numberOfSpaces;
  
  public MoveAction(Color robot, Direction direction, int numberOfSpaces) {
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

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((direction == null) ? 0 : direction.hashCode());
    result = prime * result + numberOfSpaces;
    result = prime * result + ((robot == null) ? 0 : robot.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    MoveAction other = (MoveAction) obj;
    if (direction != other.direction)
      return false;
    if (numberOfSpaces != other.numberOfSpaces)
      return false;
    if (robot != other.robot)
      return false;
    return true;
  }
  
  
  
}
