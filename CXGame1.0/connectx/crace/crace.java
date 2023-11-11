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
import connectx.CXCell;
import connectx.CXCellState;
import connectx.CXGameState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Collections;
import java.util.Random;
import java.util.concurrent.TimeoutException;




public class crace implements CXPlayer {

    private Map<tabDepth, Integer> hashTable;  // vengono salvate le tabelle associate alla profondità
    private Random rand;
    private CXGameState myWin;      
	private CXGameState yourWin;
	private int  TIMEOUT;
	private long START;
    private boolean playerA;
    private CXCellState myplayer;
    private boolean evaluated;
    

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
        myplayer = first ? CXCellState.P1 : CXCellState.P2;
		TIMEOUT = timeout_in_secs;
        playerA = first;
        evaluated = false;
        hashTable = new HashMap<>();
	}
   
    /*
     * iterative deepening algorithm
     */
    private int iterativeDeepening(GTBoard T, int depth, boolean maximizingPlayer){
        Map<Integer, valDepth> save = new HashMap<>();
        Integer[] a = T.board.getAvailableColumns();
        int retValue = a[rand.nextInt(a.length)];
        

        int d;

        System.out.print("\n\n running ...\n");
        for( d = 1; d <= depth; d++){
            
            if ((System.currentTimeMillis() - START) / 1000.0 >= TIMEOUT * (95.0 / 100.0)){
                break;
            }
            long beg = System.currentTimeMillis();

            Integer[] moves = T.board.getAvailableColumns();
        
            for(int move : moves){

                boolean closed = false;
                if (d > 1 && (save.get(move).val == Integer.MIN_VALUE || save.get(move).val == Integer.MAX_VALUE)) {
                    closed = true;              
                }
                
                if ((System.currentTimeMillis() - START) / 1000.0 >= TIMEOUT * (95.0 / 100.0))
                    break;

                if (!closed){

                    CXBoard cpy = T.board.copy();
                    GTBoard c = new GTBoard(cpy, playerA);
                    cpy.markColumn(move);

                    tabDepth x = new tabDepth(c, d);
                    //if (hashTable.get(x) == null) { // evita di rivalutare tabelle già valutate
                        int val = alphaBeta(c, Integer.MIN_VALUE, Integer.MAX_VALUE, !maximizingPlayer, d);

                        save.put(move, new valDepth(val, d));

                        hashTable.put(x, val);  
                        //System.out.print(save.get(move));
                        //System.out.print(" ");
                
                        if (save.get(move).val == Integer.MAX_VALUE) {
                            if (maximizingPlayer) {
                                System.out.print("\nwinning in ");
                                System.out.print(d/2);
                                System.out.print(" moves\n");
                                return move;
                            }
                        }

                        else if (save.get(move).val == Integer.MIN_VALUE) {
                            if (!maximizingPlayer) {
                                System.out.print("\nwinning in ");
                                System.out.print(d/2);
                                System.out.print(" moves\n");
                                return move;
                            }
                        }

                        else{
                            int pre =  preScan(T.board, move);
                            if (maximizingPlayer) {
                                save.put(move, new valDepth((save.get(move).val + pre), d));
                            }
                            else save.put(move, new valDepth((save.get(move).val - pre), d));
                        }
                    //}
                }
            }
            System.out.print("\ndepth: ");
            System.out.print(d);
            System.out.print(", time: ");
            System.out.print(System.currentTimeMillis() - beg);
            System.out.print("ms");
            //System.out.print(save);
        }
        
        System.out.print("\ndone!");

        List<Map.Entry<Integer, valDepth>> lista = new ArrayList<>(save.entrySet());

        if(maximizingPlayer) Collections.sort(lista, (entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()));

        else Collections.sort(lista, (entry1, entry2) -> entry1.getValue().compareTo(entry2.getValue()));

        int moves = lista.get(0).getValue().depth;
        retValue = lista.get(0).getKey();
        int i = 0;

        /*
         * cicli while che perdono in più mosse possibili, quando ogni mossa porta alla sconfitta
         */
        if (retValue == Integer.MAX_VALUE && !maximizingPlayer) {
            
            while (lista.get(i).getValue().val == Integer.MAX_VALUE && i < lista.size()){ 
                
                if (lista.get(i).getValue().depth > moves){
                    moves = lista.get(i).getValue().depth;
                    retValue = lista.get(i).getKey();
                }
                i++;
            }  
        } 

        else if (retValue == Integer.MIN_VALUE && maximizingPlayer) {
            
            while (lista.get(i).getValue().val == Integer.MIN_VALUE && i < lista.size()){
                
                if (lista.get(i).getValue().depth > moves){
                    moves = lista.get(i).getValue().depth;
                    retValue = lista.get(i).getKey();
                    }
                
                i++;
            }
        }
        //System.out.println(lista.get(0).getValue().depth);
        //System.out.println(lista.get(1).getValue().depth);

        return retValue;
    }

    /*
     * evaluation of the move before played
     */
    private int preScan(CXBoard B, int move){
        int val = 0;

        if (move == (B.N)/2) {
            val += 10;
        }
        else if ((move <= (B.N)/2 + 1) && (move >= (B.N)/2 - 1)) {
            val += 5;
        }

        int around = countAround(B, move);
        int inLine = countInLine(B, move);
        val += around;
        val += inLine;
        return val;
    }  

    /*
     * count how many cells are mine in range 1
     */
    private int countAround(CXBoard B, int move){
        int count = 0;
        CXBoard cpy = B.copy();
        cpy.markColumn(move);

        CXCell cell = cpy.getLastMove();

        /*
        System.out.print("\nrow: ");
        System.out.print(cell.i);
        System.out.print(", col: ");
        System.out.print(cell.j);
        */

        if (cell.i > 0 && cell.i < B.M - 1){
            for (int i = cell.i - 1; i <= cell.i + 1; i++){
                if (cell.j > 0 && cell.j < B.N - 1){
                    for (int j = cell.j - 1; j <= cell.j + 1; j++){
                        if (B.cellState(i, j) == myplayer) {
                            count++;
                        }
                    }
                }
                else if (cell.j > 0){
                    for (int j = cell.j - 1; j <= cell.j; j++){
                        if (B.cellState(i, j) == myplayer) {
                            count++;
                        }
                    }
                }
                else if (cell.j < B.N - 1) {
                    for (int j = cell.j; j <= cell.j + 1; j++){
                        if (B.cellState(i, j) == myplayer) {
                            count++;
                        }
                    }
                }
            }
        }
        else if (cell.i > 0){
            for (int i = cell.i - 1; i <= cell.i; i++){
                if (cell.j > 0 && cell.j < B.N - 1){
                    for (int j = cell.j - 1; j <= cell.j + 1; j++){
                        if (B.cellState(i, j) == myplayer) {
                            count++;
                        }
                    }
                }
                else if (cell.j > 0){
                    for (int j = cell.j - 1; j <= cell.j; j++){
                        if (B.cellState(i, j) == myplayer) {
                            count++;
                        }
                    }
                }
                else if (cell.j < B.N - 1) {
                    for (int j = cell.j; j <= cell.j + 1; j++){
                        if (B.cellState(i, j) == myplayer) {
                            count++;
                        }
                    }
                }
            }
        }
        else if (cell.i < B.M - 1){
            for (int i = cell.i; i <= cell.i + 1; i++){
                if (cell.j > 0 && cell.j < B.N - 1){
                    for (int j = cell.j - 1; j <= cell.j + 1; j++){
                        if (B.cellState(i, j) == myplayer) {
                            count++;
                        }
                    }
                }
                else if (cell.j > 0){
                    for (int j = cell.j - 1; j <= cell.j; j++){
                        if (B.cellState(i, j) == myplayer) {
                            count++;
                        }
                    }
                }
                else if (cell.j < B.N - 1) {
                    for (int j = cell.j; j <= cell.j + 1; j++){
                        if (B.cellState(i, j) == myplayer) {
                            count++;
                        }
                    }
                }
            }
        }
        return count;
    }

    /*
     * evaluate the current board
     */
    private int evaluate(GTBoard B){
        int eval = 0;
        if (B.board.gameState() == CXGameState.WINP1){
            return Integer.MAX_VALUE;
        }
        else if (B.board.gameState() == CXGameState.WINP2) {
            return Integer.MIN_VALUE;
        }
        else{
            if (playerA) {
                eval += countCentral(B.board);
            }
            else eval -= countCentral(B.board);
            
        }
        evaluated = true;
        return eval;   
    }

    /*
     * count how many central cells are mine
     */
    private int countCentral(CXBoard B){
        int count = 0;
        for (int j = (B.N)/2 - 1; j < (B.N)/2 + 1; j++){
            if (B.cellState(0, j) == CXCellState.FREE) {
                break;
            }
            for (int i = 0; i < B.M - 1; i++){
                  if (B.cellState(i, j) == myplayer) {
                     count++;
                  } 
                  /*
                  else if (B.cellState(i, j) != CXCellState.FREE) {
                     count--;   
                  }
                  */
            }
        }
        return count;
    }

    /*
     * count how many cells in line I have before the move
     */
    private int countInLine(CXBoard B, int move){
        int count = 0;

        //count += countInRow(B, move);
        count += countInCol(B, move);
        count += countInPdiag(B, move);
        count += countInSDiag(B, move);

        return count;
    }

    /*
     * count inLine ausiliar functions
     */
    private int countInSDiag(CXBoard b, int move) {
        return 0;
    }

    private int countInPdiag(CXBoard b, int move) {
        return 0;
    }

    private int countInCol(CXBoard b, int move) {
        int count = 0;

        int i = b.M - 1;
        while (b.cellState(i, move) != CXCellState.FREE && i >= 0) {
            i--;
        }
        if (i == 0) {
            return 0;
        }
        else{
            i++;
            boolean cont = true;
            while (cont && i < b.M) {
                if (b.cellState(i, move) != myplayer) {
                    cont = false;
                }
                else count++;
                i++;
            }
        }
        return count;
    }

    /*private int countInRow(CXBoard b, int move) {
        int count = 0;

        while (b.cellState(i, move) != CXCellState.FREE && i >= 0) {
            i--;
        }
        if (i == 0) {
            return 0;
        }
        else{
            boolean cont = true;
            while (cont && i < b.M) {
                if (b.cellState(i + 1, move) != myplayer) {
                    cont = false;
                }
                else count++;
            }
        }
        return count;
    }*/

    /*
     * return the selected column
     */
    public int selectColumn(CXBoard B){
        
        START = System.currentTimeMillis(); // Save starting time
        
		return GetBestMove(B, playerA);
    }

    /*
     * return the best move calculated
     */
    private int GetBestMove(CXBoard B, boolean maximizingPlayer) {

        GTBoard c = new GTBoard(B, maximizingPlayer);
        return iterativeDeepening(c, B.numOfFreeCells(), maximizingPlayer);
    }
     
    /*
     * alphaBeta pruning algorithm
     */
    private Integer alphaBeta(GTBoard T, int alpha, int beta, boolean maximizingPlayer, int depth) {
        if (T.board.gameState() != CXGameState.OPEN || T.board.getAvailableColumns().length == 0 || depth == 0) {
            return evaluate(T);
        }
        
        else if (maximizingPlayer) {
            T.eval = Integer.MIN_VALUE;
            for (int move : T.board.getAvailableColumns()) {

                CXBoard cpy = T.board.copy();
                GTBoard c = new GTBoard(cpy, maximizingPlayer);
                cpy.markColumn(move);
                
                if ((System.currentTimeMillis() - START) / 1000.0 >= TIMEOUT * (96.0 / 100.0)){
                    T.eval = evaluate(T);
                    break;
                }
                
                T.eval = Math.max (T.eval, alphaBeta(c, alpha, beta, false, depth - 1));
                alpha = Math.max(T.eval, alpha);

                if ((System.currentTimeMillis() - START) / 1000.0 >= TIMEOUT * (96.0 / 100.0)){
                    break;
                }

                cpy.unmarkColumn();
                            
                if (beta <= alpha) {
                    break;
                }
            }
            return T.eval;
        }
        else{
            T.eval = Integer.MAX_VALUE;
            for (int move : T.board.getAvailableColumns()) {

                CXBoard cpy = T.board.copy();
                GTBoard c = new GTBoard(cpy, maximizingPlayer);
                cpy.markColumn(move);

                if ((System.currentTimeMillis() - START) / 1000.0 >= TIMEOUT * (96.0 / 100.0)){
                    T.eval = evaluate(T);
                    break;
                }
                T.eval = Math.min(T.eval, alphaBeta(c, alpha, beta, true, depth - 1));
                beta = Math.min(T.eval, beta);

                if ((System.currentTimeMillis() - START) / 1000.0 >= TIMEOUT * (96.0 / 100.0)){
                    break;
                }

                cpy.unmarkColumn();;

                if (beta <= alpha) {
                    break;
                }
            }
            return T.eval;
        }
    }

    /*
     * return player name
     */
    public String playerName(){
        return "crace";
    }
}