package org.hirschhorn.ricochet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Move {

  private List<Move> children;
  private Move parent;
  private int depth;
  private BoardState boardState;
  private MoveAction moveAction;
  private Potential potential;
  
  public Potential getPotential() {
	return potential;
}

public Move(Move parent, BoardState boardState, MoveAction moveAction) {
    this.parent = parent;
    if (parent == null) {
      depth = 0;
    } else {
      depth = parent.getDepth() + 1;
    }
    this.moveAction = moveAction;
    this.boardState = boardState;
    children = new ArrayList<>();
    potential = new Potential(); 
  }
  
  public void addChildren(List<Move> nextMoves) {
    children.addAll(nextMoves);
  }

  public BoardState getBoardState() {
    return boardState;
  }

  public int getDepth() {
    return depth;
  }

  public List<Move> getChildren() {
    return children;
  }
  
  public Move getParent(){
    return parent;
  }
  
  public MoveAction getMoveAction(){
    return moveAction;
  }

  public Target getChosenTarget() {
    return boardState.getChosenTarget();
  }
  
  public String toString() {
    return asMovesString();
  }
  
  public String asMovesString() {
    StringBuilder sb = new StringBuilder();
    int moveNum = 0;
    for (Move move : getAncestorsFromRootDownToSelf()) {      
      MoveAction moveAction = move.moveAction;
      if (moveAction == null) {
        sb.append(move.boardState.asRobotPositionsString());
      } else {
        moveNum++;
        if (moveNum == 1) {
          sb.append(".  ");
        } else {        
          sb.append(" --> ");
        }
        sb.append("Move ");
        sb.append(moveNum);
        sb.append(": ");
        sb.append(moveAction.getRobot());
        sb.append(" ");
        sb.append(moveAction.getDirection());
        sb.append(" to (");
        sb.append(move.boardState.getRobotPosition(moveAction.getRobot()).asSimpleString());
        sb.append(")");
      }
    }
    return sb.toString();
  }

  public List<Move> getAncestorsFromParentUp() {
    List<Move> ancestors = new ArrayList<>();
    Move ancestor = parent;
    while (ancestor != null) {
      ancestors.add(ancestor);
      ancestor = ancestor.parent;
    }
    return ancestors;
  }

  public List<Move> getAncestorsFromSelfUp() {
    List<Move> ancestors = getAncestorsFromParentUp();
    ancestors.add(0, this);
    return ancestors;
  }
  
  public List<Move> getAncestorsFromRootDownToParent() {
    List<Move> ancestors = getAncestorsFromParentUp();
    Collections.reverse(ancestors);
    return ancestors;
  }
  
  public List<Move> getAncestorsFromRootDownToSelf() {
    List<Move> ancestors = getAncestorsFromSelfUp();
    Collections.reverse(ancestors);
    return ancestors;
  }

  public static Comparator<Move> getPotentialComparator() {
	return new Comparator<Move>(){
		@Override
		public int compare(Move move1, Move move2) {
			return move1.getPotential().compareTo(move2.getPotential());
		}
	};
  }
  
  
}
