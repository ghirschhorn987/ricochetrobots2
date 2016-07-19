package org.hirschhorn.ricochet;

import java.util.Stack;

public class DepthFirstUnprocessedMoves implements UnprocessedMoves {

	private Stack<Move> stack;


	public DepthFirstUnprocessedMoves() {
		stack = new Stack<>();
	}

	@Override
	public boolean isEmpty() {
		return stack.isEmpty();
	}

	@Override
	public Move removeFirst() {
		return stack.pop();
	}

	@Override
	public void add(Move nextMove) {
		stack.add(nextMove);
	}

	@Override
	public void clear() {
		stack.clear();

	}

}
