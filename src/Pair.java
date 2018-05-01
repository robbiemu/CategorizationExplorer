/**
 * @author Robert Collins
 * @version 29-4-2018
 */

/* an abstract Left/Right pair */
public class Pair<T> {
    protected T left;
    protected T right;

    public Pair(T left, T right) {
        this.left = left;
        this.right = right;
    }
    public Pair(T [] data) {
        this.left = data[0];
        this.right = data[1];
    }

    public T getLeft() {
        return left;
    }

    public void setLeft(T left) {
        this.left = left;
    }

    public T getRight() {
        return right;
    }

    public void setRight(T right) {
        this.right = right;
    }

    @Override
    public String toString() {
        return "Pair:{"+left+", "+right+"}";
    }
}
