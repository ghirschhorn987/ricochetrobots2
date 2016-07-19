package org.hirschhorn.ricochet;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class RobotPositionsTest {

  @Test
  public void testCompressingPositions() {
      RobotPositions originalPositions = (new RobotPositions.Builder())
              .setRobotPosition(Color.Red, Position.of(5, 4))
              .setRobotPosition(Color.Yellow, Position.of(7, 2))
              .setRobotPosition(Color.Green, Position.of(4, 3))
              .setRobotPosition(Color.Blue, Position.of(1, 2))
              .build();

      int compressedPositions = RobotPositions.compressRobotPositions(originalPositions);
      RobotPositions expandedPositions = RobotPositions.expandRobotPositions(compressedPositions);
      assertEquals(originalPositions, expandedPositions);
  }
  
  @Test
	public void testCompressingPositionsWithLargePositions() {
	    RobotPositions originalPositions = (new RobotPositions.Builder())
	            .setRobotPosition(Color.Red, Position.of(15, 14))
              .setRobotPosition(Color.Yellow, Position.of(8, 12))
              .setRobotPosition(Color.Green, Position.of(14, 13))
              .setRobotPosition(Color.Blue, Position.of(11, 12))
              .build();

	    int compressedPositions = RobotPositions.compressRobotPositions(originalPositions);
      RobotPositions expandedPositions = RobotPositions.expandRobotPositions(compressedPositions);
	    assertEquals(originalPositions, expandedPositions);
	}

}
