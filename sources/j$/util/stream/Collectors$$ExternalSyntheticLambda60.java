package j$.util.stream;

import j$.util.function.Function;

public final /* synthetic */ class Collectors$$ExternalSyntheticLambda60 implements Function {
    public static final /* synthetic */ Collectors$$ExternalSyntheticLambda60 INSTANCE = new Collectors$$ExternalSyntheticLambda60();

    private /* synthetic */ Collectors$$ExternalSyntheticLambda60() {
    }

    public /* synthetic */ Function andThen(Function function) {
        return Function.CC.$default$andThen(this, function);
    }

    public final Object apply(Object obj) {
        return Integer.valueOf(((int[]) obj)[0]);
    }

    public /* synthetic */ Function compose(Function function) {
        return Function.CC.$default$compose(this, function);
    }
}
