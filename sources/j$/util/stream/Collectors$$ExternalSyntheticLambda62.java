package j$.util.stream;

import j$.util.function.Function;

public final /* synthetic */ class Collectors$$ExternalSyntheticLambda62 implements Function {
    public static final /* synthetic */ Collectors$$ExternalSyntheticLambda62 INSTANCE = new Collectors$$ExternalSyntheticLambda62();

    private /* synthetic */ Collectors$$ExternalSyntheticLambda62() {
    }

    public /* synthetic */ Function andThen(Function function) {
        return Function.CC.$default$andThen(this, function);
    }

    public final Object apply(Object obj) {
        return Collectors.lambda$averagingLong$29((long[]) obj);
    }

    public /* synthetic */ Function compose(Function function) {
        return Function.CC.$default$compose(this, function);
    }
}
