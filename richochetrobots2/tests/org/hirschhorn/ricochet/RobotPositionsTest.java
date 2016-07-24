package org.hirschhorn.ricochet;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class RobotPositionsTest {

  private static final int[] ALL_MIN = {0, 0, 0, 0, 0, 0, 0, 0};
  private static final int[] ALL_SEVENS = {7, 7, 7, 7, 7, 7, 7, 7};
  private static final int[] ALL_EIGHTS = {8, 8, 8, 8, 8, 8, 8, 8};
  private static final int[] ALL_MAX = {15, 15, 15, 15, 15, 15, 15, 15};
  
  private static final int[] ALL_MIN_FIRST_SEVEN = {7, 0, 0, 0, 0, 0, 0, 0};
  private static final int[] ALL_MIN_FIRST_EIGHT = {8, 0, 0, 0, 0, 0, 0, 0};
  private static final int[] ALL_MIN_FIRST_MAX = {15, 0, 0, 0, 0, 0, 0, 0};

  private static final int[] ALL_MIN_LAST_SEVEN = {0, 0, 0, 0, 0, 0, 0, 7};
  private static final int[] ALL_MIN_LAST_EIGHT = {0, 0, 0, 0, 0, 0, 0, 8};
  private static final int[] ALL_MIN_LAST_MAX = {0, 0, 0, 0, 0, 0, 0, 15};

  private static final int[] ALL_MAX_FIRST_MIN = {0, 15, 15, 15, 15, 15, 15, 15};
  private static final int[] ALL_MAX_FIRST_SEVEN = {7, 15, 15, 15, 15, 15, 15, 15};
  private static final int[] ALL_MAX_FIRST_EIGHT = {8, 15, 15, 15, 15, 15, 15, 15};

  private static final int[] ALL_MAX_LAST_MIN = {15, 15, 15, 15, 15, 15, 15, 0};
  private static final int[] ALL_MAX_LAST_SEVEN = {15, 15, 15, 15, 15, 15, 15, 7};
  private static final int[] ALL_MAX_LAST_EIGHT = {15, 15, 15, 15, 15, 15, 15, 8};

  private static final int[] MIN_TO_SEVEN = {0, 1, 2, 3, 4, 5, 6, 7};
  private static final int[] ONE_TO_EIGHT = {1, 2, 3, 4, 5, 6, 7, 8};
  private static final int[] SEVEN_TO_MIN = {7, 6, 5, 4, 3, 2, 1, 0};
  private static final int[] EIGHT_TO_ONE = {8, 7, 6, 5, 4, 3, 2, 1};
  
  
  @Test
  public void testCompressPositions() {     
    int compressed = RobotPositions.compressRobotPositions(buildRobotPositionsFromIntArray(ALL_MIN));
    assertEquals(0, compressed);
    compressed = RobotPositions.compressRobotPositions(buildRobotPositionsFromIntArray(ALL_MIN_LAST_SEVEN));
    assertEquals(7, compressed);
    compressed = RobotPositions.compressRobotPositions(buildRobotPositionsFromIntArray(ALL_MIN_LAST_EIGHT));
    assertEquals(Integer.MIN_VALUE, compressed);
    compressed = RobotPositions.compressRobotPositions(buildRobotPositionsFromIntArray(ALL_MIN_LAST_MAX));
    assertEquals(Integer.MIN_VALUE + 7, compressed);

    compressed = RobotPositions.compressRobotPositions(buildRobotPositionsFromIntArray(ALL_MAX_LAST_MIN));
    assertEquals(Integer.MAX_VALUE - 7, compressed);
    compressed = RobotPositions.compressRobotPositions(buildRobotPositionsFromIntArray(ALL_MAX_LAST_SEVEN));
    assertEquals(Integer.MAX_VALUE, compressed);
    compressed = RobotPositions.compressRobotPositions(buildRobotPositionsFromIntArray(ALL_MAX_LAST_EIGHT));
    assertEquals(-8, compressed);
    compressed = RobotPositions.compressRobotPositions(buildRobotPositionsFromIntArray(ALL_MAX));
    assertEquals(-1, compressed);
  }
  
  @Test
	public void testCompressThenExpandIsUnchanged() { 
    assertCompressThenExpandIsUnchanged(ALL_MIN);
    assertCompressThenExpandIsUnchanged(ALL_SEVENS);
    assertCompressThenExpandIsUnchanged(ALL_EIGHTS);
    assertCompressThenExpandIsUnchanged(ALL_MAX);
    assertCompressThenExpandIsUnchanged(ALL_MIN_FIRST_SEVEN);
    assertCompressThenExpandIsUnchanged(ALL_MIN_FIRST_EIGHT);
    assertCompressThenExpandIsUnchanged(ALL_MIN_FIRST_MAX);
    assertCompressThenExpandIsUnchanged(ALL_MIN_LAST_SEVEN);
    assertCompressThenExpandIsUnchanged(ALL_MIN_LAST_EIGHT);
    assertCompressThenExpandIsUnchanged(ALL_MIN_LAST_MAX);
    assertCompressThenExpandIsUnchanged(ALL_MAX_FIRST_MIN);
    assertCompressThenExpandIsUnchanged(ALL_MAX_FIRST_SEVEN);
    assertCompressThenExpandIsUnchanged(ALL_MAX_FIRST_EIGHT);
    assertCompressThenExpandIsUnchanged(ALL_MAX_LAST_MIN);
    assertCompressThenExpandIsUnchanged(ALL_MAX_LAST_SEVEN);
    assertCompressThenExpandIsUnchanged(ALL_MAX_LAST_EIGHT);
    assertCompressThenExpandIsUnchanged(MIN_TO_SEVEN);
    assertCompressThenExpandIsUnchanged(ONE_TO_EIGHT);
    assertCompressThenExpandIsUnchanged(SEVEN_TO_MIN);
    assertCompressThenExpandIsUnchanged(EIGHT_TO_ONE);
	}

  private void assertCompressThenExpandIsUnchanged(int[] intArray) {
    RobotPositions original = buildRobotPositionsFromIntArray(intArray);
    int compressed = RobotPositions.compressRobotPositions(original);
    RobotPositions expanded = RobotPositions.expandRobotPositions(compressed);
    assertEquals(original, expanded);
  }
  
  private RobotPositions buildRobotPositionsFromIntArray(int[] intArray) {
    return (new RobotPositions.Builder())
        .setRobotPosition(RobotPositions.colorOfIndex(0), Position.of(intArray[0], intArray[1]))
        .setRobotPosition(RobotPositions.colorOfIndex(1), Position.of(intArray[2], intArray[3]))
        .setRobotPosition(RobotPositions.colorOfIndex(2), Position.of(intArray[4], intArray[5]))
        .setRobotPosition(RobotPositions.colorOfIndex(3), Position.of(intArray[6], intArray[7]))
         .build();
  }

}
