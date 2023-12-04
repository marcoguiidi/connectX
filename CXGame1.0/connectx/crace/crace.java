/*
 *  in connectx directory
 *  command line complile:
 *  
 * 
 */ //  javac -cp ".." *.java */*.java && java -cp ".." connectx.CXGame 6 7 4 connectx.crace.crace && rm *.class */*.class 
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
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Collections;
import java.util.Random;




public class crace implements CXPlayer {

    private Map<Integer, Integer> mapTable;  // salva le tabelle che valuta !! PROBLEMA NON LA SALVA !!
    private Random rand;
	private int  TIMEOUT;
	private long START;
    private boolean playerA;
    private CXCellState myplayer;

    /*
     * default empty constructor
     */
    public crace (){
    }

    public void initPlayer(int M, int N, int K, boolean first, int timeout_in_secs) {
		// New random seed for each game
		rand    = new Random(System.currentTimeMillis());
        myplayer = first ? CXCellState.P1 : CXCellState.P2;
		TIMEOUT = timeout_in_secs;
        playerA = first;
        mapTable = new Hashtable<>(1000000);
	}
   
    /*
     * iterative deepening algorithm
     * 
     *  @return: best evaluated move 
     */
    private int iterativeDeepening(GTBoard T, int depth, boolean maximizingPlayer){
        Map<Integer, valDepth> save = new HashMap<>();  // hashMap in cui salvare i valori delle mosse
        Integer[] a = T.board.getAvailableColumns();
        int retValue = a[rand.nextInt(a.length)];  // colonna casuale nelle mosse disponibili

        if (a.length == 1) {   // se c'è solo una mossa, allora gioco quella
            return a[0];
        }
        int d;

        System.out.print("\n\n running ...\n");
        for( d = 1; d <= depth; d++){
            
            if ((System.currentTimeMillis() - START) / 1000.0 >= TIMEOUT * (95.0 / 100.0)){
                break;
            }

            long beg = System.currentTimeMillis();  // funzione per calcolare il tempo di ogni profondità

            Integer[] moves = T.board.getAvailableColumns();

            for(int move : moves){

                boolean closed = false;
                if (d > 1 && (save.get(move).val == Integer.MIN_VALUE || save.get(move).val == Integer.MAX_VALUE)) {
                    closed = true;              // evito di rivalutare una mossa che so già portare a vittoria/sconfitta
                }
                
                if ((System.currentTimeMillis() - START) / 1000.0 >= TIMEOUT * (95.0 / 100.0))
                    break;

                if (!closed){ // booleano che controlla se la mossa è ancora aperta

                    CXBoard cpy = T.board.copy();
                    GTBoard c = new GTBoard(cpy, playerA);
                    cpy.markColumn(move);

                    if (mapTable.containsKey(board2nr(cpy)) && d > 2) { // non entra mai nel ciclo
                         System.out.println("già valutata");
                     }
                    else { // se la tabella non è presente nella lista di quelle già visitate allora la visito
                        CXBoard cc = cpy.copy(); // salvo la tabella iniziale per aggiungerla alla hashMap di quelle già valutate
                        
                        int val = alphaBeta(c, Integer.MIN_VALUE, Integer.MAX_VALUE, !maximizingPlayer, d);

                        valDepth ins = new valDepth(val, d);
                        ins.setBool(maximizingPlayer);   // funzione utile all'ordinamento delle mosse per la scelta della mossa migliore

                        save.put(move, ins);

                        //mapTable.put(board2nr(cc), 1);  // inserisce la tabella con la mossa iniziale giocata

                        /*
                         * controlli per restituire la mossa vincente
                         */
                        if (save.get(move).val == Integer.MAX_VALUE) {
                            if (maximizingPlayer) {
                                System.out.print("\nwinning in ");
                                System.out.print(d);
                                System.out.print(" moves\n");
                                return move;
                            }
                        }

                        else if (save.get(move).val == Integer.MIN_VALUE) {
                            if (!maximizingPlayer) {
                                System.out.print("\nwinning in ");
                                System.out.print(d);
                                System.out.print(" moves\n");
                                return move;
                            }
                        }

                        /*
                         * altrimenti modifichiamo il valore con le euristiche
                         */
                        else{
                            int pre =  preScan(T.board, move);
                            if (maximizingPlayer) {
                                ins.val += pre;
                                save.put(move, ins);
                            }
                            else {
                                ins.val -= pre;
                                save.put(move, ins);
                            }
                        }
                    }
                }
            }
            /*
             * stampa delle informazioni sulla profondità
             */
            System.out.print("depth: ");
            System.out.print(d);
            System.out.print(", time: ");
            System.out.print(System.currentTimeMillis() - beg);
            System.out.print("ms\n");
        }
        
        System.out.print("\ndone!");

        /*
         * ordinamento delle mosse e scelta mossa migliore
         */
        List<Map.Entry<Integer, valDepth>> lista = new ArrayList<>(save.entrySet());
        Collections.sort(lista, (entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()));
        retValue = lista.get(0).getKey();

        return retValue;
    }

