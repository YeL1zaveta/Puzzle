package puzz;

import sac.StateFunction;
import puzzle.PUZZLE;
import sac.State;


public class Manhattan extends StateFunction {
    @Override
    public double calculate(State state) {
        PUZZLE p = (PUZZLE) state;
        int n = p.board.length;
        int h=0;
        for (int i=0; i<n; i++) {
          for (int j=0; j<n; j++) {
            int value = p.board[i][j];
            if (p.board[i][j] != 0) {
              //obliczamy miejsce docelowe
              int destinationI = value / n;
              int destinationJ = value % n;
              h += Math.abs(i - destinationI) + Math.abs(j - destinationJ);
            }
                }
          }
        return h;
        }
    }
