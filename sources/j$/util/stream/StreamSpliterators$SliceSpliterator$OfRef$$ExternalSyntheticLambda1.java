package j$.util.stream;

import j$.util.function.Consumer;
import j$.util.stream.StreamSpliterators;

public final /* synthetic */ class StreamSpliterators$SliceSpliterator$OfRef$$ExternalSyntheticLambda1 implements Consumer {
    public static final /* synthetic */ StreamSpliterators$SliceSpliterator$OfRef$$ExternalSyntheticLambda1 INSTANCE = new StreamSpliterators$SliceSpliterator$OfRef$$ExternalSyntheticLambda1();

    private /* synthetic */ StreamSpliterators$SliceSpliterator$OfRef$$ExternalSyntheticLambda1() {
    }

    public final void accept(Object obj) {
        StreamSpliterators.SliceSpliterator.OfRef.lambda$tryAdvance$0(obj);
    }

    public /* synthetic */ Consumer andThen(Consumer consumer) {
        return Consumer.CC.$default$andThen(this, consumer);
    }
}
