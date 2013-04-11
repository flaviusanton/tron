/**
 * Contine reprezentarea in memorie a unei pozitii+Functii utile
 * @author Raul
 *
 */
public class Position {
	private int X;
	private int Y;
	
	
	//O serie de constructori
	public Position(){
		this.X = 0;
		this.Y = 0;
	}
	public Position(Position y){
		X = y.X;
		Y = y.Y;
	}
	public Position(int X, int Y){
		this.X = X;
		this.Y = Y;
	}
	
	public int GetX(){
		return X;
	}
	
	
	public int GetY(){
		return Y;
	}
	
	public void Setx(int X){
		this.X = X;
	}
	public void SetY(int Y){
		this.Y = Y;
	}
	
	public void Move(Direction way){
		switch(way){
		case UP:
			X--;
			break;
		case DOWN:
			X++;
			break;
		case RIGHT:
			Y++;
			break;
		case LEFT:
			Y--;
			break;
		default:
			System.out.println("This should never happen(Position.Move)");
			return;
		}
	}
	public boolean equals(Position p){
		return (X == p.X) && (Y == p.Y);
	}
	
	
	
}
