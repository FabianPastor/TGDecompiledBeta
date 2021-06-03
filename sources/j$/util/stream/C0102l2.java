package j$.util.stream;

import j$.time.a;
import j$.util.function.CLASSNAMEg;
import j$.util.function.Consumer;
import j$.util.function.v;
import j$.util.function.w;
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
        a.c(this);
        throw null;
    }

    public void accept(int i) {
        this.a = this.c.applyAsInt(this.a, i);
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
        return Integer.valueOf(this.a);
    }

    public void h(CLASSNAMEu2 u2Var) {
        accept(((CLASSNAMEl2) u2Var).a);
    }

    public w k(w wVar) {
        wVar.getClass();
        return new CLASSNAMEg(this, wVar);
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
