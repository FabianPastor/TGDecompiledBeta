package j$.util.stream;

import j$.util.function.CLASSNAMEe;
import j$.util.function.CLASSNAMEg;
import j$.util.function.Consumer;
import j$.util.function.v;
import j$.util.function.w;
import j$.util.k;
import j$.util.q;
import j$.util.stream.A2;

/* renamed from: j$.util.stream.n2  reason: case insensitive filesystem */
class CLASSNAMEn2 implements CLASSNAMEu2<Integer, q, CLASSNAMEn2>, A2.f {
    private boolean a;
    private int b;
    final /* synthetic */ v c;

    CLASSNAMEn2(v vVar) {
        this.c = vVar;
    }

    public /* synthetic */ void accept(double d) {
        k.c(this);
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
        k.b(this);
        throw null;
    }

    /* renamed from: b */
    public /* synthetic */ void accept(Integer num) {
        Q1.b(this, num);
    }

    public Consumer f(Consumer consumer) {
        consumer.getClass();
        return new CLASSNAMEe(this, consumer);
    }

    public Object get() {
        return this.a ? q.a() : q.d(this.b);
    }

    public void i(CLASSNAMEu2 u2Var) {
        CLASSNAMEn2 n2Var = (CLASSNAMEn2) u2Var;
        if (!n2Var.a) {
            accept(n2Var.b);
        }
    }

    public w l(w wVar) {
        wVar.getClass();
        return new CLASSNAMEg(this, wVar);
    }

    public void m() {
    }

    public void n(long j) {
        this.a = true;
        this.b = 0;
    }

    public /* synthetic */ boolean p() {
        return false;
    }
}
