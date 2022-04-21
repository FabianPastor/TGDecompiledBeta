package j$.util.stream;

import j$.util.function.BiFunction;
import j$.util.function.BinaryOperator;
import j$.util.function.Function;
import j$.util.stream.Collectors;

public final /* synthetic */ class Collectors$$ExternalSyntheticLambda27 implements BinaryOperator {
    public final /* synthetic */ BinaryOperator f$0;

    public /* synthetic */ Collectors$$ExternalSyntheticLambda27(BinaryOperator binaryOperator) {
        this.f$0 = binaryOperator;
    }

    public /* synthetic */ BiFunction andThen(Function function) {
        return BiFunction.CC.$default$andThen(this, function);
    }

    public final Object apply(Object obj, Object obj2) {
        return Collectors.lambda$partitioningBy$55(this.f$0, (Collectors.Partition) obj, (Collectors.Partition) obj2);
    }
}
