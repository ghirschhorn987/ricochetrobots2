CELL_WIDTH = 40;
CELL_HEIGHT = 40;
WALL_THICKNESS = 3;

function init() {
	var canvasBoard = document.getElementById("canvasBoard");
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
}

function startGame() {
	$
			.ajax({
				url : "/ricochet/game/start",
				success : function(result) {
					writeMessage(result);
					document.getElementById("moveBlueUp").style.visibility = "visible";
					document.getElementById("moveBlueDown").style.visibility = "visible";
					document.getElementById("moveBlueLeft").style.visibility = "visible";
					document.getElementById("moveBlueRight").style.visibility = "visible";
					document.getElementById("moveRedUp").style.visibility = "visible";
					document.getElementById("moveRedDown").style.visibility = "visible";
					document.getElementById("moveRedLeft").style.visibility = "visible";
					document.getElementById("moveRedRight").style.visibility = "visible";
					document.getElementById("redSquare").style.visibility = "visible";
				}
			});
}

function blueRobotHasBeenClicked() {
	blueRobotHasBeenClicked = true;
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

function xToCellX(x) {
	return (x / CELL_WIDTH)
}

function yToCellY(y) {
	return (y / CELL_HEIGHT)
}

function move() {
	moveVertical(blueRobot, cellXToX(10));
}

function ajaxMoveRobot(robotColor, direction) {
	$.ajax({
		url : "/ricochet/robot/move?robot=" + robotColor + "&direction="
				+ direction,
		success : function(result) {
			var position = JSON.parse(result);
			var robot = getRobotFromColor(robotColor);
			switch (direction) {
			case 'North':
			case 'South':
				moveVertical(robot, cellYToY(position.y));
				// if(winner){
				// moveVertical(greenRobot, 300);
				// }
				// return cellYToY(position.y);
				break;
			case 'East':
			case 'West':
				moveHorizontal(robot, cellXToX(position.x));
				// return cellXToX(position.x);
				break;
			}
		}
	});
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

function moveVertical(robot, newY) {
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
			moveVertical(robot, newY)
		}, 5);
	}
}

function moveHorizontal(robot, newX) {
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
			moveHorizontal(robot, newX)
		}, 5);
	}
}

function cellXToX(cellX) {
	return (cellX * CELL_WIDTH);
}

function cellYToY(cellY) {
	return (cellY * CELL_HEIGHT);
}

function point_it(event) {
	posX = event.offsetX ? (event.offsetX) : event.pageX
			- document.getElementById("container").offsetLeft;
	posY = event.offsetY ? (event.offsetY) : event.pageY
			- document.getElementById("container").offsetTop;
	document.getElementById("cross").style.left = (posX - 1);
	document.getElementById("cross").style.top = (posY - 15);
	// document.getElementById("cross").style.visibility = "visible" ;
	moveRobots(posX, posY);
}

function moveRobots(posX, posY) {
	// BLUE
	var possibleMoves = [];
	if ((posX > blueRobot.offsetLeft) && (posY - blueRobot.offsetTop < 40)
			&& (posY - blueRobot.offsetTop > 0)) {
		ajaxMoveRobot('Blue', 'East');
		
	}
	if ((posY > blueRobot.offsetTop) && (posX - blueRobot.offsetLeft < 40)
			&& (posX - blueRobot.offsetLeft > 0)) {
		ajaxMoveRobot('Blue', 'South');
	}
	if ((posY < blueRobot.offsetTop) && (posX - blueRobot.offsetLeft < 40)
			&& (posX - blueRobot.offsetLeft > 0)) {
		ajaxMoveRobot('Blue', 'North');
	}
	if ((posX < blueRobot.offsetLeft) && (posY - blueRobot.offsetTop < 40)
			&& (posY - blueRobot.offsetTop > 0)) {
		ajaxMoveRobot('Blue', 'West');
	}
	// RED
	if ((posX > redRobot.offsetLeft) && (posY - redRobot.offsetTop < 40)
			&& (posY - redRobot.offsetTop > 0)) {
		ajaxMoveRobot('Red', 'East');
	}
	if ((posY > redRobot.offsetTop) && (posX - redRobot.offsetLeft < 40)
			&& (posX - redRobot.offsetLeft > 0)) {
		ajaxMoveRobot('Red', 'South');
	}
	if ((posY < redRobot.offsetTop) && (posX - redRobot.offsetLeft < 40)
			&& (posX - redRobot.offsetLeft > 0)) {
		ajaxMoveRobot('Red', 'North');
	}
	if ((posX < redRobot.offsetLeft) && (posY - redRobot.offsetTop < 40)
			&& (posY - redRobot.offsetTop > 0)) {
		ajaxMoveRobot('Red', 'West');
	}
	// YELLOW
	if ((posX > yellowRobot.offsetLeft) && (posY - yellowRobot.offsetTop < 40)
			&& (posY - yellowRobot.offsetTop > 0)) {
		ajaxMoveRobot('Yellow', 'East');
	}
	if ((posY > yellowRobot.offsetTop) && (posX - yellowRobot.offsetLeft < 40)
			&& (posX - yellowRobot.offsetLeft > 0)) {
		ajaxMoveRobot('Yellow', 'South');
	}
	if ((posY < yellowRobot.offsetTop) && (posX - yellowRobot.offsetLeft < 40)
			&& (posX - yellowRobot.offsetLeft > 0)) {
		ajaxMoveRobot('Yellow', 'North');
	}
	if ((posX < yellowRobot.offsetLeft) && (posY - yellowRobot.offsetTop < 40)
			&& (posY - yellowRobot.offsetTop > 0)) {
		ajaxMoveRobot('Yellow', 'West');
	}
	// GREEN
	if ((posX > greenRobot.offsetLeft) && (posY - greenRobot.offsetTop < 40)
			&& (posY - greenRobot.offsetTop > 0)) {
		ajaxMoveRobot('Green', 'East');
	}
	if ((posY > greenRobot.offsetTop) && (posX - greenRobot.offsetLeft < 40)
			&& (posX - greenRobot.offsetLeft > 0)) {
		ajaxMoveRobot('Green', 'South');
	}
	if ((posY < greenRobot.offsetTop) && (posX - greenRobot.offsetLeft < 40)
			&& (posX - greenRobot.offsetLeft > 0)) {
		ajaxMoveRobot('Green', 'North');
	}
	if ((posX < greenRobot.offsetLeft) && (posY - greenRobot.offsetTop < 40)
			&& (posY - greenRobot.offsetTop > 0)) {
		ajaxMoveRobot('Green', 'West');
	}
}
