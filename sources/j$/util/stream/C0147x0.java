package j$.util.stream;

import j$.util.function.Consumer;

/* renamed from: j$.util.stream.x0  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEx0 implements Consumer {
    public final /* synthetic */ Y2 a;
    public final /* synthetic */ Consumer b;

    public /* synthetic */ CLASSNAMEx0(Y2 y2, Consumer consumer) {
        this.a = y2;
        this.b = consumer;
    }

    public final void accept(Object obj) {
        this.a.f(this.b, obj);
    }

    public /* synthetic */ Consumer andThen(Consumer consumer) {
        return Consumer.CC.$default$andThen(this, consumer);
    }
}
