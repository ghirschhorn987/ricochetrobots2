package org.hirschhorn.ricochet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

public class RicochetRobotsServlet extends HttpServlet {

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
      default:
        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        out.print("Unrecognized request: " + request.getRequestURI());        
    }
  }

  private String getBoardItemsAsJsonString() {
    Game game = (new GameFactory()).createGame(0);
    Gson gson = new Gson();
    return gson.toJson(game.getBoard().getBoardItems());
  }

  public void destroy() {
  }
}