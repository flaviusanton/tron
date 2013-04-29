import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;

public class Solution {
	public static final int INF = 100000;
	public static int MAXDEPTH = 18;
	public static int playerDistance = 0;

	public static int Maxi(Board b,int depth,int alfa,int beta){
		
		
		if(b.WriteMoves()){//Mutarile se scriu pe Maxi deoarece de aici incepe un set nou de mutari(mutarile sunt simultane)
			//System.out.println("Remiza");
			return -INF+10;//Jucatorii s-au ciocnit :D
						//Am considerat ca pentru Maxi remiza este aproape la fel de proasta ca infrangerea(Nu vrem remiza)
		}
		
		
		if(depth >= MAXDEPTH){
			return b.Eval();
		}
		if(b.IsFinished()){
			if(b.HaveIWon()){
				//System.out.println("Castig");
				return INF;
			}else{
				//.out.println("Pierd");
				return -INF;
			}
		}
		int part_max = -INF,max = -INF;;
		for(Direction i : Direction.ALL){
			if(b.CanMove(i, true)){
				b.Move(i, true);
				part_max = Mini(b,depth+1,alfa,beta);
				
				if (part_max >= beta) {
					b.ClearMove(i, true);
					return beta;
				}
				if(part_max > alfa){
					alfa = part_max;
				}
				/*if(part_max >= max){
					max = part_max;
				}*/
				b.ClearMove(i, true);
			}
		}
		/*if(part_max == -INF){
			System.out.println("Maxi:Nu e mutare");
		}*/
		return alfa;
	}
	
	public static int Mini(Board b,int depth,int alfa,int beta){
		int part_min = INF,min = INF;
		for(Direction i:Direction.ALL){
			if(b.CanMove(i, false)){
				b.Move(i, false);
				part_min = Maxi(b,depth+1,alfa,beta);
				
				if (part_min <= alfa) {
					b.ClearMove(i, false);
					return alfa;
				}
				if(part_min < beta){
					beta = part_min;
				}/*
				if(part_min <= min){
					min = part_min;
				}*/
				b.ClearMove(i, false);
			}
		}
		/*if(part_min == -INF){
			System.out.println("Mini:Nu e mutare");
		}*/
		return beta;
	}
	
	
	//returneaza mutarea conform algoritmului minimax.Jucatorul curent este maxi
	public static Direction GetMove(Board b){
		int max = -INF-1,part_max = -INF;
		Direction move = Direction.UP;
		for(Direction i:Direction.ALL){
			if(b.CanMove(i, true)){
				b.Move(i, true);
				part_max = Mini(b,1,-INF,INF);
				if(part_max >= max){
					max = part_max;
					move = i;
				}
				b.ClearMove(i, true);
			}
		}
		//System.out.println("Move cost:"+max);
		return move;
		
	}
	
