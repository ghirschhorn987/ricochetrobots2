function move() {
  moveTo(redRobot, redRobot.offsetLeft, 600);
}

function moveTo(robot, newX, newY) {
   console.log("entering moveTo( " + newX + "," + newY + ")");
   x = robot.offsetLeft;
   y = robot.offsetTop;
   console.log("x,y=" + x + "," + y);
   moved = false;
   if (newX != x) {
	 moved = true;
	 robot.style.left = x + 10;
	 console.log("offsetLeft=" + robot.offsetLeft);
   }	
   if (newY != y) {
	 moved = true;
	 robot.style.top = y + 10;   
	 console.log("offsetTop=" + robot.offsetTop);
	}
   if (moved == true) {
     setTimeout(function(){moveTo(robot, newX, newY)}, 100);
   }
}


function init(){
  var canvasBoard = document.getElementById("canvasBoard");
  var contextBoard = canvasBoard.getContext("2d");
  var width = 40;
  var height = 40;
  var cells = 16;

  for (var x = 0; x < (width * cells); x = x + width) {
    for (var y = 0; y < (height * cells); y = y + height) {
      contextBoard.strokeRect(x,y,width,height);
    }
  }
}




