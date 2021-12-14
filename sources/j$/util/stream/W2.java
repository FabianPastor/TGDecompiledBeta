package j$.util.stream;

import j$.util.y;
import java.util.concurrent.CountedCompleter;

final class W2 extends CLASSNAMEf {
    private final V2 h;

    W2(V2 v2, CLASSNAMEz2 z2Var, y yVar) {
        super(z2Var, yVar);
        this.h = v2;
    }

    W2(W2 w2, y yVar) {
        super((CLASSNAMEf) w2, yVar);
        this.h = w2.h;
    }

    /* access modifiers changed from: protected */
    public Object a() {
        CLASSNAMEz2 z2Var = this.a;
        T2 a = this.h.a();
        z2Var.u0(a, this.b);
        return a;
    }

    /* access modifiers changed from: protected */
    public CLASSNAMEf f(y yVar) {
        return new W2(this, yVar);
    }

    public void onCompletion(CountedCompleter countedCompleter) {
        if (!d()) {
            T2 t2 = (T2) ((W2) this.d).b();
            t2.h((T2) ((W2) this.e).b());
            g(t2);
        }
        this.b = null;
        this.e = null;
        this.d = null;
    }
}
