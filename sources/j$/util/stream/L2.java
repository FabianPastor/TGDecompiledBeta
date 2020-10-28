package j$.util.stream;

class L2 extends CLASSNAMEo5 {
    final /* synthetic */ M2 b;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    L2(M2 m2, CLASSNAMEt5 t5Var) {
        super(t5Var);
        this.b = m2;
    }

    public void accept(long j) {
        if (this.b.l.b(j)) {
            this.a.accept(j);
        }
    }

    public void n(long j) {
        this.a.n(-1);
    }
}
