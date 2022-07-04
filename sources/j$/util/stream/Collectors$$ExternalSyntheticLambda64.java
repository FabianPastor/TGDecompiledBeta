package j$.util.stream;

import j$.util.function.Function;

public final /* synthetic */ class Collectors$$ExternalSyntheticLambda64 implements Function {
    public static final /* synthetic */ Collectors$$ExternalSyntheticLambda64 INSTANCE = new Collectors$$ExternalSyntheticLambda64();

    private /* synthetic */ Collectors$$ExternalSyntheticLambda64() {
    }

    public /* synthetic */ Function andThen(Function function) {
        return Function.CC.$default$andThen(this, function);
    }

    public final Object apply(Object obj) {
        return Collectors.lambda$reducing$36((Object[]) obj);
    }

    public /* synthetic */ Function compose(Function function) {
        return Function.CC.$default$compose(this, function);
    }
}
