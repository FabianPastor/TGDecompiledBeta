package j$.util.stream;

import j$.util.function.BiConsumer;
import j$.util.function.BinaryOperator;
import j$.util.function.Function;

public final /* synthetic */ class Collectors$$ExternalSyntheticLambda3 implements BiConsumer {
    public final /* synthetic */ BinaryOperator f$0;
    public final /* synthetic */ Function f$1;

    public /* synthetic */ Collectors$$ExternalSyntheticLambda3(BinaryOperator binaryOperator, Function function) {
        this.f$0 = binaryOperator;
        this.f$1 = function;
    }

    public final void accept(Object obj, Object obj2) {
        Collectors.lambda$reducing$41(this.f$0, this.f$1, (Object[]) obj, obj2);
    }

    public /* synthetic */ BiConsumer andThen(BiConsumer biConsumer) {
        return BiConsumer.CC.$default$andThen(this, biConsumer);
    }
}
