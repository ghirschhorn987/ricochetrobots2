package org.hirschhorn.ricochet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

//Node (boardstate, move to get there, parent, children)



public class Node {

  private List<Node> children;
  private Node parent;
  private int depth;
  private BoardState boardState;
  private Move move;
  
  public Node(Node parent, BoardState boardState, Move move) {
    this.parent = parent;
    if (parent == null) {
      depth = 0;
    } else {
      depth = parent.getDepth() + 1;
    }
    this.move = move;
    this.boardState = boardState;
    children = new ArrayList<>();
  }
  
  public void addChildren(List<Node> nextMoves) {
    children.addAll(nextMoves);
  }

  public BoardState getBoardState() {
    return boardState;
  }

  public int getDepth() {
    return depth;
  }

  public List<Node> getChildren() {
    return children;
  }
  
  public Node getParent(){
    return parent;
  }
  
  public Move getMove(){
    return move;
  }

  public String toString() {
    return asMovesString();
  }
  
  public String asMovesString() {
    StringBuilder sb = new StringBuilder();
    int moveNum = 0;
    for (Node node : getAncestorsFromRootDownToSelf()) {      
      Move move = node.move;
      if (move == null) {
        sb.append(node.boardState.asRobotPositionsString());
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
        sb.append(move.getRobot());
        sb.append(" ");
        sb.append(move.getDirection());
        sb.append(" to (");
        sb.append(node.boardState.getRobotPosition(move.getRobot()).asSimpleString());
        sb.append(")");
      }
    }
    return sb.toString();
  }

  public List<Node> getAncestorsFromParentUp() {
    List<Node> ancestors = new ArrayList<>();
    Node ancestor = parent;
    while (ancestor != null) {
      ancestors.add(ancestor);
      ancestor = ancestor.parent;
    }
    return ancestors;
  }

  public List<Node> getAncestorsFromSelfUp() {
    List<Node> ancestors = getAncestorsFromParentUp();
    ancestors.add(0, this);
    return ancestors;
  }
  
  public List<Node> getAncestorsFromRootDownToParent() {
    List<Node> ancestors = getAncestorsFromParentUp();
    Collections.reverse(ancestors);
    return ancestors;
  }
  
  public List<Node> getAncestorsFromRootDownToSelf() {
    List<Node> ancestors = getAncestorsFromSelfUp();
    Collections.reverse(ancestors);
    return ancestors;
  }

  
  
}
