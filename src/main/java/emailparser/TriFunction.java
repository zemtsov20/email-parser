package emailparser;

@FunctionalInterface
public interface TriFunction<F, S, T> {
    void apply(F f, S s, T t);
}
