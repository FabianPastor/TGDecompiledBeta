package j$.util.function;

import j$.util.function.BiFunction;
import j$.util.function.BinaryOperator;
import java.util.Comparator;

public final /* synthetic */ class BinaryOperator$$ExternalSyntheticLambda0 implements BinaryOperator {
    public final /* synthetic */ Comparator f$0;

    public /* synthetic */ BinaryOperator$$ExternalSyntheticLambda0(Comparator comparator) {
        this.f$0 = comparator;
    }

    public /* synthetic */ BiFunction andThen(Function function) {
        return BiFunction.CC.$default$andThen(this, function);
    }

    public final Object apply(Object obj, Object obj2) {
        return BinaryOperator.CC.lambda$maxBy$1(this.f$0, obj, obj2);
    }
}
