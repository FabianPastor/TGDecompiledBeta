package j$.util.stream;

import j$.util.function.Function;

public final /* synthetic */ class Collectors$$ExternalSyntheticLambda55 implements Function {
    public static final /* synthetic */ Collectors$$ExternalSyntheticLambda55 INSTANCE = new Collectors$$ExternalSyntheticLambda55();

    private /* synthetic */ Collectors$$ExternalSyntheticLambda55() {
    }

    public /* synthetic */ Function andThen(Function function) {
        return Function.CC.$default$andThen(this, function);
    }

    public final Object apply(Object obj) {
        return ((StringBuilder) obj).toString();
    }

    public /* synthetic */ Function compose(Function function) {
        return Function.CC.$default$compose(this, function);
    }
}