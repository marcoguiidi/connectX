/*
 *  in connectx directory
 *  command line complile:
 * 
 */ //  javac -cp ".." *.java */*.java
 /*  
 *   command line execution:
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
    private boolean playerA;

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
        playerA = first;
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

        GTBoard T = new GTBoard(B);
		Integer[] L = B.getAvailableColumns();
        int save = L[rand.nextInt(L.length)]; // Save a random column 
        try {
			return getBestMove(T);
		} catch (TimeoutException e) {
			System.err.println("Timeout!!! Random column selected");
			return save;
		}
    }

    public int iterativeDeepening(GTBoard T, int depth) throws TimeoutException{
        T.alpha = -1;
        T.beta = 1;
        T.eval = 0;
        for(int d = 0; d <= depth; d++){
            if ((System.currentTimeMillis() - START) / 1000.0 >= TIMEOUT * (96.0 / 100.0)){
                break;
            }
            T.eval = alphaBeta(T, -1, +1, playerA, depth);
        }
        return T.eval;  
    }

    public int getBestMove(GTBoard T) throws TimeoutException{
        Integer moves[] = T.board.getAvailableColumns();
        int bestMove = moves[rand.nextInt(moves.length)];
        int bestEval = (!playerA ? -1 : +1);
        
        for(int move : moves){
            GTBoard c = new GTBoard(T.board.copy());
            c.board.markColumn(move);
            if ((System.currentTimeMillis() - START) / 1000.0 <= TIMEOUT * (96.0 / 100.0)){
                int value = iterativeDeepening(c, 50);
            
            //T.board.unmarkColumn();
                if (!playerA){
                    if(value >= bestEval && value != 1){
                        if (value == -1){
                            return move;
                        }   
                        bestEval = value;
                        bestMove = move;
                    }
                }   
                else{
                    if (value <= bestEval && value != -1){
                     if (value == 1){
                         return move;
                     }
                        bestEval = value;
                        bestMove = move;
                    }
                }
            }
        }

        /*for (int move : moves) {
            checktime();
            T.board.markColumn(move);
            int value = iterativeDeepening(T, 20);
            T.board.unmarkColumn();
            if (myWin == CXGameState.WINP1 && value >= T.alpha) {
                T.alpha = value;
                bestMove = move;
            }
            else if (myWin == CXGameState.WINP2 && value <= T.beta){
                T.beta = value;
                bestMove = move;
            }

            if (value == (myWin == CXGameState.WINP1 ? 1 : -1))
                break;   

            if ((System.currentTimeMillis() - START) / 1000.0 >= TIMEOUT * (95.0 / 100.0)){
                break;
            }
        }*/
        
        return bestMove;
    }

    private int alphaBeta(GTBoard T, int alpha, int beta, boolean maximizingPlayer, int depth) throws TimeoutException{
        checktime();
        
        if (depth == 0 || T.board.gameState() != CXGameState.OPEN) {
            T.eval = evaluate(T.board);
        }
        
        else if (maximizingPlayer) {
            T.eval = -1;
            for (int move : T.board.getAvailableColumns()) {
                GTBoard c = new GTBoard(T.board.copy());
                c.board.markColumn(move);
                
                if ((System.currentTimeMillis() - START) / 1000.0 >= TIMEOUT * (98.0 / 100.0)){
                    T.eval = evaluate(T.board);
                    break;
                }
                
                T.eval = Math.max (T.eval, alphaBeta(c, alpha, beta, false, depth - 1));
                //T.board.unmarkColumn();
                T.alpha = Math.max(T.alpha, T.eval);
                
                if (T.beta <= T.alpha) {
                    break;
                }
            }
        }
        else {
            T.eval = +1;
            for (int move : T.board.getAvailableColumns()) {
                GTBoard c = new GTBoard(T.board.copy());
                c.board.markColumn(move);
                if ((System.currentTimeMillis() - START) / 1000.0 >= TIMEOUT * (98.0 / 100.0)){
                    T.eval = evaluate(T.board);
                    break;
                }
                T.eval = Math.min(T.eval, alphaBeta(c, alpha, beta, true, depth - 1));
                //T.board.unmarkColumn();
                T.beta = Math.min(T.beta, T.eval);
                
                if (T.beta <= T.alpha) {
                    break;
                }
            }
        }
        return T.eval;
    }
   

    private int evaluate(CXBoard B){

        if (B.gameState() == CXGameState.WINP1){
            return 1;
        }
        else if (B.gameState() == CXGameState.WINP2) {
            return -1;
        }
        else 
            return 0;
    }

    public String playerName(){
        return "crace";
    }

}




