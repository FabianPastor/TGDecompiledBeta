package j$.util.stream;

import j$.util.LongSummaryStatistics;
import j$.util.function.BiFunction;
import j$.util.function.BinaryOperator;
import j$.util.function.Function;

public final /* synthetic */ class Collectors$$ExternalSyntheticLambda37 implements BinaryOperator {
    public static final /* synthetic */ Collectors$$ExternalSyntheticLambda37 INSTANCE = new Collectors$$ExternalSyntheticLambda37();

    private /* synthetic */ Collectors$$ExternalSyntheticLambda37() {
    }

    public /* synthetic */ BiFunction andThen(Function function) {
        return BiFunction.CC.$default$andThen(this, function);
    }

    public final Object apply(Object obj, Object obj2) {
        return ((LongSummaryStatistics) obj).combine((LongSummaryStatistics) obj2);
    }
}
