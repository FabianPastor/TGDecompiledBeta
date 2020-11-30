package j$.util.stream;

import j$.util.Spliterator;
import j$.util.function.J;

final class O1<T> implements g3<T, Boolean> {
    private final U2 a;
    final N1 b;
    final J c;

    O1(U2 u2, N1 n1, J j) {
        this.a = u2;
        this.b = n1;
        this.c = j;
    }

    public int b() {
        return T2.u | T2.r;
    }

    public Object c(T1 t1, Spliterator spliterator) {
        return (Boolean) new P1(this, t1, spliterator).invoke();
    }

    public Object d(T1 t1, Spliterator spliterator) {
        M1 m1 = (M1) this.c.get();
        CLASSNAMEh1 h1Var = (CLASSNAMEh1) t1;
        m1.getClass();
        h1Var.m0(h1Var.u0(m1), spliterator);
        return Boolean.valueOf(m1.b);
    }
}
