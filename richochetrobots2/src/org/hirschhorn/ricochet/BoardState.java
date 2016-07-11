package org.hirschhorn.ricochet;

import java.util.HashMap;
import java.util.Map;

public class BoardState {

  private Target chosenTarget;
  private Map<Color, Position> robotToPosition;
  
  public BoardState(Target chosenTarget, Map<Color, Position> robotToPosition) {
    this.chosenTarget = chosenTarget;
    this.robotToPosition = robotToPosition;
  }
  
  // Makes a copy of BoardState
  public BoardState(BoardState boardState) {
    this.chosenTarget = boardState.chosenTarget;
    this.robotToPosition = new HashMap<>(boardState.robotToPosition);
  }
  
  public Position getRobotPosition(Color robot) {
    return robotToPosition.get(robot);
  }

  public void setRobotPosition(Color robot, Position robotPosition) {
    robotToPosition.put(robot, robotPosition);
  }
  
  public Target getChosenTarget(){
    return chosenTarget;
  }

  public String asRobotPositionsString() {
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

}
