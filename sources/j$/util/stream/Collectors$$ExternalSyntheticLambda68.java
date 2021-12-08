package j$.util.stream;

import j$.util.function.BinaryOperator;
import j$.util.function.Supplier;

public final /* synthetic */ class Collectors$$ExternalSyntheticLambda68 implements Supplier {
    public final /* synthetic */ BinaryOperator f$0;

    public /* synthetic */ Collectors$$ExternalSyntheticLambda68(BinaryOperator binaryOperator) {
        this.f$0 = binaryOperator;
    }

    public final Object get() {
        return Collectors.lambda$reducing$38(this.f$0);
    }
}
