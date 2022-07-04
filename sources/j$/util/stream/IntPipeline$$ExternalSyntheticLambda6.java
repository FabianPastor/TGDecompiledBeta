package j$.util.stream;

import j$.util.function.IntConsumer;

public final /* synthetic */ class IntPipeline$$ExternalSyntheticLambda6 implements IntConsumer {
    public final /* synthetic */ Sink f$0;

    public /* synthetic */ IntPipeline$$ExternalSyntheticLambda6(Sink sink) {
        this.f$0 = sink;
    }

    public final void accept(int i) {
        this.f$0.accept(i);
    }

    public /* synthetic */ IntConsumer andThen(IntConsumer intConsumer) {
        return IntConsumer.CC.$default$andThen(this, intConsumer);
    }
}
