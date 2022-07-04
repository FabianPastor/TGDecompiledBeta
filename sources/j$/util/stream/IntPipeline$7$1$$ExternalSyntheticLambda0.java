package j$.util.stream;

import j$.util.function.IntConsumer;
import j$.util.stream.IntPipeline;

public final /* synthetic */ class IntPipeline$7$1$$ExternalSyntheticLambda0 implements IntConsumer {
    public final /* synthetic */ IntPipeline.AnonymousClass7.AnonymousClass1 f$0;

    public /* synthetic */ IntPipeline$7$1$$ExternalSyntheticLambda0(IntPipeline.AnonymousClass7.AnonymousClass1 r1) {
        this.f$0 = r1;
    }

    public final void accept(int i) {
        this.f$0.m522lambda$accept$0$javautilstreamIntPipeline$7$1(i);
    }

    public /* synthetic */ IntConsumer andThen(IntConsumer intConsumer) {
        return IntConsumer.CC.$default$andThen(this, intConsumer);
    }
}
