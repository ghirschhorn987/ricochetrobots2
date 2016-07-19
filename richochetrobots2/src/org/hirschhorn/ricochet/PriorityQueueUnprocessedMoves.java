package org.hirschhorn.ricochet;

import java.util.PriorityQueue;

public class PriorityQueueUnprocessedMoves implements UnprocessedMoves {

	private PriorityQueue<Move> queue;


	public PriorityQueueUnprocessedMoves() {
		queue = new PriorityQueue<>(Move.getPotentialComparator());
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
