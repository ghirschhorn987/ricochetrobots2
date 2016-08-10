// Constants
CELL_WIDTH = 40;
CELL_HEIGHT = 40;
WALL_THICKNESS = 3;

// Game State
mostRecentlyClickedRobotColor = null;
canvasBoard = null;
contextBoard = null;
totalMoves = 0;
currentVersion = 0;
playerId = "test";
round = 0;
phase = 0;

function init() {
  canvasBoard = document.getElementById("canvasBoard");
  contextBoard = canvasBoard.getContext("2d");
  var cells = 16;
  for (var x = 0; x < (CELL_WIDTH * cells); x = x + CELL_WIDTH) {
    for (var y = 0; y < (CELL_HEIGHT * cells); y = y + CELL_HEIGHT) {
      contextBoard.strokeStyle = "grey";
      contextBoard.lineWidth = 1;
      contextBoard.strokeRect(x, y, CELL_WIDTH, CELL_HEIGHT);
    }
  }

  //fill center with black
  contextBoard.fillRect(cellXToX(7), cellYToY(7), CELL_WIDTH * 2,
      CELL_HEIGHT * 2);
  
  ajaxBuildWalls();
  ajaxBuildTargets();
  
  canvasBoard.addEventListener("mousedown", ajaxMoveRobotTowardCanvasClick, false);
  
  ajaxGetLatestChangesFromServer();
  
  getRobotFromColor("Red").style.visibility = "visible";
  getRobotFromColor("Yellow").style.visibility = "visible";
  getRobotFromColor("Green").style.visibility = "visible";
  getRobotFromColor("Blue").style.visibility = "visible";
}

function ajaxGetLatestChangesFromServer(){
  $.ajax({
    url : "/ricochet/getlatestchanges?version=" + currentVersion,
    success : function(result) {
      displayLatestChanges(result);
      
      var updateEventList = JSON.parse(result);

      // Create a function that processes a single element in the list, and when done
      // calls itself with the next element.  If there are no elements in list, then
      // request more changes from server.  Need to do it this way because some events
      // use setTimeout and therefore we can't just process all events in simple loop.
      var processUpdateEventList = function(updateEventList, index) {
        if (index < updateEventList.length) {
          // We have an event to process. Process it and have that call back to us when
          // done with the next index position.
          processUpdateEvent(
              updateEventList[index],
              function() {
                processUpdateEventList(updateEventList, index + 1);
              });
        } else {
          // No more events to process -- get more from server.
          setTimeout(ajaxGetLatestChangesFromServer, 10);          
        } 
      };
      
      // Start processing the list by calling function with index of 0.
      processUpdateEventList(updateEventList, 0);
    }
  });
}

function processUpdateEvent(updateEvent, actionWhenDone) {
  
  // Game was reset on server
  if (currentVersion > updateEvent.currentVersion) {
    writeMessage("Game was reset.");
    clearLatestChangesMessages();
  }
  currentVersion = updateEvent.currentVersion;
  
  var shouldExecuteActionWhenDone = true;
  switch (updateEvent.eventType){
    case "TARGET_SET":
      var oldTarget = updateEvent.eventData.oldTarget;
      var newTarget = updateEvent.eventData.newTarget;
      var position = updateEvent.eventData.position;
      clearAllTargetsAndShowNewTarget(newTarget, position);
      break;
    case "ROBOT_GLIDED":
      var robot = getRobotFromColor(updateEvent.eventData.robot);
      var oldPositon = updateEvent.eventData.oldPosition;
      var newPosition = updateEvent.eventData.newPosition;
      var direction = updateEvent.eventData.direction;
      moveRobotGlideToPosition(robot, direction, newPosition, actionWhenDone);
      shouldExecuteActionWhenDone = false;
      break;
    case "ROBOT_JUMPED":
      var robot = getRobotFromColor(updateEvent.eventData.robot);
      var oldPositon = updateEvent.eventData.oldPosition;
      var newPosition = updateEvent.eventData.newPosition;
      moveRobotJumpToPosition(robot, newPosition, actionWhenDone);
      break;
  }
  
  if (shouldExecuteActionWhenDone) {
    actionWhenDone();
  }
}

