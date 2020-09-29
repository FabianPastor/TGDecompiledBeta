package j$.util.stream;

import j$.util.function.CLASSNAMEq;
import j$.util.function.Consumer;

/* renamed from: j$.util.stream.z5  reason: case insensitive filesystem */
public abstract class CLASSNAMEz5 implements G5 {
    protected final G5 a;

    public /* synthetic */ void accept(double d) {
        CLASSNAMEv5.c(this);
        throw null;
    }

    public /* synthetic */ void accept(int i) {
        CLASSNAMEv5.a(this);
        throw null;
    }

    public /* synthetic */ void accept(long j) {
        CLASSNAMEv5.b(this);
        throw null;
    }

    public /* synthetic */ Consumer g(Consumer consumer) {
        return CLASSNAMEq.a(this, consumer);
    }

    public CLASSNAMEz5(G5 downstream) {
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
