package j$.util.stream;

import j$.util.Spliterator;
import j$.util.stream.CLASSNAMEu2;
import java.util.concurrent.CountedCompleter;

/* renamed from: j$.util.stream.x2  reason: case insensitive filesystem */
final class CLASSNAMEx2<P_IN, P_OUT, R, S extends CLASSNAMEu2<P_OUT, R, S>> extends CLASSNAMEk1<P_IN, P_OUT, S, CLASSNAMEx2<P_IN, P_OUT, R, S>> {
    private final CLASSNAMEw2 h;

    CLASSNAMEx2(CLASSNAMEw2 w2Var, T1 t1, Spliterator spliterator) {
        super(t1, spliterator);
        this.h = w2Var;
    }

    CLASSNAMEx2(CLASSNAMEx2 x2Var, Spliterator spliterator) {
        super((CLASSNAMEk1) x2Var, spliterator);
        this.h = x2Var.h;
    }

    /* access modifiers changed from: protected */
    public Object a() {
        T1 t1 = this.var_a;
        CLASSNAMEu2 a2 = this.h.a();
        t1.t0(a2, this.b);
        return a2;
    }

    /* access modifiers changed from: protected */
    public CLASSNAMEk1 f(Spliterator spliterator) {
        return new CLASSNAMEx2(this, spliterator);
    }

    public void onCompletion(CountedCompleter countedCompleter) {
        if (!d()) {
            CLASSNAMEu2 u2Var = (CLASSNAMEu2) ((CLASSNAMEx2) this.d).b();
            u2Var.i((CLASSNAMEu2) ((CLASSNAMEx2) this.e).b());
            g(u2Var);
        }
        this.b = null;
        this.e = null;
        this.d = null;
    }
}
