package j$.util.stream;

import j$.util.function.DoubleConsumer;
import j$.util.stream.StreamSpliterators;

public final /* synthetic */ class StreamSpliterators$SliceSpliterator$OfDouble$$ExternalSyntheticLambda0 implements DoubleConsumer {
    public static final /* synthetic */ StreamSpliterators$SliceSpliterator$OfDouble$$ExternalSyntheticLambda0 INSTANCE = new StreamSpliterators$SliceSpliterator$OfDouble$$ExternalSyntheticLambda0();

    private /* synthetic */ StreamSpliterators$SliceSpliterator$OfDouble$$ExternalSyntheticLambda0() {
    }

    public final void accept(double d) {
        StreamSpliterators.SliceSpliterator.OfDouble.lambda$emptyConsumer$0(d);
    }

    public /* synthetic */ DoubleConsumer andThen(DoubleConsumer doubleConsumer) {
        return DoubleConsumer.CC.$default$andThen(this, doubleConsumer);
    }
}
