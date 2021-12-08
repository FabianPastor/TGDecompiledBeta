package j$.util.stream;

import j$.util.function.Function;

public final /* synthetic */ class Collectors$$ExternalSyntheticLambda54 implements Function {
    public static final /* synthetic */ Collectors$$ExternalSyntheticLambda54 INSTANCE = new Collectors$$ExternalSyntheticLambda54();

    private /* synthetic */ Collectors$$ExternalSyntheticLambda54() {
    }

    public /* synthetic */ Function andThen(Function function) {
        return Function.CC.$default$andThen(this, function);
    }

    public final Object apply(Object obj) {
        return Collectors.lambda$counting$9(obj);
    }

    public /* synthetic */ Function compose(Function function) {
        return Function.CC.$default$compose(this, function);
    }
}
