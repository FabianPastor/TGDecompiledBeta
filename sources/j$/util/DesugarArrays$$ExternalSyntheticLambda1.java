package j$.util;

import j$.util.function.IntConsumer;
import j$.util.function.IntUnaryOperator;

public final /* synthetic */ class DesugarArrays$$ExternalSyntheticLambda1 implements IntConsumer {
    public final /* synthetic */ int[] f$0;
    public final /* synthetic */ IntUnaryOperator f$1;

    public /* synthetic */ DesugarArrays$$ExternalSyntheticLambda1(int[] iArr, IntUnaryOperator intUnaryOperator) {
        this.f$0 = iArr;
        this.f$1 = intUnaryOperator;
    }

    public final void accept(int i) {
        DesugarArrays.lambda$parallelSetAll$1(this.f$0, this.f$1, i);
    }

    public /* synthetic */ IntConsumer andThen(IntConsumer intConsumer) {
        return IntConsumer.CC.$default$andThen(this, intConsumer);
    }
}
