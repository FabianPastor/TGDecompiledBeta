package j$.util.function;

public interface Function<T, R> {

    /* renamed from: j$.util.function.Function$-CC  reason: invalid class name */
    public final /* synthetic */ class CC {
        public static <V> Function $default$andThen(Function function, Function function2) {
            function2.getClass();
            return new i(function, function2, 0);
        }

        public static <V> Function $default$compose(Function function, Function function2) {
            function2.getClass();
            return new i(function, function2, 1);
        }
    }

    <V> Function<T, V> andThen(Function<? super R, ? extends V> function);

    R apply(T t);

    <V> Function<V, R> compose(Function<? super V, ? extends T> function);
}
