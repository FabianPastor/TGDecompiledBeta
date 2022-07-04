package j$.util.stream;

import j$.util.function.LongConsumer;

public final /* synthetic */ class LongPipeline$$ExternalSyntheticLambda7 implements LongConsumer {
    public final /* synthetic */ Sink f$0;

    public /* synthetic */ LongPipeline$$ExternalSyntheticLambda7(Sink sink) {
        this.f$0 = sink;
    }

    public final void accept(long j) {
        this.f$0.accept(j);
    }

    public /* synthetic */ LongConsumer andThen(LongConsumer longConsumer) {
        return LongConsumer.CC.$default$andThen(this, longConsumer);
    }
}
