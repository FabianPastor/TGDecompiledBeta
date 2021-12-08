package j$.util.stream;

import j$.util.function.Function;

public final /* synthetic */ class Collectors$$ExternalSyntheticLambda59 implements Function {
    public static final /* synthetic */ Collectors$$ExternalSyntheticLambda59 INSTANCE = new Collectors$$ExternalSyntheticLambda59();

    private /* synthetic */ Collectors$$ExternalSyntheticLambda59() {
    }

    public /* synthetic */ Function andThen(Function function) {
        return Function.CC.$default$andThen(this, function);
    }

    public final Object apply(Object obj) {
        return Double.valueOf(Collectors.computeFinalSum((double[]) obj));
    }

    public /* synthetic */ Function compose(Function function) {
        return Function.CC.$default$compose(this, function);
    }
}
