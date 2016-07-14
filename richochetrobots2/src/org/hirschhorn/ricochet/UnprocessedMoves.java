package org.hirschhorn.ricochet;

public interface UnprocessedMoves {

	boolean isEmpty();

	Move removeFirst();

	void add(Move nextMove);

	void clear();

}