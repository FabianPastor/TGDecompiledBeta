package j$.util.stream;

import j$.util.IntSummaryStatistics;
import j$.util.function.BiFunction;
import j$.util.function.BinaryOperator;
import j$.util.function.Function;

public final /* synthetic */ class Collectors$$ExternalSyntheticLambda35 implements BinaryOperator {
    public static final /* synthetic */ Collectors$$ExternalSyntheticLambda35 INSTANCE = new Collectors$$ExternalSyntheticLambda35();

    private /* synthetic */ Collectors$$ExternalSyntheticLambda35() {
    }

    public /* synthetic */ BiFunction andThen(Function function) {
        return BiFunction.CC.$default$andThen(this, function);
    }

    public final Object apply(Object obj, Object obj2) {
        return ((IntSummaryStatistics) obj).combine((IntSummaryStatistics) obj2);
    }
}
