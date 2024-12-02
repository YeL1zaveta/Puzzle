package puzz;
import puzzle.PUZZLE;
import sac.State;
import sac.StateFunction;
 
public class MisplacedTile extends StateFunction{
	  @Override
	  public double calculate(State state) {
		     PUZZLE p = (PUZZLE) state;
		     int n = p.board.length;
		     int h=0;
		     int value=0;
		     for (int i=0; i<n; i++) {
		       for (int j=0; j<n; j++) {
		         if (p.board[i][j] != value && p.board[i][j] != 0) {
		                      h++;
		                  }
		                  value++;
		       }
		     }
		    
		     return h;
		   }
	}