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
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hirschhorn.ricochet.board.Color;
import org.hirschhorn.ricochet.board.Direction;
import org.hirschhorn.ricochet.board.Position;
import org.hirschhorn.ricochet.board.Shape;
import org.hirschhorn.ricochet.board.Target;
import org.hirschhorn.ricochet.game.Board;
import org.hirschhorn.ricochet.game.BoardState;
import org.hirschhorn.ricochet.game.Game;
import org.hirschhorn.ricochet.game.GameFactory;
import org.hirschhorn.ricochet.game.Move;
import org.hirschhorn.ricochet.game.MoveCalculator;
import org.hirschhorn.ricochet.game.RobotPositions;
import org.hirschhorn.ricochet.game.UpdateEvent;
import org.hirschhorn.ricochet.game.UpdateEventData;
import org.hirschhorn.ricochet.game.UpdateEventType;
import org.hirschhorn.ricochet.solver.MoveNode;
import org.hirschhorn.ricochet.solver.Solver;
import org.hirschhorn.ricochet.solver.SolverFactory;
import org.hirschhorn.ricochet.solver.UnprocessedMovesType;
import org.hirschhorn.ricochet.updateevent.GameRestartedEventData;
import org.hirschhorn.ricochet.updateevent.GuessSubmittedEventData;
import org.hirschhorn.ricochet.updateevent.PlayerAddedEventData;
import org.hirschhorn.ricochet.updateevent.RobotGlidedEventData;
import org.hirschhorn.ricochet.updateevent.RobotJumpedEventData;
import org.hirschhorn.ricochet.updateevent.TargetSetEventData;

import com.google.gson.Gson;

public class RicochetRobotsServlet extends HttpServlet {

  private static final long serialVersionUID = 153254652788906133L;
  private static final long LATEST_CHANGES_TIMEOUT_MILLIS = 10000;
  
  private static Logger logger = Logger.getLogger(RicochetRobotsServlet.class.getName());

  public void init() throws ServletException {
    logger.info("Entering Servlet.init()");
    if (getUpdateEvents() == null) {   
      logger.info("In Servlet.init(). Initializing.");
      intializeUpdateEvents();
      initializeGame(0);
    }
  }

  private void initializeGame(int targetIndex) {
    Game game = (new GameFactory()).createGame(targetIndex);
    getServletContext().setAttribute("GAME", game);
        
    for (Color robot : Color.values()) {
      Position newPosition = getBoardState().getRobotPosition(robot);
      UpdateEventData eventData = new RobotJumpedEventData(robot, null, newPosition);
      addUpdateEvent(UpdateEventType.ROBOT_JUMPED, eventData);
    }
    
    Target oldTarget = null;
    Target newTarget = Target.getTarget(Color.Red, Shape.Star);
    changeTarget(oldTarget, newTarget);    
  }

  private Game getGame() {
    return (Game) getServletContext().getAttribute("GAME");
  }
  
  private Solver getSolver() {
    return (Solver) getServletContext().getAttribute("SOLVER");
  }

  private void setSolver(Solver solver) {
    getServletContext().setAttribute("SOLVER", solver);
  }

  private synchronized void intializeUpdateEvents() {
    getServletContext().setAttribute("UPDATE_EVENTS", new ArrayList<UpdateEvent>());    
    getServletContext().setAttribute("LAST_GAME_RESTART_EVENT", 0);    
  }
  
  @SuppressWarnings("unchecked")
  private synchronized void addUpdateEvent(UpdateEventType updateEventType, UpdateEventData updateEventData) {
    UpdateEvent updateEvent = new UpdateEvent(updateEventType, updateEventData, getUpdateVersion() + 1);
    ((List<UpdateEvent>) getServletContext().getAttribute("UPDATE_EVENTS")).add(updateEvent);
    if (updateEvent.getEventType().equals(UpdateEventType.GAME_RESTARTED)) {
      getServletContext().setAttribute("LAST_GAME_RESTART_EVENT", updateEvent.getCurrentVersion());  
    }
  }
  
  @SuppressWarnings("unchecked")
  private synchronized List<UpdateEvent> getUpdateEvents() {
    return (List<UpdateEvent>) getServletContext().getAttribute("UPDATE_EVENTS");
  }

  private synchronized int getUpdateVersion() {
    return getUpdateEvents().size() - 1;
  }
  
  private synchronized Integer getLastGameRestartVersion() {
    return (Integer)getServletContext().getAttribute("LAST_GAME_RESTART_EVENT");
  }

  private boolean hasChangedSince(int updateVersion) {
    return getUpdateVersion() != updateVersion;
  }

  private synchronized List<UpdateEvent> getUpdateEventsSince(int oldVersion) {
    int currentVersion = getUpdateVersion();
    if (oldVersion == currentVersion) {
      return new ArrayList<UpdateEvent>();
    }
    
    int lastGameRestartVersion = getLastGameRestartVersion();
    if (oldVersion >= lastGameRestartVersion) {
      return new ArrayList<>(getUpdateEvents().subList(oldVersion, currentVersion));
    } else {
      return new ArrayList<>(getUpdateEvents().subList(lastGameRestartVersion, currentVersion));
    }
  }

