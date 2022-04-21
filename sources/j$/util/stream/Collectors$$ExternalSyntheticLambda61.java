package j$.util.stream;

import j$.util.function.Function;

public final /* synthetic */ class Collectors$$ExternalSyntheticLambda61 implements Function {
    public static final /* synthetic */ Collectors$$ExternalSyntheticLambda61 INSTANCE = new Collectors$$ExternalSyntheticLambda61();

    private /* synthetic */ Collectors$$ExternalSyntheticLambda61() {
    }

    public /* synthetic */ Function andThen(Function function) {
        return Function.CC.$default$andThen(this, function);
    }

    public final Object apply(Object obj) {
        return Collectors.lambda$averagingInt$25((long[]) obj);
    }

    public /* synthetic */ Function compose(Function function) {
        return Function.CC.$default$compose(this, function);
    }
}
