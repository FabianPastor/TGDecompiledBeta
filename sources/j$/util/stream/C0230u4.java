package j$.util.stream;

import j$.util.function.CLASSNAMEq;
import j$.util.function.CLASSNAMEs;
import j$.util.function.CLASSNAMEt;
import j$.util.function.Consumer;
import j$.util.function.r;

/* renamed from: j$.util.stream.u4  reason: case insensitive filesystem */
class CLASSNAMEu4 implements R4, B5 {
    private double a;
    final /* synthetic */ double b;
    final /* synthetic */ r c;

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

    public /* synthetic */ boolean u() {
        CLASSNAMEv5.e();
        return false;
    }

    public /* synthetic */ void v(Double d) {
        A5.a(this, d);
    }

    CLASSNAMEu4(double d, r rVar) {
        this.b = d;
        this.c = rVar;
    }

    public void s(long size) {
        this.a = this.b;
    }

    public void accept(double t) {
        this.a = this.c.a(this.a, t);
    }

    /* renamed from: c */
    public Double get() {
        return Double.valueOf(this.a);
    }

    /* renamed from: a */
    public void l(CLASSNAMEu4 other) {
        accept(other.a);
    }
}
