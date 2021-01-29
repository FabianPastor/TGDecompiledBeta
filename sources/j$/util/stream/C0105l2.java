package j$.util.stream;

import j$.util.function.CLASSNAMEe;
import j$.util.function.CLASSNAMEg;
import j$.util.function.Consumer;
import j$.util.function.v;
import j$.util.function.w;
import j$.util.k;
import j$.util.stream.A2;

/* renamed from: j$.util.stream.l2  reason: case insensitive filesystem */
class CLASSNAMEl2 implements CLASSNAMEu2<Integer, Integer, CLASSNAMEl2>, A2.f {
    private int a;
    final /* synthetic */ int b;
    final /* synthetic */ v c;

    CLASSNAMEl2(int i, v vVar) {
        this.b = i;
        this.c = vVar;
    }

    public /* synthetic */ void accept(double d) {
        k.c(this);
        throw null;
    }

    public void accept(int i) {
        this.a = this.c.applyAsInt(this.a, i);
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
        return Integer.valueOf(this.a);
    }

    public void i(CLASSNAMEu2 u2Var) {
        accept(((CLASSNAMEl2) u2Var).a);
    }

    public w l(w wVar) {
        wVar.getClass();
        return new CLASSNAMEg(this, wVar);
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
