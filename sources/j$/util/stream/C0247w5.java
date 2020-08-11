package j$.util.stream;

import j$.util.function.CLASSNAMEq;
import j$.util.function.CLASSNAMEs;
import j$.util.function.CLASSNAMEt;
import j$.util.function.Consumer;

/* renamed from: j$.util.stream.w5  reason: case insensitive filesystem */
public abstract class CLASSNAMEw5 implements B5 {
    protected final G5 a;

    public /* synthetic */ void accept(int i) {
        CLASSNAMEv5.a(this);
        throw null;
    }

    public /* synthetic */ void accept(long j) {
        CLASSNAMEv5.b(this);
        throw null;
    }

    public /* bridge */ /* synthetic */ void accept(Object obj) {
        v((Double) obj);
    }

    public /* synthetic */ Consumer g(Consumer consumer) {
        return CLASSNAMEq.a(this, consumer);
    }

    public /* synthetic */ CLASSNAMEt p(CLASSNAMEt tVar) {
        return CLASSNAMEs.a(this, tVar);
    }

    public /* synthetic */ void v(Double d) {
        A5.a(this, d);
    }

    public CLASSNAMEw5(G5 downstream) {
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
