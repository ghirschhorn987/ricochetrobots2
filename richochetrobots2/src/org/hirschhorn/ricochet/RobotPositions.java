package org.hirschhorn.ricochet;

import java.util.ArrayList;
import java.util.List;

public class RobotPositions {

	private List<Position> positions;

	public static class Builder {
		private List<Position> positions;

		public Builder() {
			positions = new ArrayList<>();
			for (int i = 0; i < Color.values().length; i++) {
				positions.add(null);
			}
		}

		public Builder(RobotPositions robotPositions) {
			this();
			for (Color color : Color.values()) {
			  setRobotPosition(color, robotPositions.getRobotPosition(color));
			}
		}

		public Builder setRobotPosition(Color robot, Position position) {
			int index = indexOfColor(robot);
			positions.set(index, position);
			return this;
		}

		public RobotPositions build() {
			return new RobotPositions(positions);
		}

		public Position getRobotPosition(Color robot) {
			return positions.get(indexOfColor(robot));
		}
	}

	private RobotPositions(List<Position> positions) {
		for (int i = 0; i < positions.size(); i++) {
			Position position = positions.get(i);
			if (position == null) {
//				throw new IllegalArgumentException(
//						"Cannot create RobotPositions because all positions are not specified: " + positions);
				positions.set(i, Position.of(-1, -1));
			}
		}
		this.positions = new ArrayList<>(positions);
	}

	// Makes a copy of RobotPositions
	public RobotPositions(RobotPositions robotPositions) {
		this(robotPositions.positions);
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
		return positions.get(indexOfColor(robot));
	}

	public String asRobotPositionsString() {
		return String.format("%s(%s) %s(%s) %s(%s) %s(%s)",
				Color.Red,
				getRobotPosition(Color.Red).asSimpleString(),
				Color.Green,
				getRobotPosition(Color.Green).asSimpleString(),
				Color.Blue,
				getRobotPosition(Color.Blue).asSimpleString(),
				Color.Yellow,
				getRobotPosition(Color.Yellow).asSimpleString());
	}

	// the positions list is: Red, Yellow, Green, Blue
	public static int compressRobotToPosition(List<Position> expandedPositions) {
		int compressedPositions = 0;
		for (int i = 0; i < expandedPositions.size(); i++) {
			Position position = expandedPositions.get(i);
			int iOfX = i * 2;
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

}
