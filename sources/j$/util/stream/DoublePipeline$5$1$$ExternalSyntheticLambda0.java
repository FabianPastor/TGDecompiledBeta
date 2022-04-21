package j$.util.stream;

import j$.util.function.DoubleConsumer;
import j$.util.stream.DoublePipeline;

public final /* synthetic */ class DoublePipeline$5$1$$ExternalSyntheticLambda0 implements DoubleConsumer {
    public final /* synthetic */ DoublePipeline.AnonymousClass5.AnonymousClass1 f$0;

    public /* synthetic */ DoublePipeline$5$1$$ExternalSyntheticLambda0(DoublePipeline.AnonymousClass5.AnonymousClass1 r1) {
        this.f$0 = r1;
    }

    public final void accept(double d) {
        this.f$0.m512lambda$accept$0$javautilstreamDoublePipeline$5$1(d);
    }

    public /* synthetic */ DoubleConsumer andThen(DoubleConsumer doubleConsumer) {
        return DoubleConsumer.CC.$default$andThen(this, doubleConsumer);
    }
}
