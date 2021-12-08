package j$.util.stream;

import j$.util.function.BiFunction;
import j$.util.function.BinaryOperator;
import j$.util.function.Function;
import java.util.Collection;

public final /* synthetic */ class Collectors$$ExternalSyntheticLambda33 implements BinaryOperator {
    public static final /* synthetic */ Collectors$$ExternalSyntheticLambda33 INSTANCE = new Collectors$$ExternalSyntheticLambda33();

    private /* synthetic */ Collectors$$ExternalSyntheticLambda33() {
    }

    public /* synthetic */ BiFunction andThen(Function function) {
        return BiFunction.CC.$default$andThen(this, function);
    }

    public final Object apply(Object obj, Object obj2) {
        return ((Collection) obj).addAll((Collection) obj2);
    }
}
