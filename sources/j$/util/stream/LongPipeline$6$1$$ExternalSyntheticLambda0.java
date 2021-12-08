package j$.util.stream;

import j$.util.function.LongConsumer;
import j$.util.stream.LongPipeline;

public final /* synthetic */ class LongPipeline$6$1$$ExternalSyntheticLambda0 implements LongConsumer {
    public final /* synthetic */ LongPipeline.AnonymousClass6.AnonymousClass1 f$0;

    public /* synthetic */ LongPipeline$6$1$$ExternalSyntheticLambda0(LongPipeline.AnonymousClass6.AnonymousClass1 r1) {
        this.f$0 = r1;
    }

    public final void accept(long j) {
        this.f$0.m4lambda$accept$0$javautilstreamLongPipeline$6$1(j);
    }

    public /* synthetic */ LongConsumer andThen(LongConsumer longConsumer) {
        return LongConsumer.CC.$default$andThen(this, longConsumer);
    }
}
