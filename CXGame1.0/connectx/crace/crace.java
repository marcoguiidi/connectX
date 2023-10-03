/*
 *  in connectx directory
 *  command line complile:
 */  //  javac -cp ".." *.java */*.java
 /* 
 *  command line execution:
 *      java -cp ".." connectx.CXPlayerTester 6 7 4 connectx.L1.L1 connectx.crace.crace -v
 *      
 *      empty package is human play
 */


 package connectx.crace;

import connectx.CXPlayer;
import connectx.CXBoard;
import connectx.CXGameState;
import java.util.Random;
import java.util.concurrent.TimeoutException;


public class crace implements CXPlayer {

    private Random rand;
    private CXGameState myWin;      
	private CXGameState yourWin;
	private int  TIMEOUT;
	private long START;

    /*
     * default empty constructor
     */
    public crace (){
    }

    public void initPlayer(int M, int N, int K, boolean first, int timeout_in_secs) {
		// New random seed for each game
		rand    = new Random(System.currentTimeMillis());
		myWin   = first ? CXGameState.WINP1 : CXGameState.WINP2;
		yourWin = first ? CXGameState.WINP2 : CXGameState.WINP1;
		TIMEOUT = timeout_in_secs;
	}

    /*
     * check the current time
     */

    private void checktime() throws TimeoutException {
		if ((System.currentTimeMillis() - START) / 1000.0 >= TIMEOUT * (99.0 / 100.0))
			throw new TimeoutException();
    }   

    /*
	 * Check if we can win in a single move
	 *
	 * Returns the winning column if there is one, otherwise -1
	 */	
    /* 
	private int singleMoveWin(CXBoard B, Integer[] L) throws TimeoutException {
        for(int i : L) {
          checktime(); // Check timeout at every iteration
          CXGameState state = B.markColumn(i);
          if (state == myWin)
            return i; // Winning column found: return immediately
          B.unmarkColumn();
        }
            return -1;
    
    }
    */

    public int selectColumn(CXBoard B) {
		START = System.currentTimeMillis(); // Save starting time

		Integer[] L = B.getAvailableColumns();
        int save = L[rand.nextInt(L.length)]; // Save a random column 
        try {
			return getBestMove(B);
		} catch (TimeoutException e) {
			System.err.println("Timeout!!! Random column selected");
			return save;
		}
    }

    public int iterativeDeepening(CXBoard board, int depth) throws TimeoutException{
        int alpha = -1;
        int beta = 1;
        int eval = 0;
        for(int d = 0; d <= depth; d++){
            if ((System.currentTimeMillis() - START) / 1000.0 >= TIMEOUT * (96.0 / 100.0)){
                break;
            }
            boolean playerA = myWin == CXGameState.WINP1;
            eval = alphaBeta(board, alpha, beta, playerA, depth);
        }
        return eval;
    }

    public int getBestMove(CXBoard board) throws TimeoutException{
        int alpha = -1;
        int beta = 1;
        Integer moves[] = board.getAvailableColumns();
        int bestMove = moves[rand.nextInt(moves.length)];
        
        for (int move : moves) {
            checktime();
            CXBoard newBoard = board.copy();
            newBoard.markColumn(move);
            int value = iterativeDeepening(newBoard, 20);
            newBoard.unmarkColumn();
            if (myWin == CXGameState.WINP1 && value >= alpha) {
                alpha = value;
                bestMove = move;
            }
            else if (myWin == CXGameState.WINP2 && value <= beta){
                beta = value;
                bestMove = move;
            }

            if (value == (myWin == CXGameState.WINP1 ? 1 : -1))
                break;   

            if ((System.currentTimeMillis() - START) / 1000.0 >= TIMEOUT * (95.0 / 100.0)){
                break;
            }
        }
        
        return bestMove;
    }

    private int alphaBeta(CXBoard board, int alpha, int beta, boolean maximizingPlayer, int depth) throws TimeoutException{
        //checktime();
            
        
        if (depth == 0 || board.gameState() != CXGameState.OPEN) {
            return evaluate(board);
        }
        
        else if (maximizingPlayer) {
            int eval = -1;
            for (int move : board.getAvailableColumns()) {
                CXBoard newBoard = board.copy();
                newBoard.markColumn(move);
                if ((System.currentTimeMillis() - START) / 1000.0 >= TIMEOUT * (95.0 / 100.0)){
                    return evaluate(newBoard);
                }
                eval = Math.max (eval, alphaBeta(newBoard, alpha, beta, !maximizingPlayer, depth - 1));
                newBoard.unmarkColumn();
                alpha = Math.max(alpha, eval);
                
                if (beta <= alpha) {
                    break;
                }
            }
            return eval;
        }
        else {
            int eval = +1;
            for (int move : board.getAvailableColumns()) {
                CXBoard newBoard = board.copy();
                newBoard.markColumn(move);
                if ((System.currentTimeMillis() - START) / 1000.0 >= TIMEOUT * (95.0 / 100.0)){
                    return evaluate(newBoard);
                }
                eval = Math.min(eval, alphaBeta(newBoard, alpha, beta, !maximizingPlayer, depth - 1));
                newBoard.unmarkColumn();
                beta = Math.min(beta, eval);
                
                if (beta <= alpha) {
                    break;
                }
            }
            return eval;
        }
        
    }
   

    private int evaluate(CXBoard B){

        if (B.gameState() == myWin){
            return 1;
        }
        else if (B.gameState() == yourWin) {
            return -1;
        }
        else 
            return 0;
    }

    public String playerName(){
        return "crace";
    }

}