	public static void main(String[] args) throws IOException{
		//System.out.println(Long.MAX_VALUE);
		
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
			
			playerDistance = (int)Math.sqrt((MyPos.GetX() - EnemyPos.GetX())*(MyPos.GetX() - EnemyPos.GetX())
					+ (MyPos.GetY() - EnemyPos.GetY())*(MyPos.GetY() - EnemyPos.GetY()));
			
		/*	if (Width + Height < 31) {
				MAXDEPTH = 15;
			} else if (Width + Height < 51) {
				MAXDEPTH = 13;
			} else if (Width + Height < 71) {
				MAXDEPTH = 9;
			} else {
				MAXDEPTH = 7;
			}
			*/
			move = GetMove(b);
			System.out.println(move.toString());
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
	private long[] board;
	
	public static int Width=-1,Height=-1;
	private static char MyChar;
	private static boolean IsFinished = false;
	private static boolean HaveIWon = false;
	
	private static Random rand = new Random();
	
	private static Position queue[] = new Position[2500];
	private boolean[][] visited ;
	private int[][] time; 
	public Board(Position MyPos,Position EnemyPos,int Width, int Height,char MyChar){
		this.MyPos = new Position(MyPos);
		this.EnemyPos = new Position(EnemyPos);
		Board.Width = Width;
		Board.Height = Height;
		this.board = new long[Height];		
		Board.MyChar = MyChar;
		visited = new boolean[Height][Width];
		time = new int[Height][Width];
	}
	
	//Sper sa folosim constructorul asta
	public Board(Board b){
		MyPos = new Position(b.MyPos);
		EnemyPos = new Position(b.EnemyPos);
		//Width = Board.Width;
		//Height = Board.Height;
		board = b.board.clone();
		//MyChar = Board.MyChar;
	}
	
	public void ReadMap(BufferedReader in){
		char[] line;
		int i ,j;
		try{
			for( i = 0 ; i < Height ; i++){
				line = in.readLine().toCharArray();
				for(j = 0 ; j < Width ; j++){
					if(line[j] != '-'){
						board[i] = board[i] | (((long)1)<<j);
					}
					//board[i][j] = line[j];
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
		board[MyPos.GetX()] = board[MyPos.GetX()] | (((long)1)<<MyPos.GetY());
		board[EnemyPos.GetX()] = board[EnemyPos.GetX()] | (((long)1)<<EnemyPos.GetY());// = (MyChar == 'r') ? 'g' : 'r';
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
		if(Pos.GetX() < 0 || Pos.GetX() >= Height || Pos.GetY() < 0 || Pos.GetY() >= Width || ((board[Pos.GetX()] & (((long)1)<<Pos.GetY())) != 0)){
			Pos.Move(Direction.GetOpposite(move));
			return false;
		}
		Pos.Move(Direction.GetOpposite(move));
		return true;
	}
	
	public int Eval(){
		//To Be Done in the future >:)
		
		//if ((Board.Width > 30 || Board.Height > 30) && Solution.playerDistance > 20) {
		//	return 0;
		//}
		return longestReachebleRoad(); 
	}
	
	private int longestReachebleRoad(){
		Position start = new Position(EnemyPos);
		int p=0,u=0,count=0;
		int t,taux;
		queue[p] = start;
		//boolean[][] visited = new boolean[Height][Width];
		
		for (int i = 0; i < Height; i++) {
			for (int j = 0; j < Width; j++) {
				time[i][j] = ((board[i] & (((long)1)<<j)) == 0) ? 10000 : -1;
				visited[i][j] = false;
			}
		}
		time[start.GetX()][start.GetY()] = 1;
		
		while(p <= u){
			start = queue[p++];
			t = time[start.GetX()][start.GetY()];
			for(Direction i: Direction.ALL){
				start.Move(i);
				taux = time[start.GetX()][start.GetY()]; 
				if(isValid(start.GetX(),start.GetY()) && (time[start.GetX()][start.GetY()] > t+1)){
					time[start.GetX()][start.GetY()] = t+1;
					queue[++u] = new Position(start);
				}
				start.Move(Direction.GetOpposite(i));
			}
		}
		p = 0;
		start = new Position(MyPos);
		time[start.GetX()][start.GetY()] = 0;
		visited[start.GetX()][start.GetY()] = true;
		queue[p] = start;
		while(p >= 0){
			start = queue[p--];
			t = time[start.GetX()][start.GetY()];
			for(Direction i: Direction.ALL){
				start.Move(i);
				if(isValid(start.GetX(),start.GetY()) && (GetRanomRange(time[start.GetX()][start.GetY()]) > t+1) && (!visited[start.GetX()][start.GetY()]) ){
					if(t > count){
						count = t;
					}
					visited[start.GetX()][start.GetY()] = true;
					time[start.GetX()][start.GetY()] = t+1;
					queue[++p] = new Position(start);
				}
				start.Move(Direction.GetOpposite(i));
			}
		}
		return count;
	}
	private int GetRanomRange(int t){
		return t+((rand.nextInt()+1)%((t>>2)+2));
		
	}
	
	private int reachableCells(Position start) {
		Queue<Position> q = new LinkedBlockingQueue<Position>();
		boolean[][] visited = new boolean[Height][Width];
		
		for (int i = 0; i < Height; i++) {
			for (int j = 0; j < Width; j++) {
				visited[i][j] = ((board[i] & (((long)1)<<j)) == 0) ? false : true;
			}
		}
		q.add(start);
		
		int[] dx = {-1, 0, 1, 0};
		int[] dy = {0, 1, 0, -1};
		int count = 0;
		
		while (!q.isEmpty()) {
			Position pos = q.poll();
			visited[pos.GetX()][pos.GetY()] = true;
			
			for (int i = 0; i < 4; i++) {
				if (isValid(pos.GetX() + dx[i], pos.GetY() + dy[i]) 
						&& !visited[pos.GetX() + dx[i]][pos.GetY() + dy[i]]) {
							q.add(new Position(pos.GetX() + dx[i], pos.GetY() + dy[i]));
							visited[pos.GetX() + dx[i]][pos.GetY() + dy[i]] = true;
							count ++;
						}
			}
		}
		return count;
		
	}
	
	private boolean isValid(int x, int y) {
		if (x < 0 || y < 0)
			return false;
		if (x >= Height || y >= Width)
			return false;
		return true;
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
			board[MyPos.GetX()] = board[MyPos.GetX()] ^ (((long)1)<<MyPos.GetY());
			MyPos.Move(Direction.GetOpposite(move));
		}else{
			board[EnemyPos.GetX()] = board[EnemyPos.GetX()] ^ (((long)1)<<EnemyPos.GetY());
			EnemyPos.Move(Direction.GetOpposite(move));
		}
	}
	

}

//Enums

enum Direction{
	UP,DOWN,LEFT,RIGHT,NONE;
	public static Direction[] ALL = {UP,LEFT,RIGHT,DOWN};
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
