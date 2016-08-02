CELL_WIDTH = 40;
CELL_HEIGHT = 40;
WALL_THICKNESS = 3;
mostRecentlyClicked = null;
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
	buildWallsFromServer();
	

	canvasBoard.addEventListener("mousedown", moveRobotTowardCanvasClick, false);
}

function ajaxStartGame() {
	$.ajax({
				url : "/ricochet/game/start",
				success : function(result) {
					writeMessage(result);
					document.getElementById("redSquare").style.visibility = "visible";
				}
			});
}

function ajaxSolveGame() {
	writeMessage("Solving game. Please have patience...");
	var iteration = document.getElementById("iteration").value;
	$.ajax({
				url : "/ricochet/game/play?iteration=" + iteration,
				success : function(result) {
					writeMessage("Game solved!");
					var moveActions = JSON.parse(result);
					var actionWhenDone = function(currentAction){
				    	if (currentAction < moveActions.length) {
				    	  var moveAction = moveActions[currentAction];
						  ajaxMoveRobot(moveAction.robot, moveAction.direction, function() {actionWhenDone(currentAction+1)});
				    	}
				    };
					ajaxMoveRobot(moveActions[1].robot, moveActions[1].direction, function() {actionWhenDone(2)});
				}
			});
}

function buildWallsFromServer() {
	$.ajax({
		url : "/ricochet/board/get",
		success : function(result) {
			var boardItems = JSON.parse(result);
			buildWalls(boardItems);
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
		contextBoard.fillRect(x + CELL_WIDTH, y, WALL_THICKNESS,
				CELL_HEIGHT + 3);
		break;
	case "SOUTH":
		contextBoard.fillRect(x, y + CELL_HEIGHT, CELL_WIDTH + 3,
				WALL_THICKNESS);
		break;
	case "WEST":
		contextBoard.fillRect(x, y, WALL_THICKNESS, CELL_HEIGHT);
		break;
	default:
		writeMessage("Unknown direction: " + direction);
	}
}

function writeMessage(message) {
	document.getElementById("message").innerHTML = message;
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

function moveRobotTowardCanvasClick(event) {
	var pos = getPositionOnCanvas(canvasBoard, event);
	moveRobot(pos.x, pos.y, mostRecentlyClicked);
}

function ajaxMoveRobot(robotColor, direction, actionWhenDone) {
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

function moveRobotToPosition(robot, direction, position, actionWhenDone) {
			switch (direction) {
			case 'North':
			case 'South':
				moveVertical(robot, cellYToY(position.y), actionWhenDone);
				// if(winner){
				// moveVertical(greenRobot, 300);
				// }
				// return cellYToY(position.y);
				break;
			case 'East':
			case 'West':
				moveHorizontal(robot, cellXToX(position.x), actionWhenDone);
				// return cellXToX(position.x);
				break;
			}
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
	switch (robot.id){
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

function moveVertical(robot, newY, actionWhenDone) {
	// console.log("entering moveTo( " + newX + "," + newY + ")");
	y = robot.offsetTop;
	x = robot.offsetLeft;
	// console.log("x,y=" + x + "," + y);
	if (newY > y) {
		robot.style.top = y + 2;
		// console.log("offsetTop=" + robot.offsetTop);
	}
	if (newY < y) {
		robot.style.top = y - 2;
		// console.log("offsetTop=" + robot.offsetTop);
	}
	if (newY != y) {
		setTimeout(function() {
			moveVertical(robot, newY, actionWhenDone)
		}, 5);
	} else {
	  ajaxCheckForWinner(robot);
	  actionWhenDone();
	}
}

function moveHorizontal(robot, newX, actionWhenDone) {
	x = robot.offsetLeft;
	if (newX > x) {
		robot.style.left = x + 2;
		// console.log("offsetLeft=" + robot.offsetLeft);
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
	  actionWhenDone();
	}
}

function ajaxCheckForWinner(robot){
	$.ajax({
		url : "/ricochet/robot/iswinner?robot=" + getColorFromRobot(robot),
		success : function(result) {
			var isWinner = JSON.parse(result);
			if (isWinner === true){
              alert("YOU WON: " + result);
			}
		}
	});
}

function justClicked(color){
	mostRecentlyClicked = color;
}

function moveRobot(posX, posY, color) {
	var robot = getRobotFromColor(color);
	if ((posX > robot.offsetLeft) && (posY - robot.offsetTop < 40)
			&& (posY - robot.offsetTop > 0)) {
		ajaxMoveRobot(color, 'East');
		
	}
	if ((posY > robot.offsetTop) && (posX - robot.offsetLeft < 40)
			&& (posX - robot.offsetLeft > 0)) {
		ajaxMoveRobot(color, 'South');
	}
	if ((posY < robot.offsetTop) && (posX - robot.offsetLeft < 40)
			&& (posX - robot.offsetLeft > 0)) {
		ajaxMoveRobot(color, 'North');
	}
	if ((posX < robot.offsetLeft) && (posY - robot.offsetTop < 40)
			&& (posY - robot.offsetTop > 0)) {
		ajaxMoveRobot(color, 'West');
	}
}

function getPositionOnCanvas(canvas, domEvent) {
	  var rect = canvas.getBoundingClientRect();
	  var scaleX = canvas.width / rect.width;   
	  var scaleY = canvas.height / rect.height; 
	  
	  return {
	    x: (domEvent.clientX - rect.left) * scaleX, 
	    y: (domEvent.clientY - rect.top) * scaleY 
	  }
	}