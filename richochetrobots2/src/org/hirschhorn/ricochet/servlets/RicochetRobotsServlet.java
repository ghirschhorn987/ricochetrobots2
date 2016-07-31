package org.hirschhorn.ricochet.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hirschhorn.ricochet.Game;
import org.hirschhorn.ricochet.GameFactory;
import org.hirschhorn.ricochet.Move;
import org.hirschhorn.ricochet.MoveAction;
import org.hirschhorn.ricochet.UnprocessedMovesType;

import com.google.gson.Gson;

public class RicochetRobotsServlet extends HttpServlet {

  private static final long serialVersionUID = 153254652788906133L;

  public void init() throws ServletException {
  }

  public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    PrintWriter out = response.getWriter();

//    out.println("url: " + request.getRequestURI());
//    out.println("contextPath: " + request.getContextPath());
//    out.println("servletPath: " + request.getServletPath());
//    out.println("pathInfo: " + request.getPathInfo());
//    out.println("pathTranslated: " + request.getPathTranslated());
//    out.println("queryString: " + request.getQueryString());
    
    String pathInfo = request.getPathInfo();
    switch (pathInfo) {
      case "/board/get":
        response.setContentType("text/plain");
        response.setStatus(HttpServletResponse.SC_OK);
        out.println(getBoardItemsAsJsonString());
        break;
      case "/game/play":
        response.setContentType("text/html");

        int iteration = 0;
        String iterationParam = request.getParameter("iteration");
        if (iterationParam != null) {
          try {
            iteration = Integer.parseInt(iterationParam);
          } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.println("Invalid iteration: " + iterationParam);
            break;            
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
        String winningMovesJsonString = getWinningMoveActionsAsJsonString(winningMove);
        out.println(winningMovesJsonString);
        break;
      default:
        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        out.print("Unrecognized request: " + request.getRequestURI());        
    }
  }

  private String getWinningMoveActionsAsJsonString(Move move) {
    Gson gson = new Gson();
    List<MoveAction> moveActions = new ArrayList<>();
    for (Move ancestorMove : move.getAncestorsFromRootDownToSelf()) {
      moveActions.add(ancestorMove.getMoveAction());
    }
    return gson.toJson(moveActions);
  }

  private String getBoardItemsAsJsonString() {
    Game game = (new GameFactory()).createGame(0, UnprocessedMovesType.BREADTH_FIRST_SEARCH);
    Gson gson = new Gson();
    return gson.toJson(game.getBoard().getBoardItems());
  }

  public void destroy() {
  }
}