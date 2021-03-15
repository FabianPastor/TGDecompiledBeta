package j$.util.stream;

import j$.util.Spliterator;
import j$.util.stream.CLASSNAMEu2;

/* renamed from: j$.util.stream.w2  reason: case insensitive filesystem */
abstract class CLASSNAMEw2<T, R, S extends CLASSNAMEu2<T, R, S>> implements g3<T, R> {

    /* renamed from: a  reason: collision with root package name */
    private final U2 var_a;

    CLASSNAMEw2(U2 u2) {
        this.var_a = u2;
    }

    public abstract CLASSNAMEu2 a();

    public /* synthetic */ int b() {
        return 0;
    }

    public Object c(T1 t1, Spliterator spliterator) {
        return ((CLASSNAMEu2) new CLASSNAMEx2(this, t1, spliterator).invoke()).get();
    }

    public Object d(T1 t1, Spliterator spliterator) {
        CLASSNAMEu2 a2 = a();
        CLASSNAMEh1 h1Var = (CLASSNAMEh1) t1;
        a2.getClass();
        h1Var.m0(h1Var.u0(a2), spliterator);
        return a2.get();
    }
}
