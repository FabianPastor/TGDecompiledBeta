package j$.util.stream;

import j$.util.function.CLASSNAMEe;
import j$.util.function.Consumer;

/* renamed from: j$.util.stream.y0  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEy0 implements Consumer {
    public final /* synthetic */ Y2 a;
    public final /* synthetic */ Consumer b;

    public /* synthetic */ CLASSNAMEy0(Y2 y2, Consumer consumer) {
        this.a = y2;
        this.b = consumer;
    }

    public final void accept(Object obj) {
        this.a.g(this.b, obj);
    }

    public Consumer f(Consumer consumer) {
        consumer.getClass();
        return new CLASSNAMEe(this, consumer);
    }
}
