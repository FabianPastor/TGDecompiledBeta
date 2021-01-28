package j$.util.stream;

import j$.util.Spliterator;
import java.util.concurrent.CountedCompleter;

/* renamed from: j$.util.stream.v1  reason: case insensitive filesystem */
final class CLASSNAMEv1<P_IN, P_OUT, O> extends CLASSNAMEi1<P_IN, P_OUT, O, CLASSNAMEv1<P_IN, P_OUT, O>> {
    private final CLASSNAMEt1 j;

    CLASSNAMEv1(CLASSNAMEt1 t1Var, T1 t1, Spliterator spliterator) {
        super(t1, spliterator);
        this.j = t1Var;
    }

    CLASSNAMEv1(CLASSNAMEv1 v1Var, Spliterator spliterator) {
        super((CLASSNAMEi1) v1Var, spliterator);
        this.j = v1Var.j;
    }

    private void m(Object obj) {
        boolean z;
        CLASSNAMEk1 k1Var = this;
        while (true) {
            if (k1Var != null) {
                CLASSNAMEk1 c = k1Var.c();
                if (c != null && c.d != k1Var) {
                    z = false;
                    break;
                }
                k1Var = c;
            } else {
                z = true;
                break;
            }
        }
        if (z) {
            l(obj);
        } else {
            j();
        }
    }

    /* access modifiers changed from: protected */
    public Object a() {
        T1 t1 = this.a;
        h3 h3Var = (h3) this.j.e.get();
        t1.t0(h3Var, this.b);
        Object obj = h3Var.get();
        if (!this.j.b) {
            if (obj != null) {
                l(obj);
            }
            return null;
        } else if (obj == null) {
            return null;
        } else {
            m(obj);
            return obj;
        }
    }

    /* access modifiers changed from: protected */
    public CLASSNAMEk1 f(Spliterator spliterator) {
        return new CLASSNAMEv1(this, spliterator);
    }

    /* access modifiers changed from: protected */
    public Object k() {
        return this.j.c;
    }

    public void onCompletion(CountedCompleter countedCompleter) {
        if (this.j.b) {
            CLASSNAMEv1 v1Var = (CLASSNAMEv1) this.d;
            CLASSNAMEv1 v1Var2 = null;
            while (true) {
                if (v1Var != v1Var2) {
                    Object b = v1Var.b();
                    if (b != null && this.j.d.test(b)) {
                        g(b);
                        m(b);
                        break;
                    }
                    v1Var2 = v1Var;
                    v1Var = (CLASSNAMEv1) this.e;
                } else {
                    break;
                }
            }
        }
        this.b = null;
        this.e = null;
        this.d = null;
    }
}
