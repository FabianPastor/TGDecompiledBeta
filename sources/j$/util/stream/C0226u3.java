package j$.util.stream;

import j$.util.Spliterator;
import j$.util.function.n;
import j$.util.function.z;
import java.util.concurrent.CountedCompleter;

/* renamed from: j$.util.stream.u3  reason: case insensitive filesystem */
class CLASSNAMEu3 extends CLASSNAMEk1 {
    protected final CLASSNAMEi4 h;
    protected final z i;
    protected final n j;

    CLASSNAMEu3(CLASSNAMEi4 i4Var, Spliterator spliterator, z zVar, n nVar) {
        super(i4Var, spliterator);
        this.h = i4Var;
        this.i = zVar;
        this.j = nVar;
    }

    CLASSNAMEu3(CLASSNAMEu3 u3Var, Spliterator spliterator) {
        super((CLASSNAMEk1) u3Var, spliterator);
        this.h = u3Var.h;
        this.i = u3Var.i;
        this.j = u3Var.j;
    }

    /* access modifiers changed from: protected */
    public Object a() {
        CLASSNAMEg3 g3Var = (CLASSNAMEg3) this.i.apply(this.h.p0(this.b));
        this.h.t0(g3Var, this.b);
        return g3Var.a();
    }

    /* access modifiers changed from: protected */
    public CLASSNAMEk1 f(Spliterator spliterator) {
        return new CLASSNAMEu3(this, spliterator);
    }

    public void onCompletion(CountedCompleter countedCompleter) {
        if (!d()) {
            g((CLASSNAMEl3) this.j.apply((CLASSNAMEl3) ((CLASSNAMEu3) this.d).b(), (CLASSNAMEl3) ((CLASSNAMEu3) this.e).b()));
        }
        this.b = null;
        this.e = null;
        this.d = null;
    }
}
