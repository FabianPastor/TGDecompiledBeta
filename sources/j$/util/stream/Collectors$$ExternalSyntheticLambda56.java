package j$.util.stream;

import j$.util.StringJoiner;
import j$.util.function.Function;

public final /* synthetic */ class Collectors$$ExternalSyntheticLambda56 implements Function {
    public static final /* synthetic */ Collectors$$ExternalSyntheticLambda56 INSTANCE = new Collectors$$ExternalSyntheticLambda56();

    private /* synthetic */ Collectors$$ExternalSyntheticLambda56() {
    }

    public /* synthetic */ Function andThen(Function function) {
        return Function.CC.$default$andThen(this, function);
    }

    public final Object apply(Object obj) {
        return ((StringJoiner) obj).toString();
    }

    public /* synthetic */ Function compose(Function function) {
        return Function.CC.$default$compose(this, function);
    }
}
