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
	contextBoard.fillRect(cellXToX(7), cellYToY(7), CELL_WIDTH * 2, CELL_HEIGHT*2);
	buildWallsFromServer();
}

function buildWallsFromServer() {
	$.ajax({
		url : "/richochet/board/get",
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
		contextBoard.fillRect(x + CELL_WIDTH, y, WALL_THICKNESS, CELL_HEIGHT + 3);
		break;
	case "SOUTH":
		contextBoard.fillRect(x, y+ CELL_HEIGHT, CELL_WIDTH + 3, WALL_THICKNESS);
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

function move() {
	moveTo(redRobot, redRobot.offsetLeft, 600);
}

function moveTo(robot, newX, newY) {
	// console.log("entering moveTo( " + newX + "," + newY + ")");
	x = robot.offsetLeft;
	y = robot.offsetTop;
	// console.log("x,y=" + x + "," + y);
	moved = false;
	if (newX != x) {
		moved = true;
		robot.style.left = x + 2;
		// console.log("offsetLeft=" + robot.offsetLeft);
	}
	if (newY != y) {
		moved = true;
		robot.style.top = y + 2;
		// console.log("offsetTop=" + robot.offsetTop);
	}
	if (moved == true) {
		setTimeout(function() {
			moveTo(robot, newX, newY)
		}, 10);
	}
}

function cellXToX(cellX) {
	return (cellX * CELL_WIDTH);
}

function cellYToY(cellY) {
	return (cellY * CELL_HEIGHT);
}


