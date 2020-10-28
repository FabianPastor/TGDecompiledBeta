package j$.util.stream;

import j$.util.Spliterator;
import java.util.concurrent.CountedCompleter;

final class M4 extends CLASSNAMEk1 {
    private final L4 h;

    M4(L4 l4, CLASSNAMEi4 i4Var, Spliterator spliterator) {
        super(i4Var, spliterator);
        this.h = l4;
    }

    M4(M4 m4, Spliterator spliterator) {
        super((CLASSNAMEk1) m4, spliterator);
        this.h = m4.h;
    }

    /* access modifiers changed from: protected */
    public Object a() {
        CLASSNAMEi4 i4Var = this.a;
        J4 a = this.h.a();
        i4Var.t0(a, this.b);
        return a;
    }

    /* access modifiers changed from: protected */
    public CLASSNAMEk1 f(Spliterator spliterator) {
        return new M4(this, spliterator);
    }

    public void onCompletion(CountedCompleter countedCompleter) {
        if (!d()) {
            J4 j4 = (J4) ((M4) this.d).b();
            j4.i((J4) ((M4) this.e).b());
            g(j4);
        }
        this.b = null;
        this.e = null;
        this.d = null;
    }
}
