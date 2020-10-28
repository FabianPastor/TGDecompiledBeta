package j$.util.stream;

class S4 extends CLASSNAMEp5 {
    final /* synthetic */ T4 b;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    S4(T4 t4, CLASSNAMEt5 t5Var) {
        super(t5Var);
        this.b = t4;
    }

    public void accept(Object obj) {
        if (this.b.l.test(obj)) {
            this.a.accept(obj);
        }
    }

    public void n(long j) {
        this.a.n(-1);
    }
}
