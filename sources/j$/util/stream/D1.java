package j$.util.stream;

class D1 extends CLASSNAMEm5 {
    final /* synthetic */ E1 b;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    D1(E1 e1, CLASSNAMEt5 t5Var) {
        super(t5Var);
        this.b = e1;
    }

    public void accept(double d) {
        if (this.b.l.b(d)) {
            this.a.accept(d);
        }
    }

    public void n(long j) {
        this.a.n(-1);
    }
}
