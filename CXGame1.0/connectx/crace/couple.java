package connectx.crace;

import java.util.Arrays;
import java.util.Comparator;

public class couple implements Comparable<couple>{

    int column;
    int value;

    public couple(int column, int value){
        this.column = column;
        this.value = value;
    }

    public int getColumn(){
        return column;
    }

    public int getValue(){
        return value;
    }

    public void setColumn(int c){
        column = c;
    }

    public void setValue(int v){
        value = v;
    }

    @Override
    public int compareTo(couple otherCouple) {
        if (this.getValue() > otherCouple.getValue()) {
            return 1;
        }

        else if (this.getValue() < otherCouple.getValue()) {
            return -1;
        }

        else return 0;
    }   
}
