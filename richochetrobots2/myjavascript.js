CELL_WIDTH = 40;
CELL_HEIGHT = 40;
WALL_THICKNESS = 3;
mostRecentlyClickedRobotColor = null;
canvasBoard = null;
contextBoard = null;
//currentTarget = null;
//unusedTargets = document.getElementsByClassName("target");
totalMoves = 0;
currentVersion = 0;

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
}

function ajaxGetLatestChangesFromServer(){
  $.ajax({
    url : "/ricochet/getlatestchanges?version=" + currentVersion,
    success : function(result) {
      currentVersion = result;
      var date = new Date();
      var dateString = date.getHours() + ":" + date.getMinutes() + ":" + date.getSeconds();
      appendMessage(dateString + "  " + result); 
      setTimeout(ajaxGetLatestChangesFromServer, 10);
    }
  });
}

function ajaxUpdateServerFromClient(){
  
}

function ajaxUpdateClientFromServer(){
  $.ajax({
    url : "/ricochet/game/get",
    success : function(result) {
      var game = JSON.parse(result);
//      currentTarget = game.boardState.chosenTarget;
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

//function ajaxSetTarget() {
//  var targetIndex = document.getElementById("targetIndex").value;
//  $.ajax({
//    url : "/ricochet/game/target/set?targetIndex=" + targetIndex,
//    success : function(result) {
//      writeMessage(result);
//      document.getElementById("solveGameButton").style.visibility = "visible";
//      var targets = document.getElementsByClassName("target");
//      for (var i = 0; i < targets.length; i ++){        
//        targets[i].style.visibility = "hidden";
//      }
//      currentTarget = document.getElementById(targetIndexToTarget(targetIndex));
//      currentTarget.style.visibility = "visible";
//    }
//  });
//  }

function ajaxSetTarget(targetColorAndShapeString) {
  var array = targetColorAndShapeString.split(" ");
  $.ajax({
    url : "/ricochet/game/target/set?color=" + array[0] + "&shape=" + array[1],
    success : function(result) {
      var targetAndPosition = JSON.parse(result);
      clearAllTargetsAndShowNewTarget(targetAndPosition);
//      clearCurrentTarget();
//      buildTarget(targetAndPosition.target, targetAndPosition.position);
//      document.getElementById("solveGameButton").style.visibility = "visible";
//      var targets = document.getElementsByClassName("target");
//      for (var i = 0; i < targets.length; i ++){        
//        targets[i].style.visibility = "hidden";
//      }
//      document.getElementById(targetIndexToTarget(targetIndex)).style.visibility = "visible";
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
      if (isWinner == true) {
        writeMessage("CONGRATULATIONS! YOU WON: " + result);
        ajaxChooseNewTarget();
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
      var targetAndPosition = JSON.parse(result);
      clearAllTargetsAndShowNewTarget(targetAndPosition);
      ajaxGetBoardState();
    }
  });
}


// TODO: Moved logic to server to be called from ajaxChooseNewTarget
//function chooseNewTarget(){
//  var unusedTargets1 = unusedTargets;
//  for(var i=0; i < unusedTargets.length - 1; i++) {
//    console.log("" + i + " " + unusedTargets[i] + " " + currentTarget)
//    if(unusedTargets[i] == targetIndexToTarget(currentTarget)) {
//       unusedTargets1.splice(i, 1);
//    }
//  }
//  unusedTargets = unusedTargets1;
//  var n = Math.floor((Math.random() * unusedTargets.length) + 1);
//  currentTarget = targetToTargetIndex(unusedTargets[n]);
//  ajaxSetTarget(targetToTargetIndex(unusedTargets[n]));
//}

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

function clearAllTargetsAndShowNewTarget(targetAndPosition) {
  clearAllTargets();
  buildTarget(targetAndPosition.target, targetAndPosition.position);
  document.getElementById("solveGameButton").style.visibility = "visible";
}

function buildTarget(target, position) {
  //contextBoard.fillRect(cellXToX(position.x), cellYToY(position.y), CELL_HEIGHT, CELL_WIDTH);
  var GAP = 8;
  var svg   = document.getElementById("boardSvg");
  var svgNS = svg.namespaceURI;
  
  var rect = document.createElementNS(svgNS,'rect');
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
  totalMoves++;
  writeMessage(totalMoves);
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
 // document.getElementById("message").innerHTML = message;
}


function appendMessage(message) {
  var currentMessage = document.getElementById("message").innerHTML;
  document.getElementById("message").innerHTML = currentMessage + "<br>" + message;
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

//function targetIndexToTarget(targetIndex){
//  var shape;
//  var color;
//  if (targetIndex < 4){
//    color = "R";
//  }
//  if (targetIndex > 3 && targetIndex < 8){
//    color = "Y";
//  }
//  if (targetIndex > 7  && targetIndex < 12){
//    color = "G";
//  }
//  if (targetIndex > 11){
//    color = "B";
//  }
//  if (targetIndex % 4 === 0) {
//    shape = "St"
//  }
//  if (targetIndex % 4 === 1) {
//    shape = "Pl"
//  }
//  if (targetIndex % 4 === 2) {
//    shape = "Mo"
//  }
//  if (targetIndex % 4 === 3) {
//    shape = "Sa"
//  }
//  
//  return color + "_" + shape;
//}
//
//function targetToTargetIndex(target) {
//  var targetIndex = 0;
//  var targetName = target.getAttribute('id');
//  var substring = targetName.substring(2,4);
//  var firstLetter = targetName.charAt(0);
//  if(firstLetter == 'R') {
//    targetIndex = targetIndex + 3;
//  }
//  if(firstLetter == 'Y') {
//    targetIndex = targetIndex + 7;
//  }
//  if(firstLetter == 'G') {
//    targetIndex = targetIndex + 11;
//  }
//  if(firstLetter == 'B') {
//    targetIndex = targetIndex + 15;
//  }
//  if(substring == 'St') {
//    targetIndex = targetIndex - 3;
//  }
//  if(substring == 'Pl') {
//    targetIndex = targetIndex - 2;
//  }
//  if(substring == 'Mo') {
//    targetIndex = targetIndex - 1;
//  }
//  return targetIndex;
//}



