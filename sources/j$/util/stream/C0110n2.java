package j$.util.stream;

import j$.time.a;
import j$.util.function.CLASSNAMEg;
import j$.util.function.Consumer;
import j$.util.function.v;
import j$.util.function.w;
import j$.util.p;
import j$.util.stream.A2;

/* renamed from: j$.util.stream.n2  reason: case insensitive filesystem */
class CLASSNAMEn2 implements CLASSNAMEu2<Integer, p, CLASSNAMEn2>, A2.f {
    private boolean a;
    private int b;
    final /* synthetic */ v c;

    CLASSNAMEn2(v vVar) {
        this.c = vVar;
    }

    public /* synthetic */ void accept(double d) {
        a.c(this);
        throw null;
    }

    public void accept(int i) {
        if (this.a) {
            this.a = false;
        } else {
            i = this.c.applyAsInt(this.b, i);
        }
        this.b = i;
    }

    public /* synthetic */ void accept(long j) {
        a.b(this);
        throw null;
    }

    public /* synthetic */ Consumer andThen(Consumer consumer) {
        return Consumer.CC.$default$andThen(this, consumer);
    }

    /* renamed from: b */
    public /* synthetic */ void accept(Integer num) {
        Q1.b(this, num);
    }

    public Object get() {
        return this.a ? p.a() : p.d(this.b);
    }

    public void h(CLASSNAMEu2 u2Var) {
        CLASSNAMEn2 n2Var = (CLASSNAMEn2) u2Var;
        if (!n2Var.a) {
            accept(n2Var.b);
        }
    }

    public w k(w wVar) {
        wVar.getClass();
        return new CLASSNAMEg(this, wVar);
    }

    public void l() {
    }

    public void m(long j) {
        this.a = true;
        this.b = 0;
    }

    public /* synthetic */ boolean o() {
        return false;
    }
}
