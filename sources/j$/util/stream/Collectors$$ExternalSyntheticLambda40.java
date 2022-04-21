package j$.util.stream;

import j$.util.function.BiFunction;
import j$.util.function.BinaryOperator;
import j$.util.function.Function;
import j$.util.stream.Collectors;

public final /* synthetic */ class Collectors$$ExternalSyntheticLambda40 implements BinaryOperator {
    public static final /* synthetic */ Collectors$$ExternalSyntheticLambda40 INSTANCE = new Collectors$$ExternalSyntheticLambda40();

    private /* synthetic */ Collectors$$ExternalSyntheticLambda40() {
    }

    public /* synthetic */ BiFunction andThen(Function function) {
        return BiFunction.CC.$default$andThen(this, function);
    }

    public final Object apply(Object obj, Object obj2) {
        return Collectors.lambda$reducing$39((Collectors.AnonymousClass1OptionalBox) obj, (Collectors.AnonymousClass1OptionalBox) obj2);
    }
}
