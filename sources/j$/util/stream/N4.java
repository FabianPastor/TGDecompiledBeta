package j$.util.stream;

import j$.util.function.y;

class N4 extends CLASSNAMEp5 {
    y b;
    final /* synthetic */ O4 c;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    N4(O4 o4, CLASSNAMEt5 t5Var) {
        super(t5Var);
        this.c = o4;
        this.b = new K0(t5Var);
    }

    public void accept(Object obj) {
        T2 t2 = (T2) this.c.l.apply(obj);
        if (t2 != null) {
            try {
                t2.sequential().e(this.b);
            } catch (Throwable unused) {
            }
        }
        if (t2 != null) {
            t2.close();
            return;
        }
        return;
        throw th;
    }

    public void n(long j) {
        this.a.n(-1);
    }
}
