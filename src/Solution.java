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
			b.Eval();
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
				if(part_max > max){
					max = part_max;
				}
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
				if(part_min < min){
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
				if(part_max > max){
					max = part_max;
					move = i;
				}
				b.ClearMove(i, true);
			}
		}
		return move;
		
	}
	
	public static void main(String[] args){
		try{
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
			Direction move;
			
			while(!b.IsFinished()){
				move = GetMove(b);
				b.Move(move, true);
				line = in.readLine();
				move = Direction.GetMove(line);
				b.Move(move, false);
				b.WriteMoves();
			}
			
			
		}catch(IOException e){
			e.printStackTrace();
		}
		
	}
}
