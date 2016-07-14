package org.hirschhorn.ricochet;

import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Stack;

public class BreadthFirstUnprocessedMoves implements UnprocessedMoves {

	private Queue<Move> queue;


	public BreadthFirstUnprocessedMoves() {
		queue = new LinkedList<>();
	}

	@Override
	public boolean isEmpty() {
		return queue.isEmpty();
	}

	@Override
	public Move removeFirst() {
		return queue.poll();
	}

	@Override
	public void add(Move nextMove) {
		queue.add(nextMove);
	}

	@Override
	public void clear() {
		queue.clear();

	}

}