function ajaxResetServer(){
  $.ajax({
    url : "/ricochet/game/reset",
    success : function(result) {
    }
  });
}

function ajaxSubmitGuess(){
  var guess = document.getElementById('guessBox').value;
  $.ajax({
    url : "/ricochet/submit/guess?playerId=" + playerId + "&guess=" + guess,
    success : function(result) {
      writeMessage("Guess has been submitted:" + guess);
    }
  });
}

function ajaxAddNewPlayer(){
  playerId = document.getElementById('nameBox').value;
  document.getElementById('nameBox').style.visibility = 'hidden';
  document.getElementById('submitName').style.visibility = 'hidden';
  $.ajax({
    url : "/ricochet/submit/newplayer?playerId=" + playerId,
    success : function(result) {
      writeMessage("Player Has Been Added:" + guess);
    }
  });
}

function ajaxBuildWalls() {
  $.ajax({
    url : "/ricochet/board/walls/get",
    success : function(result) {
      var boardItems = JSON.parse(result);
      buildWalls(boardItems);
    }
  });
}

function ajaxBuildTargets(){
  $.ajax({
    url : "/ricochet/board/targets/get",
    success : function(result) {
      var targetsAndPositions = JSON.parse(result);
      buildTargetsListBox(targetsAndPositions.targets, targetsAndPositions.positions);
    }
  });
}

function ajaxGetBoardState() {
  $.ajax({
    url : "/ricochet/boardstate/get",
    success : function(result) {
      var boardState = JSON.parse(result);
      var target = boardState.chosenTarget;
      var robotPositions = boardState.robotPositions;
      writeMessage(
          "Target: " + target.color + " " + target.shape
              + "   Red: " + robotPositions.positions[0].x + "," + robotPositions.positions[0].y
              + "   Yellow: " + robotPositions.positions[1].x + "," + robotPositions.positions[1].y
              + "   Green: " + robotPositions.positions[2].x + "," + robotPositions.positions[2].y
              + "   Blue: " + robotPositions.positions[3].x + "," + robotPositions.positions[3].y
              );
    }
  });
}

function ajaxSetTarget(targetColorAndShapeString) {
  var array = targetColorAndShapeString.split(" ");
  $.ajax({
    url : "/ricochet/game/target/set?color=" + array[0] + "&shape=" + array[1],
    success : function(result) {
    }
  });
}

function ajaxSolveGame() {
  writeMessage("Solving game. Please have patience...");
  $.ajax({
    url : "/ricochet/game/solve",
    success : function(result) {
    }
  });
}

function ajaxMoveRobotTowardCanvasClick(event) {
  var pos = getPositionOnCanvas(canvasBoard, event);
  ajaxMoveRobotTowardPosition(mostRecentlyClickedRobotColor, pos);
}

function ajaxMoveRobotTowardPosition(robotColor, pos) {
  var direction = getDirectionOfPositionRelativeToRobot(pos, getRobotFromColor(robotColor));
  ajaxMoveRobotTowardDirection(robotColor, direction);
}

function ajaxMoveRobotTowardDirection(robotColor, direction, actionWhenDone) {
  $.ajax({
    url : "/ricochet/robot/move?robot=" + robotColor + "&direction="
        + direction,
    success : function(result) {
    }
  });
}

function ajaxCheckForWinner(robot) {
  $.ajax({
    url : "/ricochet/robot/iswinner?robot=" + getColorFromRobot(robot),
    success : function(result) {
      var isWinner = JSON.parse(result);
      if (isWinner == true) {
        writeMessage("CONGRATULATIONS! YOU WON: " + result);
      }
    }
  });
}

function targetSelectedFromListBox() {
  var targetListBox = document.getElementById("targetListBox");
  var selectedText = targetListBox.options[targetListBox.selectedIndex].text;
  ajaxSetTarget(selectedText);
}

function ajaxChooseNewTarget() {
  $.ajax({
    url : "/ricochet/game/target/chooseNew",
    success : function(result) {
    }
  });
}

