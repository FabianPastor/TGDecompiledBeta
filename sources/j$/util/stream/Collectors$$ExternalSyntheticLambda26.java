package j$.util.stream;

import j$.util.function.BiFunction;
import j$.util.function.BinaryOperator;
import j$.util.function.Function;
import java.util.Map;

public final /* synthetic */ class Collectors$$ExternalSyntheticLambda26 implements BinaryOperator {
    public final /* synthetic */ BinaryOperator f$0;

    public /* synthetic */ Collectors$$ExternalSyntheticLambda26(BinaryOperator binaryOperator) {
        this.f$0 = binaryOperator;
    }

    public /* synthetic */ BiFunction andThen(Function function) {
        return BiFunction.CC.$default$andThen(this, function);
    }

    public final Object apply(Object obj, Object obj2) {
        return Collectors.lambda$mapMerger$7(this.f$0, (Map) obj, (Map) obj2);
    }
}
