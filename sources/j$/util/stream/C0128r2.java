package j$.util.stream;

import j$.util.function.B;
import j$.util.function.C;
import j$.util.function.CLASSNAMEe;
import j$.util.function.CLASSNAMEh;
import j$.util.function.Consumer;
import j$.util.k;
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
        k.c(this);
        throw null;
    }

    public /* synthetic */ void accept(int i) {
        k.a(this);
        throw null;
    }

    public void accept(long j) {
        this.a = this.c.applyAsLong(this.a, j);
    }

    /* renamed from: b */
    public /* synthetic */ void accept(Long l) {
        Q1.c(this, l);
    }

    public Consumer f(Consumer consumer) {
        consumer.getClass();
        return new CLASSNAMEe(this, consumer);
    }

    public C g(C c2) {
        c2.getClass();
        return new CLASSNAMEh(this, c2);
    }

    public Object get() {
        return Long.valueOf(this.a);
    }

    public void i(CLASSNAMEu2 u2Var) {
        accept(((CLASSNAMEr2) u2Var).a);
    }

    public void m() {
    }

    public void n(long j) {
        this.a = this.b;
    }

    public /* synthetic */ boolean p() {
        return false;
    }
}
