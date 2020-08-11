package j$.util.stream;

import j$.util.Spliterator;
import j$.util.function.CLASSNAMEq;
import j$.util.function.Consumer;

abstract class Z1 implements f7, g7 {
    private final boolean a;

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

    public /* synthetic */ boolean u() {
        CLASSNAMEv5.e();
        return false;
    }

    public /* bridge */ /* synthetic */ Object c(CLASSNAMEq4 q4Var, Spliterator spliterator) {
        e(q4Var, spliterator);
        return null;
    }

    public /* bridge */ /* synthetic */ Object d(CLASSNAMEq4 q4Var, Spliterator spliterator) {
        f(q4Var, spliterator);
        return null;
    }

    public /* bridge */ /* synthetic */ Object get() {
        i();
        return null;
    }

    protected Z1(boolean ordered) {
        this.a = ordered;
    }

    public int a() {
        if (this.a) {
            return 0;
        }
        return CLASSNAMEu6.w;
    }

    public Void f(CLASSNAMEq4 helper, Spliterator spliterator) {
        ((Z1) helper.t0(this, spliterator)).i();
        return null;
    }

    public Void e(CLASSNAMEq4 helper, Spliterator spliterator) {
        if (this.a) {
            new CLASSNAMEa2(helper, spliterator, (G5) this).invoke();
            return null;
        }
        new CLASSNAMEb2(helper, spliterator, helper.u0(this)).invoke();
        return null;
    }

    public Void i() {
        return null;
    }
}
