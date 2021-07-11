package j$.util.stream;

import j$.util.Spliterator;
import j$.util.function.J;
import j$.util.function.Predicate;

/* renamed from: j$.util.stream.t1  reason: case insensitive filesystem */
final class CLASSNAMEt1<T, O> implements g3<T, O> {
    private final U2 a;
    final boolean b;
    final Object c;
    final Predicate d;
    final J e;

    CLASSNAMEt1(boolean z, U2 u2, Object obj, Predicate predicate, J j) {
        this.b = z;
        this.a = u2;
        this.c = obj;
        this.d = predicate;
        this.e = j;
    }

    public int b() {
        return T2.p | (this.b ? 0 : T2.m);
    }

    public Object c(T1 t1, Spliterator spliterator) {
        return new CLASSNAMEv1(this, t1, spliterator).invoke();
    }

    public Object d(T1 t1, Spliterator spliterator) {
        h3 h3Var = (h3) this.e.get();
        CLASSNAMEh1 h1Var = (CLASSNAMEh1) t1;
        h3Var.getClass();
        h1Var.m0(h1Var.u0(h3Var), spliterator);
        Object obj = h3Var.get();
        return obj != null ? obj : this.c;
    }
}
