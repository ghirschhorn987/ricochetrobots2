package org.hirschhorn.ricochet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BoardState {

  private Target chosenTarget;
  private List<Position> robotPositions;
  
  public BoardState(Target chosenTarget, List<Position> robotPositions) {
    this.chosenTarget = chosenTarget;
    this.robotPositions = robotPositions;
  }
  
  // Makes a copy of BoardState
  public BoardState(BoardState boardState) {
    this.chosenTarget = boardState.chosenTarget;
    this.robotPositions = new ArrayList<>();
    for (Position position : boardState.robotPositions) {
      this.robotPositions.add(Position.of(position.getX(),  position.getY()));
    }
  }
  
  public Position getRobotPosition(Color robot) {
    return robotPositions.get(robot.ordinal());
  }

  public void setRobotPosition(Color robot, Position robotPosition) {
    robotPositions.set(robot.ordinal(), robotPosition);
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

  
  //the positions list is: Red, Yellow, Green, Blue
  public static int compressRobotToPosition(List<Position> expandedPositions) {
	 int compressedPositions = 0;
	 for (int i = 0; i < expandedPositions.size(); i++) {
		 Position position = expandedPositions.get(i);
		 int iOfX = i*2;
		 int iOfY = iOfX + 1;
		 compressedPositions += (position.getX() * Math.pow(16, iOfX));
		 compressedPositions += (position.getY() * Math.pow(16, iOfY));
	 }
	 return compressedPositions;
  }
  
  public static List<Position> expandRobotToPosition(int compressedPositions) {
	 List<Position> expandedPositions = new ArrayList<>();
     for (int i = 0; i < 4; i++) { 
        int xValue = compressedPositions % 16;
        compressedPositions /= 16;
        int yValue = compressedPositions % 16;
        compressedPositions /= 16;
		expandedPositions.add(Position.of(xValue, yValue));
     }
     return expandedPositions;
  }

	public static List<Position> createEmptyPositionList() {
	    List<Position> positions = new ArrayList<>();
	    positions.add(Position.of(0, 0));
	    positions.add(Position.of(0, 0));	    
	    positions.add(Position.of(0, 0));	    
	    positions.add(Position.of(0, 0));
	    return positions;
	}
}
