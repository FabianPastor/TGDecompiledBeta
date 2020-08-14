package j$.util.stream;

import j$.util.function.CLASSNAMEq;
import j$.util.function.Consumer;

/* renamed from: j$.util.stream.b3  reason: case insensitive filesystem */
abstract class CLASSNAMEb3 implements G5 {
    boolean a;
    boolean b;

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

    CLASSNAMEb3(CLASSNAMEc3 matchKind) {
        this.b = !matchKind.b;
    }

    public boolean a() {
        return this.b;
    }

    public boolean u() {
        return this.a;
    }
}
