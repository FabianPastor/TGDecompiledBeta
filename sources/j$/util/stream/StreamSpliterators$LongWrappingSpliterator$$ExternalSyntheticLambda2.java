package j$.util.stream;

import j$.util.function.Consumer;
import j$.util.function.LongConsumer;
import j$.util.stream.Sink;
import j$.util.stream.SpinedBuffer;

public final /* synthetic */ class StreamSpliterators$LongWrappingSpliterator$$ExternalSyntheticLambda2 implements Sink.OfLong {
    public final /* synthetic */ SpinedBuffer.OfLong f$0;

    public /* synthetic */ StreamSpliterators$LongWrappingSpliterator$$ExternalSyntheticLambda2(SpinedBuffer.OfLong ofLong) {
        this.f$0 = ofLong;
    }

    public /* synthetic */ void accept(double d) {
        Sink.CC.$default$accept((Sink) this, d);
    }

    public /* synthetic */ void accept(int i) {
        Sink.CC.$default$accept((Sink) this, i);
    }

    public final void accept(long j) {
        this.f$0.accept(j);
    }

    public /* synthetic */ void accept(Long l) {
        Sink.OfLong.CC.$default$accept((Sink.OfLong) this, l);
    }

    public /* bridge */ /* synthetic */ void accept(Object obj) {
        accept((Long) obj);
    }

    public /* synthetic */ Consumer andThen(Consumer consumer) {
        return Consumer.CC.$default$andThen(this, consumer);
    }

    public /* synthetic */ LongConsumer andThen(LongConsumer longConsumer) {
        return LongConsumer.CC.$default$andThen(this, longConsumer);
    }

    public /* synthetic */ void begin(long j) {
        Sink.CC.$default$begin(this, j);
    }

    public /* synthetic */ boolean cancellationRequested() {
        return Sink.CC.$default$cancellationRequested(this);
    }

    public /* synthetic */ void end() {
        Sink.CC.$default$end(this);
    }
}
