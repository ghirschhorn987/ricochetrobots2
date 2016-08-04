CELL_WIDTH = 40;
CELL_HEIGHT = 40;
WALL_THICKNESS = 3;
mostRecentlyClickedRobotColor = null;
canvasBoard = null;
contextBoard = null;

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

  contextBoard.fillRect(cellXToX(7), cellYToY(7), CELL_WIDTH * 2,
      CELL_HEIGHT * 2);
  ajaxBuildWalls();
  ajaxBuildTargets();

  canvasBoard.addEventListener("mousedown", ajaxMoveRobotTowardCanvasClick, false);
}

function ajaxUpdateServerFromClient(){
  
}

function ajaxUpdateClientFromServer(){
  
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
      buildTargets(targetsAndPositions.targets, targetsAndPositions.positions);
    }
  });
}

function ajaxStartGame() {
  var targetIndex = document.getElementById("targetIndex").value;
  $.ajax({
    url : "/ricochet/game/start?targetIndex=" + targetIndex,
    success : function(result) {
      writeMessage(result);
      document.getElementById("solveGameButton").style.visibility = "visible";
    }
  });
}

function ajaxSolveGame() {
  writeMessage("Solving game. Please have patience...");
  $.ajax({
    url : "/ricochet/game/solve",
    success : function(result) {
      writeMessage("Game solved!");
      var moves = JSON.parse(result);
      var actionWhenDone = function(currentAction) {
        if (currentAction < moves.length) {
          var move = moves[currentAction];
          ajaxMoveRobotTowardDirection(move.robot, move.direction, function() {
            actionWhenDone(currentAction + 1)
          });
        }
      };
      ajaxMoveRobotTowardDirection(moves[1].robot, moves[1].direction, function() {
        actionWhenDone(2)
      });
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
      var position = JSON.parse(result);
      var robot = getRobotFromColor(robotColor);
      moveRobotToPosition(robot, direction, position, actionWhenDone);
    }
  });
}

function ajaxCheckForWinner(robot) {
  $.ajax({
    url : "/ricochet/robot/iswinner?robot=" + getColorFromRobot(robot),
    success : function(result) {
      var isWinner = JSON.parse(result);
      if (isWinner === true) {
        alert("YOU WON: " + result);
      }
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

function buildTargets(targets, positions){
  var GAP = 8;
  var svg   = document.getElementById("boardSvg");
  var svgNS = svg.namespaceURI;
  for (var i=0; i<targets.length; i++){
    var target = targets[i];
    var position = positions[i];
//   contextBoard.fillRect(cellXToX(position.x), cellYToY(position.y), CELL_HEIGHT, CELL_WIDTH);
    var rect = document.createElementNS(svgNS,'rect');
    rect.setAttribute('id', target.color + "_" + target.shape);
    rect.setAttribute('x',cellXToX(position.x) + GAP);
    rect.setAttribute('y',cellYToY(position.y) + GAP);
    rect.setAttribute('width',CELL_WIDTH - (GAP * 2));
    rect.setAttribute('height',CELL_HEIGHT - (GAP * 2));
    rect.setAttribute('fill',target.color);
    svg.appendChild(rect);
  }
}

function moveRobotToPosition(robot, direction, position, actionWhenDone) {
  switch (direction) {
  case 'North':
  case 'South':
    moveVertical(robot, cellYToY(position.y), actionWhenDone);
    break;
  case 'East':
  case 'West':
    moveHorizontal(robot, cellXToX(position.x), actionWhenDone);
    break;
  }
}

function moveVertical(robot, newY, actionWhenDone) {
  y = robot.offsetTop;
  if (newY > y) {
    robot.style.top = y + 2;
  }
  if (newY < y) {
    robot.style.top = y - 2;
  }
  if (newY != y) {
    setTimeout(function() {
      moveVertical(robot, newY, actionWhenDone)
    }, 5);
  } else {
    ajaxCheckForWinner(robot);
    if (actionWhenDone != null) {
      actionWhenDone();
    }
  }
}

function moveHorizontal(robot, newX, actionWhenDone) {
  x = robot.offsetLeft;
  if (newX > x) {
    robot.style.left = x + 2;
  }
  if (newX < x) {
    robot.style.left = x - 2;
  }
  if (newX != x) {
    setTimeout(function() {
      moveHorizontal(robot, newX, actionWhenDone)
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
