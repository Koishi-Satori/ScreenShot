package top.kkoishi.screen;

@FunctionalInterface
public interface Function {
    void apply();

    public interface SinFunction<T> extends Function {
        void apply (T arg);
    }
}
