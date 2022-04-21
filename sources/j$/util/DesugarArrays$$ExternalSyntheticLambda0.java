package j$.util;

import j$.util.function.IntConsumer;
import j$.util.function.IntToDoubleFunction;

public final /* synthetic */ class DesugarArrays$$ExternalSyntheticLambda0 implements IntConsumer {
    public final /* synthetic */ double[] f$0;
    public final /* synthetic */ IntToDoubleFunction f$1;

    public /* synthetic */ DesugarArrays$$ExternalSyntheticLambda0(double[] dArr, IntToDoubleFunction intToDoubleFunction) {
        this.f$0 = dArr;
        this.f$1 = intToDoubleFunction;
    }

    public final void accept(int i) {
        DesugarArrays.lambda$parallelSetAll$3(this.f$0, this.f$1, i);
    }

    public /* synthetic */ IntConsumer andThen(IntConsumer intConsumer) {
        return IntConsumer.CC.$default$andThen(this, intConsumer);
    }
}
