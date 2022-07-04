package j$.util;

import j$.util.function.Consumer;
import j$.util.function.DoubleConsumer;

public final /* synthetic */ class PrimitiveIterator$OfDouble$$ExternalSyntheticLambda0 implements DoubleConsumer {
    public final /* synthetic */ Consumer f$0;

    public /* synthetic */ PrimitiveIterator$OfDouble$$ExternalSyntheticLambda0(Consumer consumer) {
        this.f$0 = consumer;
    }

    public final void accept(double d) {
        this.f$0.accept(Double.valueOf(d));
    }

    public /* synthetic */ DoubleConsumer andThen(DoubleConsumer doubleConsumer) {
        return DoubleConsumer.CC.$default$andThen(this, doubleConsumer);
    }
}
