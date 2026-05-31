/**
 * Data structure for storing two elements.
 *
 * @param <A> The type of the first element.
 * @param <B> The type of the second element.
 */

public class Pair<A, B> {
    public A first;
    public B second;

    public Pair(A first, B second) {
        this.first = first;
        this.second = second;
    }
}
