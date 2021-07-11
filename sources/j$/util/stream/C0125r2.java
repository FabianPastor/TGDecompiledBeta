package j$.util.stream;

import j$.time.a;
import j$.util.function.B;
import j$.util.function.C;
import j$.util.function.CLASSNAMEh;
import j$.util.function.Consumer;
import j$.util.stream.A2;

/* renamed from: j$.util.stream.r2  reason: case insensitive filesystem */
class CLASSNAMEr2 implements CLASSNAMEu2<Long, Long, CLASSNAMEr2>, A2.g {
    private long a;
    final /* synthetic */ long b;
    final /* synthetic */ B c;

    CLASSNAMEr2(long j, B b2) {
        this.b = j;
        this.c = b2;
    }

    public /* synthetic */ void accept(double d) {
        a.c(this);
        throw null;
    }

    public /* synthetic */ void accept(int i) {
        a.a(this);
        throw null;
    }

    public void accept(long j) {
        this.a = this.c.applyAsLong(this.a, j);
    }

    public /* synthetic */ Consumer andThen(Consumer consumer) {
        return Consumer.CC.$default$andThen(this, consumer);
    }

    /* renamed from: b */
    public /* synthetic */ void accept(Long l) {
        Q1.c(this, l);
    }

    public C f(C c2) {
        c2.getClass();
        return new CLASSNAMEh(this, c2);
    }

    public Object get() {
        return Long.valueOf(this.a);
    }

    public void h(CLASSNAMEu2 u2Var) {
        accept(((CLASSNAMEr2) u2Var).a);
    }

    public void l() {
    }

    public void m(long j) {
        this.a = this.b;
    }

    public /* synthetic */ boolean o() {
        return false;
    }
}
