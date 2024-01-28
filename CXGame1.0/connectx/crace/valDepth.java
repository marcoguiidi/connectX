package connectx.crace;

/*
 * classe di dato utilizzata per ordinare le mosse e restituire la mossa migliore calcolata
 */
public class valDepth implements Comparable<valDepth>{   
    
    public int val;
    public int depth;
    boolean maximize;

    public valDepth(int v, int d){
        val = v;
        depth = d;
    }

    public void setBool(boolean bool){
        maximize = bool;
    }

    /*
     * reimplementazione di compareTo, che a parità di valore, prioritizza la colonna che vince in meno mosse / perde in più mosse
     */
    public int compareTo(valDepth o) {  
        if (maximize) {
            if (this.val > o.val) {
                return 1;
            }
            else if (this.val < o.val) {
                return -1;
            }
            else {
                if (this.val == Integer.MAX_VALUE) {
                    if (this.depth < o.depth) {
                        return 1;
                    }
                    else if (this.depth > o.depth) {
                        return -1;
                    }
                    else return 0;
                }
                else if (this.val == Integer.MIN_VALUE) {
                    if (this.depth > o.depth) {
                        return 1;
                    }
                    else if (this.depth < o.depth) {
                        return -1;
                    }
                    else return 0;
                }
                else return 0;
            }
        }
        else{
            if (this.val < o.val) {
                return 1;
            }
            else if (this.val > o.val) {
                return -1;
            }
            else {
                if (this.val == Integer.MAX_VALUE) {
                    if (this.depth > o.depth) {
                        return 1;
                    }
                    else if (this.depth < o.depth) {
                        return -1;
                    }
                    else return 0;
                }
                else if (this.val == Integer.MIN_VALUE) {
                    if (this.depth < o.depth) {
                        return 1;
                    }
                    else if (this.depth > o.depth) {
                        return -1;
                    }
                    else return 0;
                }
                else return 0;
            }
        }
    }
}