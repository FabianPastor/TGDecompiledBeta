package j$.util.function;

public interface BiFunction<T, U, R> {
    <V> BiFunction<T, U, V> andThen(Function<? super R, ? extends V> function);

    R apply(T t, U u);

    /* renamed from: j$.util.function.BiFunction$-CC  reason: invalid class name */
    public final /* synthetic */ class CC {
        public static <V> BiFunction $default$andThen(BiFunction _this, Function function) {
            function.getClass();
            return new BiFunction$$ExternalSyntheticLambda0(_this, function);
        }
    }
}
