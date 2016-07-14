package org.hirschhorn.ricochet;

public class UnprocessedMovesFactory {

	public static UnprocessedMoves newBreadthFirstUnprocessedMoves() {
		return new BreadthFirstUnprocessedMoves();		
	}

	public static UnprocessedMoves newDepthFirstUnprocessedMoves() {
	    return new DepthFirstUnprocessedMoves();
	}
	
	public static UnprocessedMoves newPriorityQueueUnprocessedMoves() {
	    return new PriorityQueueUnprocessedMoves();
	}
}
