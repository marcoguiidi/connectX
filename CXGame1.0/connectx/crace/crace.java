/*
 *  in connectx directory
 *  command line complile:
 * 
 */ //  javac -cp ".." *.java */*.java
 /*  
 *   command line execution:
 *      java -cp ".." connectx.CXGame 6 7 4 connectx.crace.crace
 *      
 *      empty package is human play
 */


 package connectx.crace;

import connectx.CXPlayer;
import connectx.CXBoard;
import connectx.CXGameState;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Collections;
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
		if ((System.currentTimeMillis() - START) / 1000.0 >= TIMEOUT * (99.5 / 100.0))
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

    /* 
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

    private int getBestMove(GTBoard T) throws TimeoutException{
        Integer moves[] = T.board.getAvailableColumns();
        int bestMove = moves[rand.nextInt(moves.length)];
        int ZMoves[] = new int[moves.length];
        int Zindex = 0;
        int bestEval = (playerA ? 1 : -1);
        
        for(int move : moves){
            GTBoard c = new GTBoard(T.board.copy());
            c.board.markColumn(move);
            if ((System.currentTimeMillis() - START) / 1000.0 <= TIMEOUT * (96.0 / 100.0)){
                int value = iterativeDeepening(c, 20);
            
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
                 if (value == 0){
                    ZMoves[Zindex] = move;
                    Zindex++;
                }
            }
        }
        if (bestEval == 0){
            return ZMoves[Zindex-1];
        }

        
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
    */
   

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

    public int selectColumn(CXBoard B){
        START = System.currentTimeMillis(); // Save starting time
        
		return GetBestMove(B, playerA);
    }

    private int GetBestMove(CXBoard B, boolean maximizingPlayer) {
        Integer[] moves = B.getAvailableColumns();

        couple[] save = new couple[moves.length];
        int sindex= 0;
        for(int i : moves){

            if ((System.currentTimeMillis() - START) / 1000.0 >= TIMEOUT * (95.0 / 100.0))
                break;

            save[sindex].setColumn(i);
            
            GTBoard c = new GTBoard(B.copy(), maximizingPlayer);
            c.board.markColumn(save[sindex].getColumn());
            save[sindex].setValue(alphaBeta(c, -1, +1, !maximizingPlayer));
            if (save[sindex].getValue() == 1 && maximizingPlayer) {
                return save[sindex].getColumn();
            }

            else if (save[sindex].getValue() == -1 && !maximizingPlayer) {
                return save[sindex].getColumn();
            }
            sindex++;
        }
        Arrays.sort(save, Collections.reverseOrder());
        return save[0].getColumn();
    }

    /*private int retMaxIndex(couple[] arr, boolean A){

        if (A){
            int best = -1;
            int index = 0;
            for (int i = 0; i < arr.length; i++){
                if (arr[i].value > best) {
                    best = arr[i].value;
                    index = i;
                }
            }
            return index;
        }

        else{
            int best = 1;
            int index = 0;
            for (int i = 0; i < arr.length; i++){
                if (arr[i] < best) {
                    best = arr[i];
                    index = i;
                }
            }
            return index;
        }
    }
*/
   

    private int miniMax(GTBoard T, boolean playerA) {

        if (T.board.getAvailableColumns().length == 0 || T.board.gameState() != CXGameState.OPEN) {
            T.eval = evaluate(T.board);
        }
        else if (playerA) {
            T.eval = Integer.MIN_VALUE;
            for (int move : T.board.getAvailableColumns()) {

                if ((System.currentTimeMillis() - START) / 1000.0 >= TIMEOUT * (98.0 / 100.0)){
                    T.eval = evaluate(T.board);
                    break;
                }

                GTBoard c = new GTBoard(T.board.copy(), playerA);
                c.board.markColumn(move);
                T.eval = Math.max(T.eval, miniMax(c, false));
            }
        }
        else{
            T.eval = Integer.MAX_VALUE;
            for (int move : T.board.getAvailableColumns()) {

                if ((System.currentTimeMillis() - START) / 1000.0 >= TIMEOUT * (98.0 / 100.0)){
                    T.eval = evaluate(T.board);
                    break;
                }

                GTBoard c = new GTBoard(T.board.copy(), playerA);
                c.board.markColumn(move);
                T.eval = Math.min(T.eval, miniMax(c, false));
            }
        }
        return T.eval;
    }

    private int alphaBeta(GTBoard T, int alpha, int beta, boolean maximizingPlayer) {

        if (T.board.gameState() != CXGameState.OPEN || T.board.getAvailableColumns().length == 0) {
            T.eval = evaluate(T.board);
        }
        
        else if (maximizingPlayer) {
            T.eval = -1;
            for (int move : T.board.getAvailableColumns()) {

                if ((System.currentTimeMillis() - START) / 1000.0 >= TIMEOUT * (98.0 / 100.0)){
                    T.eval = evaluate(T.board);
                    break;
                }

                GTBoard c = new GTBoard(T.board.copy(), maximizingPlayer);
                c.board.markColumn(move);
                
                if ((System.currentTimeMillis() - START) / 1000.0 >= TIMEOUT * (98.0 / 100.0)){
                    T.eval = evaluate(T.board);
                    break;
                }
                
                T.eval = Math.max (T.eval, alphaBeta(c, alpha, beta, false));
                alpha = Math.max(alpha, T.eval);
                
                if (beta <= alpha) {
                    break;
                }
            }
        }
        else {
            T.eval = +1;
            for (int move : T.board.getAvailableColumns()) {

                if ((System.currentTimeMillis() - START) / 1000.0 >= TIMEOUT * (98.0 / 100.0)){
                    T.eval = evaluate(T.board);
                    break;
                }

                GTBoard c = new GTBoard(T.board.copy(), maximizingPlayer);
                c.board.markColumn(move);

                if ((System.currentTimeMillis() - START) / 1000.0 >= TIMEOUT * (98.0 / 100.0)){
                    T.eval = evaluate(T.board);
                    break;
                }
                T.eval = Math.min(T.eval, alphaBeta(c, alpha, beta, true));
                beta = Math.min(beta, T.eval);
                
                if (beta <= alpha) {
                    break;
                }
            }
        }
        return T.eval;
    }

    public String playerName(){
        return "crace";
    }
}