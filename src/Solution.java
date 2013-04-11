import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Solution {
	public static final int INF = 1000;
	public static final int MAXDEPH = 20;
	
	public static int Maxi(Board b,int depth){
		
		
		if(b.WriteMoves()){//Mutarile se scriu pe Maxi deoarece de aici incepe un set nou de mutari(mutarile sunt simultane)
			return 0;//Jucatorii s-au ciocnit :D
		}
		
		
		if(depth >= MAXDEPH){
			return b.Eval();
		}
		if(b.IsFinished()){
			if(b.HaveIWon()){
				return INF;
			}else{
				return -INF;
			}
		}
		int max = -INF,part_max = -INF;
		for(Direction i: Direction.ALL){
			if(b.CanMove(i, true)){
				b.Move(i, true);
				part_max = Maxi(b,depth+1);
				if(part_max >= max){
					max = part_max;
				}
				b.ClearMove(i, true);
			}
		}
		return max;
		
		
	}
	
	public static int Mini(Board b,int depth){
		int min = INF,part_min = INF;
		for(Direction i:Direction.ALL){
			if(b.CanMove(i, false)){
				b.Move(i, false);
				part_min = Maxi(b,depth+1);
				if(part_min <= min){
					min = part_min;
				}
				b.ClearMove(i, false);
			}
		}
			return min;
	}
	
	
	//returneaza mutarea conform algoritmului minimax.Jucatorul curent este maxi
	public static Direction GetMove(Board b){
		int max = -INF,part_max = -INF;
		Direction move = Direction.NONE;
		for(Direction i:Direction.ALL){
			if(b.CanMove(i, true)){
				b.Move(i, true);
				part_max = Mini(b,1);
				if(part_max >= max){
					max = part_max;
					move = i;
				}
				b.ClearMove(i, true);
			}
		}
		return move;
		
	}
	
	public static void main(String[] args) throws IOException{
		
		//try{
			String line;
			String[] tokens;
			Position MyPos,EnemyPos;
			char MyChar;
			int Width,Height;
			BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
			line = in.readLine();
			MyChar = line.toCharArray()[0];
			line = in.readLine();
			tokens = line.split(" ");
			if(MyChar == 'r'){
				MyPos = new Position(Integer.parseInt(tokens[0]), Integer.parseInt(tokens[1]));
				EnemyPos = new Position(Integer.parseInt(tokens[2]),Integer.parseInt(tokens[3]));
			}else{
				MyPos = new Position(Integer.parseInt(tokens[2]),Integer.parseInt(tokens[3]));
				EnemyPos = new Position(Integer.parseInt(tokens[0]), Integer.parseInt(tokens[1]));
			}
			line = in.readLine();
			tokens = line.split(" ");
			Height = Integer.parseInt(tokens[0]);
			Width = Integer.parseInt(tokens[1]);
			Board b = new Board(MyPos, EnemyPos, Width, Height, MyChar);
			b.ReadMap(in);
			Direction move;
			
			while(!b.IsFinished()){
				move = GetMove(b);
				b.Move(move, true);
				System.out.println(move.toString());
				line = in.readLine();
				move = Direction.GetMove(line);
				b.Move(move, false);
				b.WriteMoves();
			}
			
			
	//	}catch(IOException e){
		//	e.printStackTrace();
		//}
		
	}
}



//Position Class
/**
 * Contine Reprezentarea in memorie a tablei de joc.Si functii utile de management a tablei de joc
 * @author Raul
 * 
 *
 */
class Board {
	private Position MyPos,EnemyPos;
	private char[][] Board;
	
	
	private static int Width=-1,Height=-1;
	private static char MyChar;
	private static boolean IsFinished = false;
	private static boolean HaveIWon = false;
	public Board(Position MyPos,Position EnemyPos,int Width, int Height,char MyChar){
		this.MyPos = new Position(MyPos);
		this.EnemyPos = new Position(EnemyPos);
		this.Width = Width;
		this.Height = Height;
		this.Board = new char[Width][Height];		
		this.MyChar = MyChar;
	}
	
	//Sper sa folosim constructorul asta
	public Board(Board b){
		MyPos = new Position(b.MyPos);
		EnemyPos = new Position(b.EnemyPos);
		Width = b.Width;
		Height = b.Height;
		Board = b.Board.clone();
		MyChar = b.MyChar;
	}
	
	public void ReadMap(BufferedReader in){
		char[] line;
		int i ,j;
		try{
			for( i = 0 ; i < Height ; i++){
				line = in.readLine().toCharArray();
				for(j = 0 ; j < Width ; j++){
					Board[i][j] = line[j];
				}
			}
		}catch (IOException e){
			e.printStackTrace();
			
		}
	}
	
	//Se garanteaza ca mutarea este valida.MyMove este true daca muta botul simulat sau inamicul.Daca este o mutare invalida 
	//se seteaza flagul de final de joc si de castig(Se considera remiza la fel de proasta ca o infrangere)(Aici ar mai trebui lucrat)
	public void Move(Direction move,boolean MyMove){
		if(MyMove){
			if(!CheckMove(move, MyPos)){
				HaveIWon = false;
				IsFinished = true;
			}
			MyPos.Move(move);
		}else{
			if(!CheckMove(move, EnemyPos)){
				
				HaveIWon = (IsFinished)?false:true;//Daca s-a terminat jocul inseamna ca mutarea anterioara a botului meu a fost pierzatoarea
				IsFinished = true;
			}
			EnemyPos.Move(move);
		}
	}
	//Scrie mutarile in board.Intoarce true daca cei 2 jucatori s-au ciocnit
	public boolean WriteMoves(){
		Board[MyPos.GetX()][MyPos.GetY()] = MyChar;
		Board[EnemyPos.GetX()][EnemyPos.GetY()] = (MyChar == 'r') ? 'g' : 'r';
		return MyPos.equals(EnemyPos);
	}
	
	//Intoarce true daca jucatorul poate efecuta mutarea.DoIMove este true daca muta jucatorul simulat
	public boolean CanMove(Direction move,boolean DoIMove){
		if(DoIMove){
			return CheckMove(move, MyPos);
		}else{
			return CheckMove(move, EnemyPos);
		}
	}
	
	private boolean CheckMove(Direction move,Position Pos){
		if(move == Direction.NONE){
			return false;//Just to make sure
		}
		Pos.Move(move);
		if(Pos.GetX() < 0 || Pos.GetX() >= Height || Pos.GetY() < 0 || Pos.GetY() >= Width || Board[Pos.GetX()][Pos.GetY()] != '-'){
			Pos.Move(Direction.GetOpposite(move));
			return false;
		}
		Pos.Move(Direction.GetOpposite(move));
		return true;
	}
	
	public int Eval(){
		//To Be Done in the future >:)
		return 0;
	}
	public boolean IsFinished(){
		return IsFinished;
	}
	
	public boolean HaveIWon(){
		return HaveIWon;
	}
	//Face roll-back pentru mutarea move.DoIMove determina ce jucator a facut mutarea
	public void ClearMove(Direction move,boolean DoIMove){
		if(DoIMove){
			Board[MyPos.GetX()][MyPos.GetY()] = '-';
			MyPos.Move(Direction.GetOpposite(move));
		}else{
			Board[EnemyPos.GetX()][EnemyPos.GetY()] = '-';
			EnemyPos.Move(Direction.GetOpposite(move));
		}
	}
	

}

//Enums

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


//Position
/**
 * Contine reprezentarea in memorie a unei pozitii+Functii utile
 * @author Raul
 *
 */
class Position {
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
