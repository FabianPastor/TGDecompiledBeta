package j$.util.stream;

import j$.util.function.Function;

public final /* synthetic */ class Collectors$$ExternalSyntheticLambda58 implements Function {
    public static final /* synthetic */ Collectors$$ExternalSyntheticLambda58 INSTANCE = new Collectors$$ExternalSyntheticLambda58();

    private /* synthetic */ Collectors$$ExternalSyntheticLambda58() {
    }

    public /* synthetic */ Function andThen(Function function) {
        return Function.CC.$default$andThen(this, function);
    }

    public final Object apply(Object obj) {
        return Collectors.lambda$averagingDouble$33((double[]) obj);
    }

    public /* synthetic */ Function compose(Function function) {
        return Function.CC.$default$compose(this, function);
    }
}
