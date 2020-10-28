package j$.util.stream;

import j$.util.Spliterator;
import java.util.concurrent.CountedCompleter;

final class S1 extends CLASSNAMEi1 {
    private final M1 j;

    S1(M1 m1, CLASSNAMEi4 i4Var, Spliterator spliterator) {
        super(i4Var, spliterator);
        this.j = m1;
    }

    S1(S1 s1, Spliterator spliterator) {
        super((CLASSNAMEi1) s1, spliterator);
        this.j = s1.j;
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
        CLASSNAMEi4 i4Var = this.a;
        K6 k6 = (K6) this.j.e.get();
        i4Var.t0(k6, this.b);
        Object obj = k6.get();
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
        return new S1(this, spliterator);
    }

    /* access modifiers changed from: protected */
    public Object k() {
        return this.j.c;
    }

    public void onCompletion(CountedCompleter countedCompleter) {
        if (this.j.b) {
            S1 s1 = (S1) this.d;
            S1 s12 = null;
            while (true) {
                if (s1 != s12) {
                    Object b = s1.b();
                    if (b != null && this.j.d.test(b)) {
                        g(b);
                        m(b);
                        break;
                    }
                    s12 = s1;
                    s1 = (S1) this.e;
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
