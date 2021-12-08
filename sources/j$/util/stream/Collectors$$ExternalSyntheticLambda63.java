package j$.util.stream;

import j$.util.function.Function;

public final /* synthetic */ class Collectors$$ExternalSyntheticLambda63 implements Function {
    public static final /* synthetic */ Collectors$$ExternalSyntheticLambda63 INSTANCE = new Collectors$$ExternalSyntheticLambda63();

    private /* synthetic */ Collectors$$ExternalSyntheticLambda63() {
    }

    public /* synthetic */ Function andThen(Function function) {
        return Function.CC.$default$andThen(this, function);
    }

    public final Object apply(Object obj) {
        return Long.valueOf(((long[]) obj)[0]);
    }

    public /* synthetic */ Function compose(Function function) {
        return Function.CC.$default$compose(this, function);
    }
}
