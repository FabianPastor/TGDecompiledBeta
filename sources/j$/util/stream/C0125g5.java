package j$.util.stream;

import j$.util.function.q;

/* renamed from: j$.util.stream.g5  reason: case insensitive filesystem */
class CLASSNAMEg5 extends CLASSNAMEp5 {
    q b;
    final /* synthetic */ CLASSNAMEh5 c;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    CLASSNAMEg5(CLASSNAMEh5 h5Var, CLASSNAMEt5 t5Var) {
        super(t5Var);
        this.c = h5Var;
        this.b = new N(t5Var);
    }

    public void accept(Object obj) {
        L1 l1 = (L1) this.c.l.apply(obj);
        if (l1 != null) {
            try {
                l1.sequential().k(this.b);
            } catch (Throwable unused) {
            }
        }
        if (l1 != null) {
            l1.close();
            return;
        }
        return;
        throw th;
    }

    public void n(long j) {
        this.a.n(-1);
    }
}
