package j$.util.stream;

import j$.util.function.Consumer;
import j$.util.function.DoubleConsumer;
import j$.util.stream.Sink;

public final /* synthetic */ class StreamSpliterators$DoubleWrappingSpliterator$$ExternalSyntheticLambda1 implements Sink.OfDouble {
    public final /* synthetic */ DoubleConsumer f$0;

    public /* synthetic */ StreamSpliterators$DoubleWrappingSpliterator$$ExternalSyntheticLambda1(DoubleConsumer doubleConsumer) {
        this.f$0 = doubleConsumer;
    }

    public final void accept(double d) {
        this.f$0.accept(d);
    }

    public /* synthetic */ void accept(int i) {
        Sink.CC.$default$accept((Sink) this, i);
    }

    public /* synthetic */ void accept(long j) {
        Sink.CC.$default$accept((Sink) this, j);
    }

    public /* synthetic */ void accept(Double d) {
        Sink.OfDouble.CC.$default$accept((Sink.OfDouble) this, d);
    }

    public /* bridge */ /* synthetic */ void accept(Object obj) {
        accept((Double) obj);
    }

    public /* synthetic */ Consumer andThen(Consumer consumer) {
        return Consumer.CC.$default$andThen(this, consumer);
    }

    public /* synthetic */ DoubleConsumer andThen(DoubleConsumer doubleConsumer) {
        return DoubleConsumer.CC.$default$andThen(this, doubleConsumer);
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
