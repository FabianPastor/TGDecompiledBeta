package j$.util.function;

import j$.util.function.DoubleConsumer;

public final /* synthetic */ class DoubleConsumer$$ExternalSyntheticLambda0 implements DoubleConsumer {
    public final /* synthetic */ DoubleConsumer f$0;
    public final /* synthetic */ DoubleConsumer f$1;

    public /* synthetic */ DoubleConsumer$$ExternalSyntheticLambda0(DoubleConsumer doubleConsumer, DoubleConsumer doubleConsumer2) {
        this.f$0 = doubleConsumer;
        this.f$1 = doubleConsumer2;
    }

    public final void accept(double d) {
        DoubleConsumer.CC.lambda$andThen$0(this.f$0, this.f$1, d);
    }

    public /* synthetic */ DoubleConsumer andThen(DoubleConsumer doubleConsumer) {
        return DoubleConsumer.CC.$default$andThen(this, doubleConsumer);
    }
}
