package j$.util.function;

import j$.util.function.IntConsumer;

public final /* synthetic */ class IntConsumer$$ExternalSyntheticLambda0 implements IntConsumer {
    public final /* synthetic */ IntConsumer f$0;
    public final /* synthetic */ IntConsumer f$1;

    public /* synthetic */ IntConsumer$$ExternalSyntheticLambda0(IntConsumer intConsumer, IntConsumer intConsumer2) {
        this.f$0 = intConsumer;
        this.f$1 = intConsumer2;
    }

    public final void accept(int i) {
        IntConsumer.CC.lambda$andThen$0(this.f$0, this.f$1, i);
    }

    public /* synthetic */ IntConsumer andThen(IntConsumer intConsumer) {
        return IntConsumer.CC.$default$andThen(this, intConsumer);
    }
}
