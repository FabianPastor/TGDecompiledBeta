package j$.util.stream;

import j$.util.function.BiFunction;
import j$.util.function.BinaryOperator;
import j$.util.function.Function;

public final /* synthetic */ class Collectors$$ExternalSyntheticLambda28 implements BinaryOperator {
    public final /* synthetic */ BinaryOperator f$0;

    public /* synthetic */ Collectors$$ExternalSyntheticLambda28(BinaryOperator binaryOperator) {
        this.f$0 = binaryOperator;
    }

    public /* synthetic */ BiFunction andThen(Function function) {
        return BiFunction.CC.$default$andThen(this, function);
    }

    public final Object apply(Object obj, Object obj2) {
        return Collectors.lambda$reducing$35(this.f$0, (Object[]) obj, (Object[]) obj2);
    }
}
