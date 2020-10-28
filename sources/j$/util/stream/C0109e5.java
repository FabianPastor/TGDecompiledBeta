package j$.util.stream;

import j$.util.function.u;

/* renamed from: j$.util.stream.e5  reason: case insensitive filesystem */
class CLASSNAMEe5 extends CLASSNAMEp5 {
    u b;
    final /* synthetic */ CLASSNAMEf5 c;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    CLASSNAMEe5(CLASSNAMEf5 f5Var, CLASSNAMEt5 t5Var) {
        super(t5Var);
        this.c = f5Var;
        this.b = new CLASSNAMEc(t5Var);
    }

    public void accept(Object obj) {
        CLASSNAMEx2 x2Var = (CLASSNAMEx2) this.c.l.apply(obj);
        if (x2Var != null) {
            try {
                x2Var.sequential().Q(this.b);
            } catch (Throwable unused) {
            }
        }
        if (x2Var != null) {
            x2Var.close();
            return;
        }
        return;
        throw th;
    }

    public void n(long j) {
        this.a.n(-1);
    }
}
