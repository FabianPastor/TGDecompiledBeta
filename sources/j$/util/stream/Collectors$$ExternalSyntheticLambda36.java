package j$.util.stream;

import j$.util.function.BiFunction;
import j$.util.function.BinaryOperator;
import j$.util.function.Function;
import java.util.List;

public final /* synthetic */ class Collectors$$ExternalSyntheticLambda36 implements BinaryOperator {
    public static final /* synthetic */ Collectors$$ExternalSyntheticLambda36 INSTANCE = new Collectors$$ExternalSyntheticLambda36();

    private /* synthetic */ Collectors$$ExternalSyntheticLambda36() {
    }

    public /* synthetic */ BiFunction andThen(Function function) {
        return BiFunction.CC.$default$andThen(this, function);
    }

    public final Object apply(Object obj, Object obj2) {
        return ((List) obj).addAll((List) obj2);
    }
}
