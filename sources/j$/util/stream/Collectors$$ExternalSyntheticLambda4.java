package j$.util.stream;

import j$.util.Map;
import j$.util.function.BiConsumer;
import j$.util.function.BinaryOperator;
import j$.util.function.Function;

public final /* synthetic */ class Collectors$$ExternalSyntheticLambda4 implements BiConsumer {
    public final /* synthetic */ Function f$0;
    public final /* synthetic */ Function f$1;
    public final /* synthetic */ BinaryOperator f$2;

    public /* synthetic */ Collectors$$ExternalSyntheticLambda4(Function function, Function function2, BinaryOperator binaryOperator) {
        this.f$0 = function;
        this.f$1 = function2;
        this.f$2 = binaryOperator;
    }

    public final void accept(Object obj, Object obj2) {
        Map.EL.merge((java.util.Map) obj, this.f$0.apply(obj2), this.f$1.apply(obj2), this.f$2);
    }

    public /* synthetic */ BiConsumer andThen(BiConsumer biConsumer) {
        return BiConsumer.CC.$default$andThen(this, biConsumer);
    }
}
