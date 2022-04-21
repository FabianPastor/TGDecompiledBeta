package j$.util.stream;

import j$.util.function.Consumer;
import j$.util.stream.StreamSpliterators;

public final /* synthetic */ class StreamSpliterators$SliceSpliterator$OfRef$$ExternalSyntheticLambda0 implements Consumer {
    public static final /* synthetic */ StreamSpliterators$SliceSpliterator$OfRef$$ExternalSyntheticLambda0 INSTANCE = new StreamSpliterators$SliceSpliterator$OfRef$$ExternalSyntheticLambda0();

    private /* synthetic */ StreamSpliterators$SliceSpliterator$OfRef$$ExternalSyntheticLambda0() {
    }

    public final void accept(Object obj) {
        StreamSpliterators.SliceSpliterator.OfRef.lambda$forEachRemaining$1(obj);
    }

    public /* synthetic */ Consumer andThen(Consumer consumer) {
        return Consumer.CC.$default$andThen(this, consumer);
    }
}
