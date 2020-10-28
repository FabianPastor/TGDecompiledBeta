package j$.util.stream;

import j$.util.Spliterator;
import j$.util.function.E;
import j$.util.function.Predicate;

final class M1 implements J6 {
    private final CLASSNAMEh6 a;
    final boolean b;
    final Object c;
    final Predicate d;
    final E e;

    M1(boolean z, CLASSNAMEh6 h6Var, Object obj, Predicate predicate, E e2) {
        this.b = z;
        this.a = h6Var;
        this.c = obj;
        this.d = predicate;
        this.e = e2;
    }

    public int b() {
        return CLASSNAMEg6.z | (this.b ? 0 : CLASSNAMEg6.w);
    }

    public Object c(CLASSNAMEi4 i4Var, Spliterator spliterator) {
        return new S1(this, i4Var, spliterator).invoke();
    }

    public Object d(CLASSNAMEi4 i4Var, Spliterator spliterator) {
        K6 k6 = (K6) this.e.get();
        CLASSNAMEh1 h1Var = (CLASSNAMEh1) i4Var;
        k6.getClass();
        h1Var.m0(h1Var.u0(k6), spliterator);
        Object obj = k6.get();
        return obj != null ? obj : this.c;
    }
}
