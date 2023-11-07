

package connectx.crace;

import connectx.CXPlayer;
import connectx.CXBoard;
import connectx.CXGameState;


public class GTBoard{
    public int eval;
    public CXBoard board;

    public GTBoard(CXBoard b, boolean maximizingPlayer){
        this.board = b;
        eval = (maximizingPlayer ? Integer.MIN_VALUE : Integer.MAX_VALUE);
    }
}
