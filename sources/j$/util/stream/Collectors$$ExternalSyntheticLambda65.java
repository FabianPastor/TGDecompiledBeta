package j$.util.stream;

import j$.util.function.Function;

public final /* synthetic */ class Collectors$$ExternalSyntheticLambda65 implements Function {
    public static final /* synthetic */ Collectors$$ExternalSyntheticLambda65 INSTANCE = new Collectors$$ExternalSyntheticLambda65();

    private /* synthetic */ Collectors$$ExternalSyntheticLambda65() {
    }

    public /* synthetic */ Function andThen(Function function) {
        return Function.CC.$default$andThen(this, function);
    }

    public final Object apply(Object obj) {
        return Collectors.lambda$reducing$43((Object[]) obj);
    }

    public /* synthetic */ Function compose(Function function) {
        return Function.CC.$default$compose(this, function);
    }
}
