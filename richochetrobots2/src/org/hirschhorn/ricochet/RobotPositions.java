package org.hirschhorn.ricochet;

import java.util.HashMap;
import java.util.Map;

public class RobotPositions {

  private Map<Color, Position> positions;
  // private List<Position> positions;

  public static class Builder {
    private Map<Color, Position> positions;
    // private List<Position> positions;

    public Builder() {
      positions = new HashMap<>();
      // positions = new ArrayList<>();
      // for (int i = 0; i < Color.values().length; i++) {
      // positions.add(null);
      // }
    }

    public Builder(RobotPositions robotPositions) {
      this();
      for (Color color : Color.values()) {
        setRobotPosition(color, robotPositions.getRobotPosition(color));
      }
    }

    public Builder setRobotPosition(Color robot, Position position) {
      positions.put(robot, position);
      // positions.set(indexOfColor(robot), position);
      return this;
    }

    public RobotPositions build() {
      return new RobotPositions(positions);
    }

    public Position getRobotPosition(Color robot) {
      return positions.get(robot);
      // return positions.get(indexOfColor(robot));
    }
  }

  private RobotPositions(Map<Color, Position> positions) {
    // private RobotPositions(List<Position> positions) {

    // while (positions.size() < Color.values().length) {
    // positions.add(null);
    // }

    for (Color color : Color.values()) {
      Position position = positions.get(color);
      // Position position = positions.get(indexOfColor(color));
      if (position == null) {
        // throw new IllegalArgumentException("Cannot create RobotPositions
        // because not all positions are specified: " + positions);
        positions.put(color, Position.of(-1, -1));
        // positions.set(index, Position.of(-1, -1));
      }
    }

    this.positions = new HashMap<>(positions);
    // this.positions = new ArrayList<>(positions);
  }

  // Makes a copy of RobotPositions
  public RobotPositions(RobotPositions robotPositions) {
    this(robotPositions.positions);
  }

  public int size() {
    return positions.size();
  }

  public static int indexOfColor(Color color) {
    switch (color) {
      case Red:
        return 0;
      case Yellow:
        return 1;
      case Green:
        return 2;
      case Blue:
        return 3;
      default:
        throw new AssertionError("Unknown color: " + color);
    }
  }

  public static Color colorOfIndex(int index) {
    switch (index) {
      case 0:
        return Color.Red;
      case 1:
        return Color.Yellow;
      case 2:
        return Color.Green;
      case 3:
        return Color.Blue;
      default:
        throw new AssertionError("Unknown index for color: " + index);
    }
  }

  public Position getRobotPosition(Color robot) {
    return positions.get(robot);
    // return positions.get(indexOfColor(robot));
  }

  public String toString() {
    return String.format(
        "%s(%s) %s(%s) %s(%s) %s(%s)",
        Color.Red,
        getRobotPosition(Color.Red).asSimpleString(),
        Color.Green,
        getRobotPosition(Color.Green).asSimpleString(),
        Color.Blue,
        getRobotPosition(Color.Blue).asSimpleString(),
        Color.Yellow,
        getRobotPosition(Color.Yellow).asSimpleString());
  }

  @Override
  public int hashCode() {
    return positions.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    return positions.equals(((RobotPositions)obj).positions);
  }

  // TODO: NEED TO FIX THIS. INT HAS RANGE OF MINUS 2 BILLION TO 2 BILLION, BUT
  // THIS METHOD PRODUCES VALUES FROM ZERO TO FOUR BILLION
  public static int compressRobotPositions(RobotPositions expandedPositions) {
    int compressedPositions = 0;
    for (int i = 0; i < expandedPositions.size(); i++) {
      Position position = expandedPositions.getRobotPosition(colorOfIndex(i));
      int iOfX = i * 2;
      int iOfY = iOfX + 1;
      compressedPositions += (position.getX() * Math.pow(16, iOfX));
      compressedPositions += (position.getY() * Math.pow(16, iOfY));
    }
    return compressedPositions;
  }

  // TODO(): NEED TO FIX THIS. SEE COMMENT IN compressRobotToPosition()
  public static RobotPositions expandRobotPositions(int compressedPositions) {
    RobotPositions.Builder expandedPositions = new RobotPositions.Builder();
    for (int i = 0; i < Color.values().length; i++) {
      int xValue = compressedPositions % 16;
      compressedPositions /= 16;
      int yValue = compressedPositions % 16;
      compressedPositions /= 16;
      expandedPositions.setRobotPosition(colorOfIndex(i), Position.of(xValue, yValue));
    }
    return expandedPositions.build();
  }

}
