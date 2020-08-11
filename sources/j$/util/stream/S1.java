package j$.util.stream;

import j$.util.function.CLASSNAMEq;
import j$.util.function.Consumer;

abstract class S1 implements g7 {
    boolean a;
    Object b;

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

    public /* synthetic */ void r() {
        CLASSNAMEv5.f();
    }

    public /* synthetic */ void s(long j) {
        CLASSNAMEv5.d();
    }

    S1() {
    }

    public void accept(Object value) {
        if (!this.a) {
            this.a = true;
            this.b = value;
        }
    }

    public boolean u() {
        return this.a;
    }
}
