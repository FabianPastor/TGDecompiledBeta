package j$.util.stream;

import j$.util.function.CLASSNAMEq;
import j$.util.function.Consumer;

public final /* synthetic */ class Q0 implements G5 {
    public final /* synthetic */ Consumer a;

    public /* synthetic */ Q0(Consumer consumer) {
        this.a = consumer;
    }

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

    public final void accept(Object obj) {
        this.a.accept(obj);
    }

    public /* synthetic */ Consumer g(Consumer consumer) {
        return CLASSNAMEq.a(this, consumer);
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
