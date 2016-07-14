package org.hirschhorn.ricochet;

public class Potential {
    
	private int score;
	
	public Potential(){
		score = 100;
	}
	
	public int adjustIfMoveSameColorAsTarget(Move move){
		Color targetColor = move.getBoardState().getChosenTarget().getColor();
		Color moveColor = move.getMoveAction().getRobot();
		if (moveColor.equals(targetColor)){
			score = score + 5;
		}
		return score;
	}
	
	public int getScore(){
		return score;
	}

	public int compareTo(Potential other) {
		return Integer.compare(this.getScore(), other.getScore());
	}
}
