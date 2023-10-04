

package connectx.crace;

import connectx.CXPlayer;
import connectx.CXBoard;
import connectx.CXGameState;


public class GTBoard{
    public int eval;
    public int alpha;
    public int beta;
    public CXBoard board;

    public GTBoard(CXBoard b){
        this.board = b;
        eval = 0;
        alpha = -1;
        beta = 1;
    }
}
