package j$.util.stream;

import j$.util.Spliterator;

abstract class L4 implements J6 {
    private final CLASSNAMEh6 a;

    L4(CLASSNAMEh6 h6Var) {
        this.a = h6Var;
    }

    public abstract J4 a();

    public /* synthetic */ int b() {
        return 0;
    }

    public Object c(CLASSNAMEi4 i4Var, Spliterator spliterator) {
        return ((J4) new M4(this, i4Var, spliterator).invoke()).get();
    }

    public Object d(CLASSNAMEi4 i4Var, Spliterator spliterator) {
        J4 a2 = a();
        CLASSNAMEh1 h1Var = (CLASSNAMEh1) i4Var;
        a2.getClass();
        h1Var.m0(h1Var.u0(a2), spliterator);
        return a2.get();
    }
}
