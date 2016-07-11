package org.hirschhorn.ricochet;

import java.util.Queue;

public class NodeCollection {

  private Queue<Node> queue;
  
  public NodeCollection(){
    
  }
  
  public boolean isEmpty() {
    if (queue.peek() == null) {
      return true;
    } else {
      return false;
    }
  }

  public Node getFirst() {
    return queue.peek();
  }

  public void add(Node nextMove) {
    queue.add(nextMove);
  }

  public void remove() {
    queue.remove();
  }

}
