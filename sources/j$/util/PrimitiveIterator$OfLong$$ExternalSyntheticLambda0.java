package j$.util;

import j$.util.function.Consumer;
import j$.util.function.LongConsumer;

public final /* synthetic */ class PrimitiveIterator$OfLong$$ExternalSyntheticLambda0 implements LongConsumer {
    public final /* synthetic */ Consumer f$0;

    public /* synthetic */ PrimitiveIterator$OfLong$$ExternalSyntheticLambda0(Consumer consumer) {
        this.f$0 = consumer;
    }

    public final void accept(long j) {
        this.f$0.accept(Long.valueOf(j));
    }

    public /* synthetic */ LongConsumer andThen(LongConsumer longConsumer) {
        return LongConsumer.CC.$default$andThen(this, longConsumer);
    }
}
