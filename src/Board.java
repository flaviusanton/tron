import java.io.BufferedReader;
import java.io.IOException;


/**
 * Contine Reprezentarea in memorie a tablei de joc.Si functii utile de management a tablei de joc
 * @author Raul
 * 
 *
 */
public class Board {
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