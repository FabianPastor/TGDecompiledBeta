package j$.util.stream;

import j$.util.function.BiFunction;
import j$.util.function.BinaryOperator;
import j$.util.function.Function;
import java.util.Set;

public final /* synthetic */ class Collectors$$ExternalSyntheticLambda38 implements BinaryOperator {
    public static final /* synthetic */ Collectors$$ExternalSyntheticLambda38 INSTANCE = new Collectors$$ExternalSyntheticLambda38();

    private /* synthetic */ Collectors$$ExternalSyntheticLambda38() {
    }

    public /* synthetic */ BiFunction andThen(Function function) {
        return BiFunction.CC.$default$andThen(this, function);
    }

    public final Object apply(Object obj, Object obj2) {
        return ((Set) obj).addAll((Set) obj2);
    }
}
