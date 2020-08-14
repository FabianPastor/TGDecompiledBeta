package j$.util.stream;

import j$.util.function.CLASSNAMEq;
import j$.util.function.CLASSNAMEs;
import j$.util.function.CLASSNAMEt;
import j$.util.function.Consumer;

/* renamed from: j$.util.stream.c1  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEc1 implements B5 {
    public final /* synthetic */ CLASSNAMEk6 a;

    public /* synthetic */ CLASSNAMEc1(CLASSNAMEk6 k6Var) {
        this.a = k6Var;
    }

    public final void accept(double d) {
        this.a.accept(d);
    }

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

    public /* synthetic */ void r() {
        CLASSNAMEv5.f();
    }

    public /* synthetic */ void s(long j) {
        CLASSNAMEv5.d();
    }

    public /* synthetic */ boolean u() {
        CLASSNAMEv5.e();
        return false;
    }

    public /* synthetic */ void v(Double d) {
        A5.a(this, d);
    }
}
