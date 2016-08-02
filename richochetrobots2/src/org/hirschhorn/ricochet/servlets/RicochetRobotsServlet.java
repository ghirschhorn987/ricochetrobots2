package org.hirschhorn.ricochet.servlets;

//TODO: Set robot positions in UI from server
//TODO: Set targets in UI from server
//TODO: Add click handler to move robots
//TODO: Convert Image to SVG
//TODO: Add Timer

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hirschhorn.ricochet.Board;
import org.hirschhorn.ricochet.BoardState;
import org.hirschhorn.ricochet.Color;
import org.hirschhorn.ricochet.Direction;
import org.hirschhorn.ricochet.Game;
import org.hirschhorn.ricochet.GameFactory;
import org.hirschhorn.ricochet.Move;
import org.hirschhorn.ricochet.MoveAction;
import org.hirschhorn.ricochet.MoveCalculator;
import org.hirschhorn.ricochet.Position;
import org.hirschhorn.ricochet.RobotPositions;
import org.hirschhorn.ricochet.Target;
import org.hirschhorn.ricochet.UnprocessedMovesType;

import com.google.gson.Gson;

public class RicochetRobotsServlet extends HttpServlet {

  private static final long serialVersionUID = 153254652788906133L;

  public void init() throws ServletException {
  }

  public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//    See http://codebox.org.uk/pages/java-servlet-url-parts   
//    PrintWriter out = response.getWriter();
//    out.println("url: " + request.getRequestURI());
//    out.println("contextPath: " + request.getContextPath());
//    out.println("servletPath: " + request.getServletPath());
//    out.println("pathInfo: " + request.getPathInfo());
//    out.println("pathTranslated: " + request.getPathTranslated());
//    out.println("queryString: " + request.getQueryString());
    
    String pathInfo = request.getPathInfo();
    switch (pathInfo) {
      case "/board/get":
        doGetBoard(request, response);
        break;
      case "/boardstate/get":
        doGetBoardState(request, response);
        break;
      case "/game/play":
        doGetPlayGame(request, response);
        break;
      case "/game/start":
        doGetStartGame(request, response);
        break;
      case "/robot/move":
        doMoveRobot(request, response);
        break;
      case "/robot/iswinner":
        doIsWinner(request, response);
        break;
      default:
        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        response.getWriter().print("Unrecognized request: " + request.getRequestURI());        
    }
  }

  private void doGetStartGame(HttpServletRequest request, HttpServletResponse response) throws IOException {
    PrintWriter out = response.getWriter();
    Game game = (new GameFactory()).createGame(0, UnprocessedMovesType.BREADTH_FIRST_SEARCH);
    getServletContext().setAttribute("GAME", game);
    getServletContext().setAttribute("BOARD_STATE", game.getRootMove().getBoardState());
    out.println("Game started.");
  }

  private Game getGame() {
    return (Game) getServletContext().getAttribute("GAME");
  }
  
  private BoardState getBoardState() {
    return (BoardState) getServletContext().getAttribute("BOARD_STATE");
  }
  
  private void updateBoardState(Color robot, Position newPosition) {
    RobotPositions.Builder robotPositionsBuilder = new RobotPositions.Builder(getBoardState().getRobotPositions());
    robotPositionsBuilder.setRobotPosition(robot, newPosition);
    BoardState newBoardState = new BoardState(getBoardState().getChosenTarget(), robotPositionsBuilder.build());
    getServletContext().setAttribute("BOARD_STATE", newBoardState);
  }
  
  private void doIsWinner(HttpServletRequest request, HttpServletResponse response) throws IOException {
    PrintWriter out = response.getWriter();
    Color robot = Color.valueOf(request.getParameter("robot"));
    Game game = getGame();
    Gson gson = new Gson();
    boolean isWinner = isWinner(robot, game.getBoard(), getBoardState());
    out.println(gson.toJson(isWinner));
  }

  private void doMoveRobot(HttpServletRequest request, HttpServletResponse response) throws IOException {
    PrintWriter out = response.getWriter();
    Color robot = Color.valueOf(request.getParameter("robot"));
    Direction direction = Direction.valueOf(request.getParameter("direction"));
    Game game = getGame();
    Position newPosition = MoveCalculator.calculateRobotPosition(getBoardState(), game.getBoard(), robot, direction);
    updateBoardState(robot, newPosition);
    Gson gson = new Gson();
    out.println(gson.toJson(newPosition));
  }


  private boolean isWinner(Color robot,Board board, BoardState boardState) {
    Target chosenTarget = boardState.getChosenTarget();
    Color targetColor = chosenTarget.getColor();
    return boardState.getRobotPosition(targetColor).equals(board.getTargetPosition(chosenTarget));
  }

  private void doGetPlayGame(HttpServletRequest request, HttpServletResponse response) throws IOException {
    PrintWriter out = response.getWriter();
    response.setContentType("text/html");
    int iteration = 0;
    String iterationParam = request.getParameter("iteration");
    if (iterationParam != null) {
      try {
        iteration = Integer.parseInt(iterationParam);
      } catch (NumberFormatException e) {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        out.println("Invalid iteration: " + iterationParam);
        return;
      }
    }
    
    UnprocessedMovesType movesType = UnprocessedMovesType.BREADTH_FIRST_SEARCH;
    String movesTypeParam = request.getParameter("unprocessedMovesType");
    if (movesTypeParam != null) {
      movesType = UnprocessedMovesType.valueOf(movesTypeParam);
    }
    
    GameFactory gameFactory = new GameFactory();
    Game game = gameFactory.createGame(iteration, movesType);
    Move winningMove = game.play().get(0);
    response.setStatus(HttpServletResponse.SC_OK);
    String winningMovesJsonString = getMoveActionsAsJsonString(winningMove);
    out.println(winningMovesJsonString);
  }

  private void doGetBoard(HttpServletRequest request, HttpServletResponse response) throws IOException {
    PrintWriter out = response.getWriter();    
    response.setContentType("text/plain");
    response.setStatus(HttpServletResponse.SC_OK);
    Game game = (new GameFactory()).createGame(0, UnprocessedMovesType.BREADTH_FIRST_SEARCH);
    Gson gson = new Gson();
    out.println(gson.toJson(game.getBoard().getBoardItems()));
  }
 
  private void doGetBoardState(HttpServletRequest request, HttpServletResponse response) throws IOException {
    PrintWriter out = response.getWriter();    
    response.setContentType("text/plain");
    response.setStatus(HttpServletResponse.SC_OK);
    Gson gson = new Gson();
    out.println(gson.toJson(getBoardState()));
  }

  private String getMoveActionsAsJsonString(Move move) {
    Gson gson = new Gson();
    List<MoveAction> moveActions = new ArrayList<>();
    for (Move ancestorMove : move.getAncestorsFromRootDownToSelf()) {
      moveActions.add(ancestorMove.getMoveAction());
    }
    return gson.toJson(moveActions);
  }

  
  public void destroy() {
  }
}