package j$.util.stream;

import j$.util.function.LongConsumer;
import j$.util.stream.StreamSpliterators;

public final /* synthetic */ class StreamSpliterators$SliceSpliterator$OfLong$$ExternalSyntheticLambda0 implements LongConsumer {
    public static final /* synthetic */ StreamSpliterators$SliceSpliterator$OfLong$$ExternalSyntheticLambda0 INSTANCE = new StreamSpliterators$SliceSpliterator$OfLong$$ExternalSyntheticLambda0();

    private /* synthetic */ StreamSpliterators$SliceSpliterator$OfLong$$ExternalSyntheticLambda0() {
    }

    public final void accept(long j) {
        StreamSpliterators.SliceSpliterator.OfLong.lambda$emptyConsumer$0(j);
    }

    public /* synthetic */ LongConsumer andThen(LongConsumer longConsumer) {
        return LongConsumer.CC.$default$andThen(this, longConsumer);
    }
}
