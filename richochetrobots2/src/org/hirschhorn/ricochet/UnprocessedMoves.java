package org.hirschhorn.ricochet;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

public class UnprocessedMoves {

  public enum SearchMode {
    DFS, BFS
  }

  private SearchMode searchMode = SearchMode.BFS;
  private Queue<Move> queue;
  private Stack<Move> stack;

  public UnprocessedMoves(SearchMode searchMode) {
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

  public Move removeFirst() {
    switch (searchMode) {
    case BFS:
      return queue.poll();
    case DFS:
      return stack.pop();
    default:
      throw new AssertionError("Unknown SearchMode: " + searchMode);
    }
  }

  public void add(Move nextMove) {
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
