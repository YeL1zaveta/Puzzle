package puzz;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import sac.graph.GraphState;
import sac.graph.GraphStateImpl;
import sac.StateFunction;
import sac.graph.AStar;
import sac.graph.GraphSearchAlgorithm;

public class PUZZLE extends GraphStateImpl {
    protected int[][] board;
    int n =3;
    int size =n*n;
    protected int emptyRow, emptyCol;
    private static Random rand = new Random(123);
    

    public PUZZLE(int n) {
        this.n = n;
        this.size = n * n;
        board = new int[n][n];
        int value = 0;

        // Inicjalizacja planszy w ułożonym stanie
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                board[i][j] = value++;
            }
        }

        // Pozycja pustego pola 
        emptyRow = n - 1;
        emptyCol = n - 1;
    }
    public PUZZLE(PUZZLE toCopy) {
        board = new int[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                board[i][j] = toCopy.board[i][j];
            }
        }
        this.emptyRow = toCopy.emptyRow;
        this.emptyCol = toCopy.emptyCol;
    }
    
	
	 // Metoda zwracająca możliwe ruchy
    public List<int[]> getPossibleMoves() {
        List<int[]> moves = new ArrayList<>();
        if (emptyRow > 0) moves.add(new int[]{emptyRow - 1, emptyCol}); // w górę
        if (emptyRow < n - 1) moves.add(new int[]{emptyRow + 1, emptyCol}); // w dół
        if (emptyCol > 0) moves.add(new int[]{emptyRow, emptyCol - 1}); // w lewo
        if (emptyCol < n - 1) moves.add(new int[]{emptyRow, emptyCol + 1}); // w prawo
        return moves;
    }

    // Metoda wykonująca ruch
    public void makeMove(int newRow, int newCol) {
        // Zamień wartości miejsc na planszy
        int temp = board[newRow][newCol];
        board[newRow][newCol] = board[emptyRow][emptyCol];
        board[emptyRow][emptyCol] = temp;

        // Zaktualizuj pozycję pustego pola
        emptyRow = newRow;
        emptyCol = newCol;
    }


    // Metoda do tasowania planszy
    public void shuffle(int numberOfMoves) {
        for (int i = 0; i < numberOfMoves; i++) {
            List<int[]> moves = getPossibleMoves();
            int[] move = moves.get(rand.nextInt(moves.size()));
            makeMove(move[0], move[1]);
        }
    }

    
    @Override
    public List<GraphState> generateChildren() {
        List<GraphState> children = new LinkedList<>();
        List<int[]> moves = getPossibleMoves();

        for (int[] move : moves) {
            PUZZLE child = new PUZZLE(this); // Utwórz kopię bieżącego stanu
            child.makeMove(move[0], move[1]); // Wykonaj ruch na kopii

            child.setMoveName("Move to (" + move[0] + ", " + move[1] + ")");
            children.add(child);
        }
        return children;
    }

	
	@Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                sb.append(board[i][j]).append(" ");
            }
            sb.append("\n");
        }
        return sb.toString();
    }
    
    // Metoda do ustawiania funkcji heurystycznej
    public void setHeuristic(StateFunction heuristicFunction) {
        this.setHFunction(heuristicFunction);
    }
	
    public static void main(String[] args) {
        int numProblems = 100; // Liczba losowych plansz
        int numShuffleMoves = 1000; // Liczba ruchów mieszających

        // Heurystyki do porównania
        StateFunction[] heuristics = {new MisplacedTile(), new Manhattan()};
        String[] heuristicNames = {"MisplacedTiles", "Manhattan"};

        // Średnie wyniki dla każdej heurystyki
        double[] totalVisitedStates = new double[heuristics.length];
        double[] totalWaitingStates = new double[heuristics.length];
        double[] totalTime = new double[heuristics.length];
        double[] totalPathLength = new double[heuristics.length];

        // Generowanie i rozwiązywanie 100 losowych plansz
        for (int problemIndex = 0; problemIndex < numProblems; problemIndex++) {
            PUZZLE puzzle = new PUZZLE(3);
            puzzle.shuffle(numShuffleMoves); // Mieszanie planszy 1000 razy

            System.out.println("Running problem " + (problemIndex + 1) + " out of " + numProblems);

            //System.out.println(puzzle);
            
            // Przeprowadzenie testu dla obu heurystyk
            for (int heuristicIndex = 0; heuristicIndex < heuristics.length; heuristicIndex++) {
                PUZZLE.setHFunction(heuristics[heuristicIndex]); // Ustawienie heurystyki

                GraphSearchAlgorithm algorithm = new AStar(puzzle);
                algorithm.execute();
                
                // Zbieranie wyników
                totalVisitedStates[heuristicIndex] += algorithm.getClosedStatesCount();
                totalWaitingStates[heuristicIndex] += algorithm.getOpenSet().size();
                totalTime[heuristicIndex] += algorithm.getDurationTime();
                
                System.out.println(puzzle);

                if (!algorithm.getSolutions().isEmpty()) {
                    GraphState solution = algorithm.getSolutions().get(0);
                    totalPathLength[heuristicIndex] += solution.getPath().size();
                   // System.out.println("SOLUTION:");
                   //System.out.println(solution);
                } else {
                    System.out.println("No solution found for problem " + (problemIndex + 1) + " with heuristic " + heuristicNames[heuristicIndex]);
                }
            }
        }

        // Wyświetlanie średnich wartości dla każdej heurystyki
        for (int i = 0; i < heuristics.length; i++) {
            System.out.printf("Heuristic %s:\n", heuristicNames[i]);
            System.out.printf("Średnia liczba stanów odwiedzonych: %.2f%n", totalVisitedStates[i] / numProblems);
            System.out.printf("Średnia liczba stanów oczekujących (w chwili stopu): %.2f%n", totalWaitingStates[i] / numProblems);
            System.out.printf("Średnia długość ścieżki: %.2f%n", totalPathLength[i] / numProblems);
            System.out.printf("Średni czas wykonania: %.2f ms%n", totalTime[i] / numProblems);
            System.out.println();
        }
    }


    
    
    @Override
	public boolean isSolution() {
	    int count = 0;
	    for (int i = 0; i < n; i++) {
	        for (int j = 0; j < n; j++) {
	            if (board[i][j] != count) {
	                return false;
	            }
	            count++;
	        }
	    }
	    return true;
	}

	@Override
	public boolean equals(Object obj) {
	    if (this == obj) return true;
	    if (obj == null || getClass() != obj.getClass()) return false;
	    PUZZLE puzzle = (PUZZLE) obj;
	    for (int i = 0; i < n; i++) {
	        for (int j = 0; j < n; j++) {
	            if (this.board[i][j] != puzzle.board[i][j]) {
	                return false;
	            }
	        }
	    }
	    return true;
	}

	@Override
	public int hashCode() {
	    return toString().hashCode();
	}


}



