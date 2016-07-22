package org.hirschhorn.ricochet;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class MoveTest {

  @Test
  public void testAsMovesString() {
    RobotPositions robotPositions =
    	(new RobotPositions.Builder())
         .setRobotPosition(Color.Red, Position.of(0, 1))
        .setRobotPosition(Color.Green, Position.of(7, 0))
        .setRobotPosition(Color.Blue, Position.of(0, 0))
        .setRobotPosition(Color.Yellow,  Position.of(7, 7))
        .build();
    BoardState boardState = new BoardState(Target.getTarget(Color.Blue,  Shape.Moon), robotPositions);
    Move move = new Move(null, boardState, null);

    robotPositions = (new RobotPositions.Builder(robotPositions)).setRobotPosition(Color.Red, Position.of(7, 1)).build();
    boardState = new BoardState(Target.getTarget(Color.Blue,  Shape.Moon), robotPositions);
    move = new Move(move, boardState, new MoveAction(Color.Red, Direction.East, 7));
    
    robotPositions = (new RobotPositions.Builder(robotPositions)).setRobotPosition(Color.Blue, Position.of(0, 7)).build();
    boardState = new BoardState(Target.getTarget(Color.Blue,  Shape.Moon), robotPositions);
    move = new Move(move, boardState, new MoveAction(Color.Blue, Direction.South, 7));
    
    robotPositions = (new RobotPositions.Builder(robotPositions)).setRobotPosition(Color.Blue, Position.of(0, 0)).build();
    boardState = new BoardState(Target.getTarget(Color.Blue,  Shape.Moon), robotPositions);
    move = new Move(move, boardState, new MoveAction(Color.Blue, Direction.North, 7));
       
    String expected = "Red(0,1) Green(7,0) Blue(0,0) Yellow(7,7).  Move 1: Red East to (7,1) --> Move 2: Blue South to (0,7) --> Move 3: Blue North to (0,0)";
    assertEquals(expected, move.toString());
  }
  
  

}
