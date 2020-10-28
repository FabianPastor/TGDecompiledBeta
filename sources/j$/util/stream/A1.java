package j$.util.stream;

class A1 extends CLASSNAMEm5 {
    final /* synthetic */ B1 b;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    A1(B1 b1, CLASSNAMEt5 t5Var) {
        super(t5Var);
        this.b = b1;
    }

    public void accept(double d) {
        L1 l1 = (L1) this.b.l.apply(d);
        if (l1 != null) {
            try {
                l1.sequential().k(new CLASSNAMEo(this));
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
