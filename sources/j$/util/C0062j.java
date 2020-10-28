package j$.util;

import j$.util.function.Consumer;
import j$.util.function.f;
import j$.util.function.q;

/* renamed from: j$.util.j  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEj implements q {
    public final /* synthetic */ Consumer a;

    public /* synthetic */ CLASSNAMEj(Consumer consumer) {
        this.a = consumer;
    }

    public final void accept(double d) {
        this.a.accept(Double.valueOf(d));
    }

    public q k(q qVar) {
        qVar.getClass();
        return new f(this, qVar);
    }
}