  private BoardState getBoardState() {
    return getGame().getBoardState();
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    // See http://codebox.org.uk/pages/java-servlet-url-parts
    String pathInfo = request.getPathInfo();
    switch (pathInfo) {
      case "/getlatestchanges":
        doGetLatestChanges(request, response);
        break;
      case "/board/walls/get":
        doGetWalls(request, response);
        break;
      case "/board/targets/get":
        doGetTargets(request, response);
        break;
      case "/game/get":
        doGetGame(request, response);
        break;
      case "/boardstate/get":
        doGetBoardState(request, response);
        break;
      case "/game/restart":
        doRestartGame(request, response);
        break;
      case "/game/solve":
        doGetSolveGame(request, response);
        break;
      case "/game/target/set":
        doGetSetTarget(request, response);
        break;
      case "/game/target/chooseNew":
        doGetChooseNewTarget(request, response);
        break;
      case "/robot/move":
        doMoveRobot(request, response);
        break;
      case "/robot/iswinner":
        doIsWinner(request, response);
        break;
      case "/submit/guess":
        doSubmitGuess(request, response);
        break;
      case "/submit/newplayer":
        doAddNewPlayer(request, response);
        break;
      default:
        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        response.getWriter().print("Unrecognized request: " + request.getRequestURI());
    }
  }

  private void doAddNewPlayer(HttpServletRequest request, HttpServletResponse response) {
    String playerId = request.getParameter("playerId");
    Game game = getGame();
    game.addToPlayerIds(playerId);
    addUpdateEvent(UpdateEventType.PLAYER_ADDED, new PlayerAddedEventData(request.getParameter("playerId")));
  }

  private void doSubmitGuess(HttpServletRequest request, HttpServletResponse response) {
    String guesserId = request.getParameter("guesserId");
    String guess = request.getParameter("guess");
    getGame().addGuessToMap(guesserId, Integer.parseInt(guess));
    addUpdateEvent(
        UpdateEventType.GUESS_SUBMITTED,
        new GuessSubmittedEventData(request.getParameter("guesserId"), Integer.parseInt(request.getParameter("guess"))));
  }

  private void doGetLatestChanges(HttpServletRequest request, HttpServletResponse response) throws IOException {
    int updateVersion = Integer.valueOf(request.getParameter("version"));
    long startTime = System.currentTimeMillis();
    while (!hasChangedSince(updateVersion)
            && (System.currentTimeMillis() - startTime) < LATEST_CHANGES_TIMEOUT_MILLIS) {
      try {
        Thread.sleep(250);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }
    }
    PrintWriter out = response.getWriter();
    Gson gson = new Gson();
    out.println(gson.toJson(getUpdateEventsSince(updateVersion)));
  }

  /**
   * This does NOT remove target from unused targets
   */
  private void doGetSetTarget(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Target oldTarget = getBoardState().getChosenTarget();

    Color color = Color.valueOf(request.getParameter("color"));
    Shape shape = Shape.valueOf(request.getParameter("shape"));
    Target newTarget = Target.getTarget(color, shape);
    changeTarget(oldTarget, newTarget);
  }
  
  private void changeTarget(Target oldTarget, Target newTarget) {
    getBoardState().setChosenTarget(newTarget);
    addUpdateEvent(UpdateEventType.TARGET_SET, new TargetSetEventData(oldTarget, newTarget, getPosition(newTarget)));    
  }

  private void doGetChooseNewTarget(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Target oldTarget = getBoardState().getChosenTarget();
    if (oldTarget != null) {
      getGame().removeTarget(getGame().getBoardState().getChosenTarget());
    }

    // Choose random target to return
    List<Target> unusedTargets = getGame().getUnusedTargets();
    int n = (int) Math.floor((Math.random() * unusedTargets.size()));
    Target newTarget = unusedTargets.get(n);

    changeTarget(oldTarget, newTarget);
  }

  private Position getPosition(Target target) {
    return getGame().getBoard().getTargetPosition(target);
  }

  private void updateBoardState(Color robot, Position newPosition) {
    RobotPositions.Builder robotPositionsBuilder = new RobotPositions.Builder(getBoardState().getRobotPositions());
    robotPositionsBuilder.setRobotPosition(robot, newPosition);
    BoardState newBoardState = new BoardState(getBoardState().getChosenTarget(), robotPositionsBuilder.build());
    getGame().updateBoardState(newBoardState);
  }

  private void doIsWinner(HttpServletRequest request, HttpServletResponse response) throws IOException {
    PrintWriter out = response.getWriter();
    Color robot = Color.valueOf(request.getParameter("robot"));
    Gson gson = new Gson();
    boolean isWinner = isWinner(robot, getGame().getBoard(), getBoardState());
    out.println(gson.toJson(isWinner));
  }

