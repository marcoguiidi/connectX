package connectx.crace;
import java.util.Map;


public class couple<K, V> implements Map.Entry<K, V>{

    private K column;
    private V value;

    public couple(K col, V val) {
        this.value = val;
        this.column = col;
    }

    @Override
    public K getKey() {
        return column;
    }

    @Override
    public V getValue() {
        return value;
    }

    @Override
    public V setValue(V val) {
        V oldValue = this.value;
        this.value = val;
        return oldValue;
    }

    public K setCol(K col){
        K oldCol = this.column;
        this.column = col;
        return oldCol;
    }
}