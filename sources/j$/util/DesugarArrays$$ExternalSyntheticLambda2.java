package j$.util;

import j$.util.function.IntConsumer;
import j$.util.function.IntToLongFunction;

public final /* synthetic */ class DesugarArrays$$ExternalSyntheticLambda2 implements IntConsumer {
    public final /* synthetic */ long[] f$0;
    public final /* synthetic */ IntToLongFunction f$1;

    public /* synthetic */ DesugarArrays$$ExternalSyntheticLambda2(long[] jArr, IntToLongFunction intToLongFunction) {
        this.f$0 = jArr;
        this.f$1 = intToLongFunction;
    }

    public final void accept(int i) {
        DesugarArrays.lambda$parallelSetAll$2(this.f$0, this.f$1, i);
    }

    public /* synthetic */ IntConsumer andThen(IntConsumer intConsumer) {
        return IntConsumer.CC.$default$andThen(this, intConsumer);
    }
}