function buildWalls(boardItems) {
  for (var i = 0; i < boardItems.length; i++) {
    var boardItem = boardItems[i];
    if (boardItem.northWall === true) {
      buildWall(boardItem.position.x, boardItem.position.y, "NORTH");
    }
    if (boardItem.eastWall === true) {
      buildWall(boardItem.position.x, boardItem.position.y, "EAST");
    }
    if (boardItem.southWall === true) {
      buildWall(boardItem.position.x, boardItem.position.y, "SOUTH");
    }
    if (boardItem.westWall === true) {
      buildWall(boardItem.position.x, boardItem.position.y, "WEST");
    }
  }
}

function buildWall(cellX, cellY, direction) {
  var x = cellXToX(cellX) - 1;
  var y = cellYToY(cellY) - 1;
  contextBoard.strokeStyle = "black";
  switch (direction) {
  case "NORTH":
    contextBoard.fillRect(x, y, CELL_WIDTH, WALL_THICKNESS);
    break;
  case "EAST":
    contextBoard.fillRect(x + CELL_WIDTH, y, WALL_THICKNESS, CELL_HEIGHT + 3);
    break;
  case "SOUTH":
    contextBoard.fillRect(x, y + CELL_HEIGHT, CELL_WIDTH + 3, WALL_THICKNESS);
    break;
  case "WEST":
    contextBoard.fillRect(x, y, WALL_THICKNESS, CELL_HEIGHT);
    break;
  default:
    writeMessage("Unknown direction: " + direction);
  }
}

function buildTargetsListBox(targets, positions){
  var targetListBox = document.getElementById("targetListBox");
  for (var i=0; i<targets.length; i++){
    var target = targets[i];
    var targetOption = document.createElement('option');
    targetOption.text = target.color + " " + target.shape;
    targetListBox.add(targetOption); 
  }
}

function clearAllTargetsAndShowNewTarget(target, position) {
  clearAllTargets();
  buildTarget(target, position);
}

function buildTarget(target, position) {
  //contextBoard.fillRect(cellXToX(position.x), cellYToY(position.y), CELL_HEIGHT, CELL_WIDTH);
  var GAP = 8;
  var svg   = document.getElementById("boardSvg");
  var svgNS = svg.namespaceURI;
  
  var svgShape = 'rect';
  switch (target.shape) {
    case 'Star':
      svgShape = 'rect';
      break;
    case 'Planet':
      svgShape = 'rect';
      break;
    case 'Moon':
      svgShape = 'rect';
      break;
    case 'Sawblade':
      svgShape = 'rect';
      break;
  }
  
  var rect = document.createElementNS(svgNS,svgShape);
  rect.setAttribute('id', target.color.charAt(0) + "_" + target.shape.substring(0,2));
  rect.setAttribute('class', "target");
  rect.setAttribute('x',cellXToX(position.x) + GAP);
  rect.setAttribute('y',cellYToY(position.y) + GAP);
  rect.setAttribute('width',CELL_WIDTH - (GAP * 2));
  rect.setAttribute('height',CELL_HEIGHT - (GAP * 2));
  rect.setAttribute('fill',target.color);
  rect.setAttribute('color', target.color);
  rect.setAttribute('shape', target.shape);
//  rect.style.visibility = 'hidden';
  svg.appendChild(rect);
}

function clearAllTargets(){
  var svg = document.getElementById("boardSvg");
  var targets = document.getElementsByClassName("target");
  for (var i = 0; i < targets.length; i++) {
    svg.removeChild(targets[i]);    
  }
}


function moveRobotJumpToPosition(robot, cellPosition) {
  robot.style.left = cellXToX(cellPosition.x);
  robot.style.top = cellYToY(cellPosition.y);
}

function moveRobotGlideToPosition(robot, direction, cellPosition, actionWhenDone) {
  switch (direction) {
  case 'North':
  case 'South':
    moveVerticalGlide(robot, cellYToY(cellPosition.y), actionWhenDone);
    break;
  case 'East':
  case 'West':
    moveHorizontalGlide(robot, cellXToX(cellPosition.x), actionWhenDone);
    break;
  }
  totalMoves++;
  writeMessage("Total Moves: " + totalMoves);
}

