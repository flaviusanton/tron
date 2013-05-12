import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Queue;
import java.util.LinkedList;
import java.util.Random;

public class Solution {
	public static final int INF = 100000;
	public static int MAXDEPTH = 18;
	public static HashMap<char[][], Integer> scores = new HashMap<char[][], Integer>();
	
	public static int Maxi(Board b,int depth,int alfa,int beta){
		
		
		if(b.WriteMoves()){//Mutarile se scriu pe Maxi deoarece de aici incepe un set nou de mutari(mutarile sunt simultane)
			return 0;//Jucatorii s-au ciocnit :D
		}
		
		
		if(depth >= MAXDEPTH){
			return b.Eval();
		}
		if(b.IsFinished()){
			if(b.HaveIWon()){
				return INF;
			}else{
				return -INF;
			}
		}
		int part_max = -INF;
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
				b.ClearMove(i, true);
			}
		}
		return alfa;
	}
	
	public static int Mini(Board b,int depth,int alfa,int beta){
		int part_min = INF;
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
				}
				b.ClearMove(i, false);
			}
		}
			return beta;
	}
	
	
	//returneaza mutarea conform algoritmului minimax.Jucatorul curent este maxi
	public static Direction GetMove(Board b){
		int max = -INF,part_max = -INF;
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
			
			if (Width + Height < 31) {
				MAXDEPTH = 17;
			} else if (Width + Height < 51) {
				MAXDEPTH = 15;
			} else if (Width + Height < 71) {
				MAXDEPTH = 11;
			} else {
				MAXDEPTH = 9;
			}
			
			if (b.canReach(MyPos, EnemyPos))
				move = GetMove(b);
			else
				move = PathPool.GetMove(b);
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
	private char[][] board;
	
	
	public static int Width=-1,Height=-1;
	private static char MyChar;
	private static boolean IsFinished = false;
	private static boolean HaveIWon = false;
	public Board(Position MyPos,Position EnemyPos,int Width, int Height,char MyChar){
		this.MyPos = new Position(MyPos);
		this.EnemyPos = new Position(EnemyPos);
		Board.Width = Width;
		Board.Height = Height;
		this.board = new char[Height][Width];		
		Board.MyChar = MyChar;
	}
	
	public char[][] getBoard() {
		return board;
	}
	
	public Direction searchForKeyPositions() {
		boolean[][] visited = new boolean[Height][Width];
		
		for (int i = 0; i < Height; i++) {
			for (int j = 0; j < Width; j++) {
				visited[i][j] = (board[i][j] == '-') ? false : true;
			}
		}
		
		for (int i = 0; i < Height; i++) {
			for (int j = 0; j < Width; j++) {
				if (!visited[i][j] && canReach(MyPos, new Position(i, j))) {
					if ((distFrom(MyPos, new Position(i, j)) < distFrom(EnemyPos, new Position(i, j)))
							&& (reachableCells(MyPos) > reachableCells(EnemyPos))) {
						return goTowards(new Position(i, j));
						}
				}
			}
		}
		if (canReach(MyPos, new Position(Height/2, Width/2)))
			return goTowards(new Position(Height/2, Width/2));
		else
			return goTowards(EnemyPos);
	}
	
	public Direction goTowards(Position position) {
		Queue<Position> q = new LinkedList<Position>();
		boolean[][] visited = new boolean[Height][Width];
		
		int[][] from = new int[Height][Width];
		
		for (int i = 0; i < Height; i++) {
			for (int j = 0; j < Width; j++) {
				visited[i][j] = (board[i][j] == '-') ? false : true;
			}
		}
		q.add(MyPos);
		
		int[] dx = {-1, 0, 1, 0};
		int[] dy = {0, 1, 0, -1};
		
		while (!q.isEmpty()) {
			Position pos = q.poll();
			visited[pos.GetX()][pos.GetY()] = true;
			
			for (int i = 0; i < 4; i++) {
				if (isValid(pos.GetX() + dx[i], pos.GetY() + dy[i]) 
						&& !visited[pos.GetX() + dx[i]][pos.GetY() + dy[i]]) {
							q.add(new Position(pos.GetX() + dx[i], pos.GetY() + dy[i]));
							visited[pos.GetX() + dx[i]][pos.GetY() + dy[i]] = true;
							from[pos.GetX() + dx[i]][pos.GetY() + dy[i]] = i;
						}
			}
		}
		return goBack(position, from);
	}

	private Direction goBack(Position pos, int[][] from) {
		Position newPos;
		int[] dx = {-1, 0, 1, 0};
		int[] dy = {0, 1, 0, -1};
		
		newPos = new Position(pos.GetX() - dx[from[pos.GetX()][pos.GetY()]], 
							  pos.GetY() - dy[from[pos.GetX()][pos.GetY()]]);
		
		if (newPos.GetX() == MyPos.GetX() && newPos.GetY() == MyPos.GetY()) {
			switch (from[pos.GetX()][pos.GetY()]) {
				case 0:
					return Direction.UP;
				case 1:
					return Direction.RIGHT;
				case 2:
					return Direction.DOWN;
				case 3:
					return Direction.LEFT;
				default:
					return Direction.NONE;
			}
		}
		return goBack(newPos, from);
	}

	private int distFrom(Position start, Position end) {
		Queue<Position> q = new LinkedList<Position>();
		boolean[][] visited = new boolean[Height][Width];
		
		int[][] dist = new int[Height][Width];
		
		for (int i = 0; i < Height; i++) {
			for (int j = 0; j < Width; j++) {
				visited[i][j] = (board[i][j] == '-') ? false : true;
			}
		}
		q.add(start);
		
		int[] dx = {-1, 0, 1, 0};
		int[] dy = {0, 1, 0, -1};
		
		while (!q.isEmpty()) {
			Position pos = q.poll();
			visited[pos.GetX()][pos.GetY()] = true;
			
			for (int i = 0; i < 4; i++) {
				
				if (pos.GetX() + dx[i] == end.GetX() 
						&& pos.GetY() + dy[i] == end.GetY()) {
					return dist[pos.GetX()][pos.GetY()] + 1;
				}
				if (isValid(pos.GetX() + dx[i], pos.GetY() + dy[i]) 
						&& !visited[pos.GetX() + dx[i]][pos.GetY() + dy[i]]) {
							q.add(new Position(pos.GetX() + dx[i], pos.GetY() + dy[i]));
							visited[pos.GetX() + dx[i]][pos.GetY() + dy[i]] = true;
							
							dist[pos.GetX() + dx[i]][pos.GetY() + dy[i]] = 
									dist[pos.GetX()][pos.GetY()] + 1;
						}
			}
		}
		return -1;
	}

	public boolean canReach(Position from, Position where) {
		Queue<Position> q = new LinkedList<Position>();
		boolean[][] visited = new boolean[Height][Width];
		
		for (int i = 0; i < Height; i++) {
			for (int j = 0; j < Width; j++) {
				visited[i][j] = (board[i][j] == '-') ? false : true;
			}
		}
		q.add(from);
		
		int[] dx = {-1, 0, 1, 0};
		int[] dy = {0, 1, 0, -1};
		
		while (!q.isEmpty()) {
			Position pos = q.poll();
			visited[pos.GetX()][pos.GetY()] = true;
			
			for (int i = 0; i < 4; i++) {
				if (pos.GetX() + dx[i] == where.GetX() 
						&& pos.GetY() + dy[i] == where.GetY()) {
					return true;
				}
				
				if (isValid(pos.GetX() + dx[i], pos.GetY() + dy[i]) 
						&& !visited[pos.GetX() + dx[i]][pos.GetY() + dy[i]]) {
					
					q.add(new Position(pos.GetX() + dx[i], pos.GetY() + dy[i]));
					visited[pos.GetX() + dx[i]][pos.GetY() + dy[i]] = true;
				}
			}
		}
		return false;
	}

	//Sper sa folosim constructorul asta
	public Board(Board b){
		MyPos = new Position(b.MyPos);
		EnemyPos = new Position(b.EnemyPos);
		board = b.board.clone();
	}
	
	public void ReadMap(BufferedReader in){
		char[] line;
		int i ,j;
		try{
			for( i = 0 ; i < Height ; i++){
				line = in.readLine().toCharArray();
				for(j = 0 ; j < Width ; j++){
					board[i][j] = line[j];
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
		board[MyPos.GetX()][MyPos.GetY()] = MyChar;
		board[EnemyPos.GetX()][EnemyPos.GetY()] = (MyChar == 'r') ? 'g' : 'r';
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
	
	public boolean CheckMove(Direction move,Position Pos){
		if(move == Direction.NONE){
			return false;//Just to make sure
		}
		Pos.Move(move);
		if(Pos.GetX() < 0 || Pos.GetX() >= Height || Pos.GetY() < 0 || Pos.GetY() >= Width || board[Pos.GetX()][Pos.GetY()] != '-'){
			Pos.Move(Direction.GetOpposite(move));
			return false;
		}
		Pos.Move(Direction.GetOpposite(move));
		return true;
	}
	
	public int Eval(){
		return reachableCellsExclusive(MyPos, EnemyPos);
	}
	
	private int reachableCellsExclusive(Position me, Position him) {
		Queue<Position> myQ  = new LinkedList<Position>();
		Queue<Position> hisQ = new LinkedList<Position>();
		boolean[][] visited = new boolean[Height][Width];
		
		for (int i = 0; i < Height; i++) {
			for (int j = 0; j < Width; j++) {
				visited[i][j] = (board[i][j] == '-') ? false : true;
			}
		}
		myQ.add(me);
		hisQ.add(him);
		
		int[] dx = {-1, 0, 1, 0};
		int[] dy = {0, 1, 0, -1};
		int diff = 0;
		
		while (!myQ.isEmpty() || !hisQ.isEmpty()) {
			Position pos;
			if (!myQ.isEmpty()) {
				pos = myQ.poll();
				visited[pos.GetX()][pos.GetY()] = true;
			
				for (int i = 0; i < 4; i++) {
					if (isValid(pos.GetX() + dx[i], pos.GetY() + dy[i]) 
						&& !visited[pos.GetX() + dx[i]][pos.GetY() + dy[i]]) {
							myQ.add(new Position(pos.GetX() + dx[i], pos.GetY() + dy[i]));
							visited[pos.GetX() + dx[i]][pos.GetY() + dy[i]] = true;
							diff++;
					}
				}
			}
			
			if (!hisQ.isEmpty()) {
				pos = hisQ.poll();
				visited[pos.GetX()][pos.GetY()] = true;
			
				for (int i = 0; i < 4; i++) {
					if (isValid(pos.GetX() + dx[i], pos.GetY() + dy[i]) 
						&& !visited[pos.GetX() + dx[i]][pos.GetY() + dy[i]]) {
							hisQ.add(new Position(pos.GetX() + dx[i], pos.GetY() + dy[i]));
							visited[pos.GetX() + dx[i]][pos.GetY() + dy[i]] = true;
							diff--;
					}
				}
			}
			
		}
		return diff + numberOfVisitedNeighbours(me);
	}

	private int numberOfVisitedNeighbours(Position me) {
		int count = 0;
		int[] dx = {-1, 0, 1, 0};
		int[] dy = {0, 1, 0, -1};
		
		for (int i = 0; i < 4; ++i) {
			if (isValid(me.GetX() + dx[i], me.GetY() + dy[i])) {
				if (board[me.GetX() + dx[i]][me.GetY() + dy[i]] != '-')
					count++;
			}
		}
		return count;
	}

	public int bfs(Position start) {
		Queue<Position> q = new LinkedList<Position>();
		boolean[][] visited = new boolean[Height][Width];
		for (int i = 0;i < Height;i ++)
			for (int j = 0;j < Width;j ++)
				if (board[i][j] == '-') visited[i][j] = false;
				else visited[i][j] = true;
		q.add(start);
		visited[start.GetX()][start.GetY()] = true;
		int[] dx = {-1,0,1,0};
		int[] dy = {0,1,0,-1};
		int counter = 0;
		while (!q.isEmpty()) {
			Position pos = q.poll();
			if (pos.GetX() == EnemyPos.GetX() && pos.GetY() == EnemyPos.GetY()) {
				return counter;
			}
			counter ++;
			for (int k = 0;k < 4;k ++)
				if (isValid(pos.GetX() + dx[k], pos.GetY() + dy[k]) && 
						visited[pos.GetX() + dx[k]][pos.GetY() + dy[k]] == false) {
					q.add(new Position(pos.GetX() + dx[k],pos.GetY() + dy[k]));
					visited[pos.GetX() + dx[k]][pos.GetY() + dy[k]] = true;
				}
				else if (isValid(pos.GetX() + dx[k], pos.GetY() + dy[k]) &&
						pos.GetX() + dx[k] == EnemyPos.GetX() && pos.GetY() + dy[k] == EnemyPos.GetY()) {
					return counter;
				}
		}
		return -1;
	}
	private int reachableCells(Position start) {
		Queue<Position> q = new LinkedList<Position>();
		boolean[][] visited = new boolean[Height][Width];
		
		for (int i = 0; i < Height; i++) {
			for (int j = 0; j < Width; j++) {
				visited[i][j] = (board[i][j] == '-') ? false : true;
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
			board[MyPos.GetX()][MyPos.GetY()] = '-';
			MyPos.Move(Direction.GetOpposite(move));
		}else{
			board[EnemyPos.GetX()][EnemyPos.GetY()] = '-';
			EnemyPos.Move(Direction.GetOpposite(move));
		}
	}

	public Position GetMyPos() {
		return MyPos;
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
	
	public Direction GetDirectionOf(Position p) {
			if(X < p.X){
				return Direction.DOWN;
			}
			if(X > p.X){
				return Direction.UP;
			}
			if(Y < p.Y){
				return Direction.RIGHT;
			}
			return Direction.LEFT;
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

class PathPool {
	static final int POOLSIZE = 200;
	static int OFFSET = 10;
	static float PRESSURE = 1;
	static float PRESSUREUP = 1.8f;
	static float PRESSUREDOWN = 0.7f;
	static int ITERATIONS = 6;
	static final Random gen = new Random();
	private static CandidatePath[] pool = new CandidatePath[POOLSIZE];
	private static int PathLen = -1;
	static boolean initialBoard[][] = new boolean[Board.Height][Board.Width];
	
	public static void GenInitialPool(Board b) {
		boolean viz[][] = new boolean[Board.Height][Board.Width];
		Position[] path = new Position[CandidatePath.MAXPATHLEN];
		int p = 0;
		for (int i = 0; i < Board.Height; i++) {
			for (int j = 0; j < Board.Width; j++) {
				if (b.getBoard()[i][j] != '-') {
					viz[i][j] = true;
					initialBoard[i][j] = true;
				}
			}
		}
		path[0] = b.GetMyPos();
		viz[path[0].GetX()][path[0].GetY()] = true;
		Dfs(viz, path, p,pool);
	}

	public static void Dfs(boolean[][] viz, Position[] path, int p,CandidatePath[] pool) {
		Position aux = path[p];
		Position pos = GetNewPos(aux, viz);
		if (pos == null) {
			CandidatePath newPath = new CandidatePath(path, p);
			AddNewPath(newPath,pool);
		} else {
			do{
				viz[pos.GetX()][pos.GetY()] = true;
				path[++p] = pos;
				Dfs(viz, path, p,pool);
				path[p--] = null;
				pos = GetNewPos(aux, viz);
			}while(pos != null);
		}
		
	}

	public static void AddNewPath(CandidatePath path,CandidatePath[] pool) {
		if(PathLen<POOLSIZE-1){
			pool[++PathLen] = path;
			PushUp(PathLen,pool);
		}else if(path.len > pool[PathLen].len){
			pool[PathLen] = path;
			PushUp(PathLen,pool);
		}
	}
	
	public static void PushUp(int pos,CandidatePath[] pool){
		CandidatePath aux;
		while(pos > 0 && pool[pos].len > pool[pos-1].len){
			aux = pool[pos];
			pool[pos] = pool[pos-1];
			pool[pos-1] = aux;
			pos--;
		}
	}
	
	// For testing purposes
	public static void PrintPool() {
		int i, j;
		System.out.println(PathLen);
		for (i = 0; i <= PathLen; i++) {
			/*for (j = 0; j <= pool[i].len; j++) {
				System.out.print(pool[i].path[j]);
			}*/
			System.out.print(pool[i].len+" ");
			if(i%20 == 0){
				System.out.println();
			}
		}

	}

	public static Position GetNewPos(Position pos, boolean[][] viz) {
		int dx[] = { 0, 1, -1, 0 };
		int dy[] = { -1, 0, 0, 1 };
		Position v[] = new Position[4];
		int nvecini[] = new int[4];
		int l = -1;
		int nV = 0;
		int X, Y, R;
		for (int i = 0; i < 4; i++) {
			X = pos.GetX() + dx[i];
			Y = pos.GetY() + dy[i];
			if ((X>=0) && (Y>=0) && (X<viz.length) && (Y < viz[0].length) && (!viz[X][Y])) {
				l++;
				nvecini[l] = GetNVecini(pos.GetX() + dx[i], pos.GetY() + dy[i],
						viz);
				v[l] = new Position(X, Y);
				nV += nvecini[l];
			}
		}
		if(nV == 0){
			return null;
		}
		R = gen.nextInt(nV) + 1;
		for (int i = 0; i <= l; i++) {
			R -= nvecini[i];
			if (R <= 0) {
				return v[i];
			}
		}
		return null;

	}

	public static int GetNVecini(int x, int y, boolean viz[][]) {
		int rez = 1;
		int dx[] = { 0, 1, -1, 0 };
		int dy[] = { -1, 0, 0, 1 };
		for (int i = 0; i < 4; i++) {
			if ((x+dx[i]>=0) && (y+dy[i]>=0) && (x+dx[i]<viz.length) && (y+dy[i] < viz[0].length) && (!viz[x + dx[i]][y + dy[i]])) {
				rez++;
			}
		}

		return rez;
	}

	public static void GetNextGen(CandidatePath[] fathers, CandidatePath[] children){
		int i, j, start;
		int PrePathLen = PathLen;
		int k;
		CandidatePath aux;
		PathLen = -1;
		for(i = 0 ; i <= PrePathLen; i++){
			start = fathers[i].len;
			start = start - (start*OFFSET)/100;
			aux = fathers[i];
			for(j = start ; j <= fathers[i].len;j++){
				
				aux.viz[aux.path[j].GetX()][aux.path[j].GetY()] = false;
				aux.path[j] = null;
			}
			for(j = 0 ;j < start ;j ++ ){
				if(DoesItMutate(j,start)){
					k = j;
					j++;
					for(;j< start;j++){
						//Curat restul mutarilor
						aux.viz[aux.path[j].GetX()][aux.path[j].GetY()] = false;
						aux.path[j] = null;
					}
					Dfs(fathers[i].viz,fathers[i].path,k,children);
				}
			}
			fathers[i] = null;
		}
		
	}
	
	public static boolean DoesItMutate(int start,int end){
		float s2 = ((float)start)*PRESSURE;
		start = ((int)s2);
		
		if(start >= end){
			return true;
		}
		int aux = gen.nextInt(end-start);
		return  aux == 0;
	}
	
	
	public static Direction GetMove(Board b) {
		GenInitialPool(b);
		CandidatePath[] children = new CandidatePath[POOLSIZE];
		CandidatePath[] aux;
		int M1 =  pool[0].len,M2;
		Direction rez = Direction.UP;
		if(pool[0].path[1] != null){
			rez = b.GetMyPos().GetDirectionOf(pool[0].path[1]);
		}
		
		for(int i = 0 ;i<= ITERATIONS;i++){
			GetNextGen(pool, children);
			aux = pool;
			pool = children;
			children = aux;
			if(pool[0] == null){
				break;
			}
			M2 = pool[0].len;
			if(((float)M2) / ((float)M1) <= 0.1){
				PRESSURE *= 1.4;
			}else{
				PRESSURE *= 0.7;
			}
			if(M2 > M1){
				M1 = M2;
				rez = b.GetMyPos().GetDirectionOf(pool[0].path[1]);
			}
		}
		
		return rez;
	}

	
	/*
	 * Used only for testing
	 */
	public static Direction Solve(Board b){
		
		GenInitialPool(b);
		CandidatePath[] children = new CandidatePath[POOLSIZE];
		CandidatePath[] aux;
		int M1 =  pool[0].len,M2;
		Direction rez = b.GetMyPos().GetDirectionOf(pool[0].path[1]);
		
		for(int i = 0 ;i<= ITERATIONS;i++){
			GetNextGen(pool, children);
			aux = pool;
			pool = children;
			children = aux;
			M2 = pool[0].len;
			if(((float)M2) / ((float)M1) <= 0.1){
				PRESSURE *= 1.4;
			}else{
				PRESSURE *= 0.7;
			}
			if(M2 > M1){
				M1 = M2;
				rez = b.GetMyPos().GetDirectionOf(pool[0].path[1]);
			}
		}
		//System.out.println("Maximul este:"+M1);
		//PrintPool();
		return rez;
	}
	
	
	static class CandidatePath {
		static final int MAXPATHLEN = 2500;
		static final int MAXLEN = 50;
		Position path[];
		boolean viz[][];
		int len = 0;

		public CandidatePath() {
			path = new Position[MAXPATHLEN];
			viz = new boolean[MAXLEN][MAXLEN];
			len = 0;
		}

		public CandidatePath(CandidatePath p) {
			path = p.path.clone();
			viz = p.viz.clone();
			len = p.len;
		}

		public CandidatePath(Position[] p, int len) {
			path = p.clone();
			int W,H;
			H = initialBoard.length;
			W = initialBoard[0].length;
			viz = new boolean[H][];
			for(int i = 0 ; i < H; i++){
				viz[i] = initialBoard[i].clone();
			}
			this.len = len;
			for(int i = 0 ; i <= len ; i++){
				viz[p[i].GetX()][p[i].GetY()] = true;
			}
		}

	}
}

