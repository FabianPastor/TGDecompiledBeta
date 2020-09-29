package j$.util.stream;

import j$.util.function.CLASSNAMEq;
import j$.util.function.Consumer;
import j$.util.function.I;
import j$.util.function.J;

/* renamed from: j$.util.stream.y5  reason: case insensitive filesystem */
public abstract class CLASSNAMEy5 implements F5 {
    protected final G5 a;

    public /* synthetic */ void accept(double d) {
        CLASSNAMEv5.c(this);
        throw null;
    }

    public /* synthetic */ void accept(int i) {
        CLASSNAMEv5.a(this);
        throw null;
    }

    public /* bridge */ /* synthetic */ void accept(Object obj) {
        n((Long) obj);
    }

    public /* synthetic */ Consumer g(Consumer consumer) {
        return CLASSNAMEq.a(this, consumer);
    }

    public /* synthetic */ J h(J j) {
        return I.a(this, j);
    }

    public /* synthetic */ void n(Long l) {
        E5.a(this, l);
    }

    public CLASSNAMEy5(G5 downstream) {
        downstream.getClass();
        this.a = downstream;
    }

    public void s(long size) {
        this.a.s(size);
    }

    public void r() {
        this.a.r();
    }

    public boolean u() {
        return this.a.u();
    }
}
