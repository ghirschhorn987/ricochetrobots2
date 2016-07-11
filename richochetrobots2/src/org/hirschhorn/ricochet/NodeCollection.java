package org.hirschhorn.ricochet;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

public class NodeCollection {

  public enum SearchMode {
    DFS, BFS
  }

  private SearchMode searchMode = SearchMode.BFS;
  private Queue<Node> queue;
  private Stack<Node> stack;

  public NodeCollection(SearchMode searchMode) {
    switch (searchMode) {
    case BFS:
      queue = new LinkedList<>();
      break;
    case DFS:
      stack = new Stack<>();
      break;
    default:
      throw new AssertionError("Unknown SearchMode: " + searchMode);
    }
  }

  public boolean isEmpty() {
    switch (searchMode) {
    case BFS:
      return queue.isEmpty();
    case DFS:
      return stack.isEmpty();
    default:
      throw new AssertionError("Unknown SearchMode: " + searchMode);
    }
  }

  public Node removeFirst() {
    switch (searchMode) {
    case BFS:
      return queue.poll();
    case DFS:
      return stack.pop();
    default:
      throw new AssertionError("Unknown SearchMode: " + searchMode);
    }
  }

  public void add(Node nextMove) {
    switch (searchMode) {
    case BFS:
      queue.add(nextMove);
      break;
    case DFS:
      stack.add(nextMove);
      break;
    default:
      throw new AssertionError("Unknown SearchMode: " + searchMode);
    }
  }

}
