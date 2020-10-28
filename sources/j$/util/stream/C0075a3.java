package j$.util.stream;

import j$.util.Spliterator;
import j$.util.function.E;

/* renamed from: j$.util.stream.a3  reason: case insensitive filesystem */
final class CLASSNAMEa3 implements J6 {
    private final CLASSNAMEh6 a;
    final Z2 b;
    final E c;

    CLASSNAMEa3(CLASSNAMEh6 h6Var, Z2 z2, E e) {
        this.a = h6Var;
        this.b = z2;
        this.c = e;
    }

    public int b() {
        return CLASSNAMEg6.z | CLASSNAMEg6.w;
    }

    public Object c(CLASSNAMEi4 i4Var, Spliterator spliterator) {
        return (Boolean) new CLASSNAMEb3(this, i4Var, spliterator).invoke();
    }

    public Object d(CLASSNAMEi4 i4Var, Spliterator spliterator) {
        Y2 y2 = (Y2) this.c.get();
        CLASSNAMEh1 h1Var = (CLASSNAMEh1) i4Var;
        y2.getClass();
        h1Var.m0(h1Var.u0(y2), spliterator);
        return Boolean.valueOf(y2.b);
    }
}
