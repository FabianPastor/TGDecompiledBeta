package j$.util.function;

public interface Function<T, R> {
    <V> Function<T, V> andThen(Function<? super R, ? extends V> function);

    R apply(T t);

    <V> Function<V, R> compose(Function<? super V, ? extends T> function);

    /* renamed from: j$.util.function.Function$-CC  reason: invalid class name */
    public final /* synthetic */ class CC {
        public static <V> Function $default$compose(Function _this, Function function) {
            function.getClass();
            return new Function$$ExternalSyntheticLambda1(_this, function);
        }

        public static <V> Function $default$andThen(Function _this, Function function) {
            function.getClass();
            return new Function$$ExternalSyntheticLambda0(_this, function);
        }

        public static <T> Function<T, T> identity() {
            return Function$$ExternalSyntheticLambda2.INSTANCE;
        }

        public static /* synthetic */ Object lambda$identity$2(Object t) {
            return t;
        }
    }
}
