package j$.util.stream;

import j$.util.function.A;
import j$.util.function.B;
import j$.util.function.CLASSNAMEq;
import j$.util.function.Consumer;

/* renamed from: j$.util.stream.x5  reason: case insensitive filesystem */
public abstract class CLASSNAMEx5 implements D5 {
    protected final G5 a;

    public /* synthetic */ void accept(double d) {
        CLASSNAMEv5.c(this);
        throw null;
    }

    public /* synthetic */ void accept(long j) {
        CLASSNAMEv5.b(this);
        throw null;
    }

    public /* bridge */ /* synthetic */ void accept(Object obj) {
        t((Integer) obj);
    }

    public /* synthetic */ Consumer g(Consumer consumer) {
        return CLASSNAMEq.a(this, consumer);
    }

    public /* synthetic */ B q(B b) {
        return A.a(this, b);
    }

    public /* synthetic */ void t(Integer num) {
        C5.a(this, num);
    }

    public CLASSNAMEx5(G5 downstream) {
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
