package j$.util.stream;

import j$.util.function.CLASSNAMEq;
import j$.util.function.Consumer;

/* renamed from: j$.util.stream.v0  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEv0 implements Consumer {
    public final /* synthetic */ J6 a;
    public final /* synthetic */ Consumer b;

    public /* synthetic */ CLASSNAMEv0(J6 j6, Consumer consumer) {
        this.a = j6;
        this.b = consumer;
    }

    public final void accept(Object obj) {
        this.a.h(this.b, obj);
    }

    public /* synthetic */ Consumer g(Consumer consumer) {
        return CLASSNAMEq.a(this, consumer);
    }
}
