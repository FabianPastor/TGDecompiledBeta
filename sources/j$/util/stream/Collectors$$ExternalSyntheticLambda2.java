package j$.util.stream;

import j$.util.function.BiConsumer;
import j$.util.function.BinaryOperator;

public final /* synthetic */ class Collectors$$ExternalSyntheticLambda2 implements BiConsumer {
    public final /* synthetic */ BinaryOperator f$0;

    public /* synthetic */ Collectors$$ExternalSyntheticLambda2(BinaryOperator binaryOperator) {
        this.f$0 = binaryOperator;
    }

    public final void accept(Object obj, Object obj2) {
        Collectors.lambda$reducing$34(this.f$0, (Object[]) obj, obj2);
    }

    public /* synthetic */ BiConsumer andThen(BiConsumer biConsumer) {
        return BiConsumer.CC.$default$andThen(this, biConsumer);
    }
}
