/**
 * Data structure for storing three elements.
 *
 * @param <A> The type of the first element.
 * @param <B> The type of the second element.
 * @param <C> The type of the third element.
 */

public class Trip<A, B, C> {
    public A first;
    public B second;
    public C third;

    public Trip(A first, B second, C third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }
}
