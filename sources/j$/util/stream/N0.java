package j$.util.stream;

import j$.util.function.A;
import j$.util.function.B;
import j$.util.function.CLASSNAMEq;
import j$.util.function.Consumer;

public final /* synthetic */ class N0 implements D5 {
    public final /* synthetic */ CLASSNAMEm6 a;

    public /* synthetic */ N0(CLASSNAMEm6 m6Var) {
        this.a = m6Var;
    }

    public /* synthetic */ void accept(double d) {
        CLASSNAMEv5.c(this);
        throw null;
    }

    public final void accept(int i) {
        this.a.accept(i);
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

    public /* synthetic */ void r() {
        CLASSNAMEv5.f();
    }

    public /* synthetic */ void s(long j) {
        CLASSNAMEv5.d();
    }

    public /* synthetic */ void t(Integer num) {
        C5.a(this, num);
    }

    public /* synthetic */ boolean u() {
        CLASSNAMEv5.e();
        return false;
    }
}
