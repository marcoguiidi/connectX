

package connectx.crace;

import connectx.CXBoard;

/*
 * classe di dato che associa ad ogni Board una valutazione intera
 */
public class GTBoard{
    public int eval;
    public CXBoard board;

    public GTBoard(CXBoard b, boolean maximizingPlayer){
        this.board = b;
        eval = (maximizingPlayer ? Integer.MIN_VALUE : Integer.MAX_VALUE);
    }
}
