package j$.util.stream;

/* renamed from: j$.util.stream.o2  reason: case insensitive filesystem */
class CLASSNAMEo2 extends CLASSNAMEn5 {
    final /* synthetic */ CLASSNAMEp2 b;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    CLASSNAMEo2(CLASSNAMEp2 p2Var, CLASSNAMEt5 t5Var) {
        super(t5Var);
        this.b = p2Var;
    }

    public void accept(int i) {
        CLASSNAMEx2 x2Var = (CLASSNAMEx2) this.b.l.apply(i);
        if (x2Var != null) {
            try {
                x2Var.sequential().Q(new F(this));
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
