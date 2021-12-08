package j$.util.function;

import j$.util.function.LongConsumer;

public final /* synthetic */ class LongConsumer$$ExternalSyntheticLambda0 implements LongConsumer {
    public final /* synthetic */ LongConsumer f$0;
    public final /* synthetic */ LongConsumer f$1;

    public /* synthetic */ LongConsumer$$ExternalSyntheticLambda0(LongConsumer longConsumer, LongConsumer longConsumer2) {
        this.f$0 = longConsumer;
        this.f$1 = longConsumer2;
    }

    public final void accept(long j) {
        LongConsumer.CC.lambda$andThen$0(this.f$0, this.f$1, j);
    }

    public /* synthetic */ LongConsumer andThen(LongConsumer longConsumer) {
        return LongConsumer.CC.$default$andThen(this, longConsumer);
    }
}
