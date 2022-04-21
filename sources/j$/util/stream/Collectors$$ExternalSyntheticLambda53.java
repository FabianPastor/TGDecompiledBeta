package j$.util.stream;

import j$.util.function.Function;

public final /* synthetic */ class Collectors$$ExternalSyntheticLambda53 implements Function {
    public static final /* synthetic */ Collectors$$ExternalSyntheticLambda53 INSTANCE = new Collectors$$ExternalSyntheticLambda53();

    private /* synthetic */ Collectors$$ExternalSyntheticLambda53() {
    }

    public /* synthetic */ Function andThen(Function function) {
        return Function.CC.$default$andThen(this, function);
    }

    public final Object apply(Object obj) {
        return Collectors.lambda$castingIdentity$1(obj);
    }

    public /* synthetic */ Function compose(Function function) {
        return Function.CC.$default$compose(this, function);
    }
}