function moveVerticalGlide(robot, newY, actionWhenDone) {
  y = robot.offsetTop;
  if (newY > y) {
    robot.style.top = y + 2;
  }
  if (newY < y) {
    robot.style.top = y - 2;
  }
  if (newY != y) {
    setTimeout(function() {
      moveVerticalGlide(robot, newY, actionWhenDone)
    }, 5);
  } else {
    ajaxCheckForWinner(robot);
    if (actionWhenDone != null) {
      actionWhenDone();
    }
  }
}

function moveHorizontalGlide(robot, newX, actionWhenDone) {
  x = robot.offsetLeft;
  if (newX > x) {
    robot.style.left = x + 2;
  }
  if (newX < x) {
    robot.style.left = x - 2;
  }
  if (newX != x) {
    setTimeout(function() {
      moveHorizontalGlide(robot, newX, actionWhenDone)
    }, 5);
  } else {
    ajaxCheckForWinner(robot);
    if (actionWhenDone != null) {
      actionWhenDone();
    }
  }
}

function justClicked(robotColor) {
  mostRecentlyClickedRobotColor = robotColor;
}

function writeMessage(message) {
  document.getElementById("message").innerHTML = message;
}

function clearLatestChangesMessages() {
  document.getElementById("latestChangesMessages").innerHTML = "";
}

function displayLatestChanges(message) {
  var date = new Date();
  var dateString = date.getHours() + ":" + date.getMinutes() + ":" + date.getSeconds();

  var currentMessage = document.getElementById("latestChangesMessages").innerHTML;
  document.getElementById("latestChangesMessages").innerHTML = currentMessage + "<br>" + dateString + "  " + message;
}

function getPositionOnCanvas(canvas, domEvent) {
  var rect = canvas.getBoundingClientRect();
  var scaleX = canvas.width / rect.width;
  var scaleY = canvas.height / rect.height;

  return {
    x : (domEvent.clientX - rect.left) * scaleX,
    y : (domEvent.clientY - rect.top) * scaleY
  }
}

function cellXToX(cellX) {
  return (cellX * CELL_WIDTH);
}

function cellYToY(cellY) {
  return (cellY * CELL_HEIGHT);
}

function xToCellX(x) {
  return (x / CELL_WIDTH)
}

function yToCellY(y) {
  return (y / CELL_HEIGHT)
}

function getDirectionOfPositionRelativeToRobot(pos, robot) {
  var posX = pos.x;
  var posY = pos.y;
  var direction;
  if ((posX > robot.offsetLeft) && (posY - robot.offsetTop < CELL_HEIGHT)
      && (posY - robot.offsetTop > 0)) {
    direction = 'East';
  }
  if ((posY > robot.offsetTop) && (posX - robot.offsetLeft < CELL_WIDTH)
      && (posX - robot.offsetLeft > 0)) {
    direction = 'South';
  }
  if ((posY < robot.offsetTop) && (posX - robot.offsetLeft < CELL_WIDTH)
      && (posX - robot.offsetLeft > 0)) {
    direction = 'North';
  }
  if ((posX < robot.offsetLeft) && (posY - robot.offsetTop < CELL_HEIGHT)
      && (posY - robot.offsetTop > 0)) {
    direction = 'West';
  }
  return direction;
}

function getRobotFromColor(color) {
  var robot;
  switch (color) {
  case "Blue":
    robot = document.getElementById("blueRobot");
    break;
  case "Red":
    robot = document.getElementById("redRobot");
    break;
  case "Yellow":
    robot = document.getElementById("yellowRobot");
    break;
  case "Green":
    robot = document.getElementById("greenRobot");
    break;
  }
  return robot;
}

function getColorFromRobot(robot) {
  var color;
  switch (robot.id) {
  case "blueRobot":
    color = "Blue";
    break;
  case "redRobot":
    color = "Red";
    break;
  case "yellowRobot":
    color = "Yellow";
    break;
  case "greenRobot":
    color = "Green";
    break;
  }
  return color;
}
