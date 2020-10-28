package j$.util.stream;

import j$.util.C;
import j$.util.CLASSNAMEw;
import j$.util.Spliterator;
import j$.util.function.Consumer;
import j$.util.function.f;
import j$.util.function.q;

final class A6 extends D6 implements C, q {
    double e;

    A6(C c, long j, long j2) {
        super(c, j, j2);
    }

    A6(C c, A6 a6) {
        super(c, a6);
    }

    public void accept(double d) {
        this.e = d;
    }

    public /* synthetic */ boolean b(Consumer consumer) {
        return CLASSNAMEw.d(this, consumer);
    }

    public /* synthetic */ void forEachRemaining(Consumer consumer) {
        CLASSNAMEw.a(this, consumer);
    }

    public q k(q qVar) {
        qVar.getClass();
        return new f(this, qVar);
    }

    /* access modifiers changed from: protected */
    public Spliterator r(Spliterator spliterator) {
        return new A6((C) spliterator, this);
    }

    /* access modifiers changed from: protected */
    public void t(Object obj) {
        ((q) obj).accept(this.e);
    }

    /* access modifiers changed from: protected */
    public n6 u(int i) {
        return new CLASSNAMEk6(i);
    }
}
