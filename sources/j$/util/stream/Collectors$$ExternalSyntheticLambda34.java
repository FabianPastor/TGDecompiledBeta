package j$.util.stream;

import j$.util.DoubleSummaryStatistics;
import j$.util.function.BiFunction;
import j$.util.function.BinaryOperator;
import j$.util.function.Function;

public final /* synthetic */ class Collectors$$ExternalSyntheticLambda34 implements BinaryOperator {
    public static final /* synthetic */ Collectors$$ExternalSyntheticLambda34 INSTANCE = new Collectors$$ExternalSyntheticLambda34();

    private /* synthetic */ Collectors$$ExternalSyntheticLambda34() {
    }

    public /* synthetic */ BiFunction andThen(Function function) {
        return BiFunction.CC.$default$andThen(this, function);
    }

    public final Object apply(Object obj, Object obj2) {
        return ((DoubleSummaryStatistics) obj).combine((DoubleSummaryStatistics) obj2);
    }
}
