
enum Direction{
	UP,DOWN,LEFT,RIGHT,NONE;
	public static Direction[] ALL = {UP,DOWN,LEFT,RIGHT};
	public static Direction GetOpposite(Direction d){
		switch(d){
		case UP:
			return DOWN;
		case DOWN:
			return UP;
		case LEFT:
			return RIGHT;
		case RIGHT:
			return LEFT;
		default:
			System.out.println("This should never happen(Direction.getOpposite");
			return NONE;
		}
	}
	
	public String toString(){
		switch(this){
		case UP:
			return "UP";
		case DOWN:
			return "DOWN";
		case LEFT:
			return "LEFT";
		case RIGHT:
			return "RIGHT";
		default:
			System.out.println("This should never happen(Direction.toString");
			return "NONE";
		
		}
	}
	public static Direction GetMove(String s){
		if(s.equals("UP")){return UP;}
		if(s.equals("DOWN")){return DOWN;}
		if(s.equals("LEFT")){return LEFT;}
		if(s.equals("RIGHT")){return RIGHT;}
		System.out.println("This should never happen(Direcion.GetMove)");
		return NONE;//
	}
}