    /*
     * evaluation of the move before played
     * 
     * @return: euristic evaluation
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
     * 
     * @return: eval of the board
     */
    private int evaluate(GTBoard B){
        int eval = 0;
        if (B.board.gameState() == CXGameState.WINP1){
            return Integer.MAX_VALUE;
        }
        else if (B.board.gameState() == CXGameState.WINP2) {
            return Integer.MIN_VALUE;
        }
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
            }
        }
        return count;
    }

    /*
     * count how many cells in line i have before the move
     */
    private int countInLine(CXBoard B, int move){
        int count = 0;

        count += countInRow(B, move);
        count += countInCol(B, move);
        count += countInPdiag(B, move);
        count += countInSDiag(B, move);

        return count;
    }

    /*
     * countInLine ausiliar functions
     */
    private int countInSDiag(CXBoard b, int move) {  // conta quanti gettoni ottengo in diagonale Right Bottom
        return 0;
    }

    private int countInPdiag(CXBoard b, int move) {  // conta quanti gettoni ottengo in diagonale Left Bottom
        return 0;
    }

    private int countInCol(CXBoard b, int move) { // conta quanti gettoni otterrei in colonna se gioco una mossa
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

    private int countInRow(CXBoard b, int move) {  // conta quanti gettoni in riga otterrei se gioco una mossa
        int count = 0;

        int i = b.M - 1;
        while (b.cellState(i, move) != CXCellState.FREE && i >= 0) {
            i--;
        }
        if (i == 0) { // controllo inutile perchè la mossa è lecita
            return 0;
        }
        else{
            boolean cont = true;
            int j = move;
            while (j <= b.N - 1 && cont) {  // conta a destra della mossa
                if (b.cellState(i, j) != myplayer) {
                    cont = false;
                }
                else count++;
                j++;
            }
            cont = true;
            j = move;
            while (j >= 0 && cont) {  // conta a sinistra della mossa
                if (b.cellState(i, j) != myplayer) {
                    cont = false;
                }
                else count++;
                j--;
            }

        }
        return count;
    }

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
     * 
     * @return: eval of the board
     */
    private Integer alphaBeta(GTBoard T, int alpha, int beta, boolean maximizingPlayer, int depth) {
        if (T.board.gameState() != CXGameState.OPEN || T.board.getAvailableColumns().length == 0 || depth == 0) {
            mapTable.put(board2nr(T.board), 1);
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

                // if (!mapTable.containsKey(c.board)) {
                //     mapTable.put(c.board, 1);
                // }
                
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

                // if (!mapTable.containsKey(c.board)) {
                //     mapTable.put(c.board, 1);
                // }
            
                T.eval = Math.min(T.eval, alphaBeta(c, alpha, beta, true, depth - 1));
                beta = Math.min(T.eval, beta);

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
    }

    /*
     * funzione che associa ad ogni board un numero
     */
    private int board2nr(CXBoard B){
        int eval = 0;
        for(int i = 0; i < B.N; i++){
            eval += Math.pow(colCount(B, i), i);
        }
        return eval;
    }

    /*
     * funzione che conta quante celle sono mie nella colonna col
     */
    private int colCount(CXBoard B, int col){
        int eval = 0;
        int i = B.M - 1;
        CXCellState state;
        while (i >= 0 && ((state = B.cellState(i, col)) != CXCellState.FREE)){
            if (state == myplayer) {
                eval ++;
            }
            i--;
        }
        return eval;
    }

    /*
     * return player name
     */
    public String playerName(){
        return "crace";
    }
}