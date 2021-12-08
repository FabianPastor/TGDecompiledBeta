package j$.util.stream;

import j$.util.function.Consumer;
import j$.util.stream.StreamSpliterators;

public final /* synthetic */ class StreamSpliterators$DistinctSpliterator$$ExternalSyntheticLambda0 implements Consumer {
    public final /* synthetic */ StreamSpliterators.DistinctSpliterator f$0;
    public final /* synthetic */ Consumer f$1;

    public /* synthetic */ StreamSpliterators$DistinctSpliterator$$ExternalSyntheticLambda0(StreamSpliterators.DistinctSpliterator distinctSpliterator, Consumer consumer) {
        this.f$0 = distinctSpliterator;
        this.f$1 = consumer;
    }

    public final void accept(Object obj) {
        this.f$0.m5xb9bff3f1(this.f$1, obj);
    }

    public /* synthetic */ Consumer andThen(Consumer consumer) {
        return Consumer.CC.$default$andThen(this, consumer);
    }
}
