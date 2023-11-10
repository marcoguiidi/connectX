package connectx.crace;

public class valDepth implements Comparable<valDepth>{
    
    public int val;
    public int depth;

    public valDepth(int v, int d){
        val = v;
        depth = d;
    }

    public int compareTo(valDepth o) {
        if (this.val > o.val) {
            return 1;
        }
        else if (this.val < o.val) {
            return -1;
        }
        else{
            if (this.depth > o.depth) {
                return 1;
            }
            else if (this.depth < o.depth) {
                return -1;
            }
            else return 0;
        }
    }

    
}
