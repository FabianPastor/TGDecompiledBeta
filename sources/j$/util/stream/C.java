package j$.util.stream;

import j$.util.function.CLASSNAMEq;
import j$.util.function.Consumer;
import j$.util.function.I;
import j$.util.function.J;

public final /* synthetic */ class C implements F5 {
    public final /* synthetic */ J a;

    public /* synthetic */ C(J j) {
        this.a = j;
    }

    public /* synthetic */ void accept(double d) {
        CLASSNAMEv5.c(this);
        throw null;
    }

    public /* synthetic */ void accept(int i) {
        CLASSNAMEv5.a(this);
        throw null;
    }

    public final void accept(long j) {
        this.a.accept(j);
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
}
