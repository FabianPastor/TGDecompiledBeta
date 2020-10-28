package j$.util.stream;

import j$.util.CLASSNAMEk;
import j$.util.Spliterator;
import j$.util.function.CLASSNAMEe;
import j$.util.function.Consumer;

abstract class X1 implements J6, K6 {
    private final boolean a;

    protected X1(boolean z) {
        this.a = z;
    }

    public /* synthetic */ void accept(double d) {
        CLASSNAMEk.c(this);
        throw null;
    }

    public /* synthetic */ void accept(int i) {
        CLASSNAMEk.a(this);
        throw null;
    }

    public /* synthetic */ void accept(long j) {
        CLASSNAMEk.b(this);
        throw null;
    }

    public int b() {
        if (this.a) {
            return 0;
        }
        return CLASSNAMEg6.w;
    }

    public Object c(CLASSNAMEi4 i4Var, Spliterator spliterator) {
        (this.a ? new Y1(i4Var, spliterator, (CLASSNAMEt5) this) : new Z1(i4Var, spliterator, i4Var.u0(this))).invoke();
        return null;
    }

    public Object d(CLASSNAMEi4 i4Var, Spliterator spliterator) {
        CLASSNAMEh1 h1Var = (CLASSNAMEh1) i4Var;
        h1Var.m0(h1Var.u0(this), spliterator);
        return null;
    }

    public Consumer f(Consumer consumer) {
        consumer.getClass();
        return new CLASSNAMEe(this, consumer);
    }

    public /* bridge */ /* synthetic */ Object get() {
        return null;
    }

    public void m() {
    }

    public void n(long j) {
    }

    public /* synthetic */ boolean p() {
        return false;
    }
}