  private void doMoveRobot(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Color robot = Color.valueOf(request.getParameter("robot"));
    Direction direction = Direction.valueOf(request.getParameter("direction"));
    Position oldPosition = getBoardState().getRobotPosition(robot);
    Position newPosition = MoveCalculator.calculateRobotPositionAfterMoving(getBoardState(), getGame().getBoard(),
            robot, direction);
    updateBoardState(robot, newPosition);

    addUpdateEvent(UpdateEventType.ROBOT_GLIDED, new RobotGlidedEventData(robot, oldPosition, newPosition, direction));
  }

  private boolean isWinner(Color robot, Board board, BoardState boardState) {
    Target chosenTarget = boardState.getChosenTarget();
    Color targetColor = chosenTarget.getColor();
    return boardState.getRobotPosition(targetColor).equals(board.getTargetPosition(chosenTarget));
  }
  
  private synchronized void doRestartGame(HttpServletRequest request, HttpServletResponse response) throws IOException {
    getSolver().tryToCancel();
    initializeGame(0);
    addUpdateEvent(UpdateEventType.GAME_RESTARTED, new GameRestartedEventData());
  }  

  private void doGetSolveGame(HttpServletRequest request, HttpServletResponse response) throws IOException {
    PrintWriter out = response.getWriter();
    response.setContentType("text/html");
    UnprocessedMovesType movesType = UnprocessedMovesType.BREADTH_FIRST_SEARCH;
    String movesTypeParam = request.getParameter("unprocessedMovesType");
    if (movesTypeParam != null) {
      movesType = UnprocessedMovesType.valueOf(movesTypeParam);
    }

    Game game = getGame();
    if (getSolver() != null) {
      getSolver().tryToCancel();
    }
    Solver newSolver = (new SolverFactory()).createSolver(game, movesType);
    setSolver(newSolver);
    List<MoveNode> winningMoves = newSolver.solve();
    if (winningMoves.isEmpty()) {
      // Solver was cancelled or couldn't find a solution. Should send this info to client.
    }
    else {
      MoveNode winningMove = winningMoves.get(0);      
      for (MoveNode moveNode : winningMove.getAncestorsFromRootDownToSelf()) {
        Move move = moveNode.getMove();
        if (move != null) {
          Color robot = move.getRobot();
          Direction direction = move.getDirection();
          Position oldPosition = getBoardState().getRobotPosition(robot);
          Position newPosition = MoveCalculator.calculateRobotPositionAfterMoving(getBoardState(), getGame().getBoard(),
                robot, direction);
        
          updateBoardState(robot, newPosition);
          addUpdateEvent(UpdateEventType.ROBOT_GLIDED, new RobotGlidedEventData(robot, oldPosition, newPosition, direction));      
        }
      }
      doGetChooseNewTarget(request, response);
    }
  }

  public int getTargetIndexParam(HttpServletRequest request) {
    int targetIndex = 0;
    String targetIndexParam = request.getParameter("targetIndex");
    if (targetIndexParam != null) {
      targetIndex = Integer.parseInt(targetIndexParam);
    }
    return targetIndex;
  }

  private void doGetWalls(HttpServletRequest request, HttpServletResponse response) throws IOException {
    PrintWriter out = response.getWriter();
    response.setContentType("text/plain");
    response.setStatus(HttpServletResponse.SC_OK);
    Gson gson = new Gson();
    out.println(gson.toJson(getGame().getBoard().getBoardItems()));
  }

  private static class TargetsAndPositions {
    private List<Target> targets = new ArrayList<>();
    private List<Position> positions = new ArrayList<>();
    private List<Integer> targetIndex = new ArrayList<>();
  }

  private void doGetTargets(HttpServletRequest request, HttpServletResponse response) throws IOException {
    PrintWriter out = response.getWriter();
    response.setContentType("text/plain");
    response.setStatus(HttpServletResponse.SC_OK);
    Map<Target, Position> targetsToPositions = getGame().getBoard().getTargetsToPositions();
    TargetsAndPositions targetsAndPositions = new TargetsAndPositions();
    targetsAndPositions.targets = new ArrayList<>(targetsToPositions.keySet());
    targetsAndPositions.positions = new ArrayList<>(targetsToPositions.values());
    Gson gson = new Gson();
    String jsonString = gson.toJson(targetsAndPositions);
    out.println(jsonString);
  }

  private void doGetGame(HttpServletRequest request, HttpServletResponse response) throws IOException {
    PrintWriter out = response.getWriter();
    response.setContentType("text/plain");
    response.setStatus(HttpServletResponse.SC_OK);
    Gson gson = new Gson();
    String jsonString = gson.toJson(getGame());
    out.println(jsonString);
  }

  private void doGetBoardState(HttpServletRequest request, HttpServletResponse response) throws IOException {
    PrintWriter out = response.getWriter();
    response.setContentType("text/plain");
    response.setStatus(HttpServletResponse.SC_OK);
    Gson gson = new Gson();
    out.println(gson.toJson(getBoardState()));
  }

  private String getMovesAsJsonString(MoveNode moveNode) {
    Gson gson = new Gson();
    List<Move> moves = new ArrayList<>();
    for (MoveNode ancestorMove : moveNode.getAncestorsFromRootDownToSelf()) {
      moves.add(ancestorMove.getMove());
    }
    return gson.toJson(moves);
  }

  public void destroy() {
  }
}