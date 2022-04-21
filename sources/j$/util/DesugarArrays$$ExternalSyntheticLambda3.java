package j$.util;

import j$.util.function.IntConsumer;
import j$.util.function.IntFunction;

public final /* synthetic */ class DesugarArrays$$ExternalSyntheticLambda3 implements IntConsumer {
    public final /* synthetic */ Object[] f$0;
    public final /* synthetic */ IntFunction f$1;

    public /* synthetic */ DesugarArrays$$ExternalSyntheticLambda3(Object[] objArr, IntFunction intFunction) {
        this.f$0 = objArr;
        this.f$1 = intFunction;
    }

    public final void accept(int i) {
        DesugarArrays.lambda$parallelSetAll$0(this.f$0, this.f$1, i);
    }

    public /* synthetic */ IntConsumer andThen(IntConsumer intConsumer) {
        return IntConsumer.CC.$default$andThen(this, intConsumer);
    }
}
