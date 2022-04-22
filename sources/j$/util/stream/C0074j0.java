package j$.util.stream;

import j$.util.y;
import java.util.concurrent.CountedCompleter;

/* renamed from: j$.util.stream.j0  reason: case insensitive filesystem */
final class CLASSNAMEj0 extends CLASSNAMEd {
    private final CLASSNAMEd0 j;

    CLASSNAMEj0(CLASSNAMEd0 d0Var, CLASSNAMEz2 z2Var, y yVar) {
        super(z2Var, yVar);
        this.j = d0Var;
    }

    CLASSNAMEj0(CLASSNAMEj0 j0Var, y yVar) {
        super((CLASSNAMEd) j0Var, yVar);
        this.j = j0Var.j;
    }

    private void m(Object obj) {
        boolean z;
        CLASSNAMEf fVar = this;
        while (true) {
            if (fVar != null) {
                CLASSNAMEf c = fVar.c();
                if (c != null && c.d != fVar) {
                    z = false;
                    break;
                }
                fVar = c;
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
        CLASSNAMEz2 z2Var = this.a;
        P4 p4 = (P4) this.j.e.get();
        z2Var.u0(p4, this.b);
        Object obj = p4.get();
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
    public CLASSNAMEf f(y yVar) {
        return new CLASSNAMEj0(this, yVar);
    }

    /* access modifiers changed from: protected */
    public Object k() {
        return this.j.c;
    }

    public void onCompletion(CountedCompleter countedCompleter) {
        if (this.j.b) {
            CLASSNAMEj0 j0Var = (CLASSNAMEj0) this.d;
            CLASSNAMEj0 j0Var2 = null;
            while (true) {
                if (j0Var != j0Var2) {
                    Object b = j0Var.b();
                    if (b != null && this.j.d.test(b)) {
                        g(b);
                        m(b);
                        break;
                    }
                    j0Var2 = j0Var;
                    j0Var = (CLASSNAMEj0) this.e;
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
