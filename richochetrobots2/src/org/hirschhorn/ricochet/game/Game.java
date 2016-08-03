package org.hirschhorn.ricochet.game;

public class Game {
  
  private Board board;
  private BoardState boardState;

  public Game(Board board, BoardState boardState) {
    this.board = board;
    this.boardState = boardState;
  }

  public Board getBoard() {
    return board;
  }

  public BoardState getBoardState() {
    return boardState;
  }

  public void updateBoardState(BoardState newBoardState) {
    this.boardState = newBoardState;
  }
  
}
