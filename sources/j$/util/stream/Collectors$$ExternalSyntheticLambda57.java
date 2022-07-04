package j$.util.stream;

import j$.util.Optional;
import j$.util.function.Function;
import j$.util.stream.Collectors;

public final /* synthetic */ class Collectors$$ExternalSyntheticLambda57 implements Function {
    public static final /* synthetic */ Collectors$$ExternalSyntheticLambda57 INSTANCE = new Collectors$$ExternalSyntheticLambda57();

    private /* synthetic */ Collectors$$ExternalSyntheticLambda57() {
    }

    public /* synthetic */ Function andThen(Function function) {
        return Function.CC.$default$andThen(this, function);
    }

    public final Object apply(Object obj) {
        return Optional.ofNullable(((Collectors.AnonymousClass1OptionalBox) obj).value);
    }

    public /* synthetic */ Function compose(Function function) {
        return Function.CC.$default$compose(this, function);
    }
}
