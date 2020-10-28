package j$.util.stream;

class C2 extends CLASSNAMEo5 {
    final /* synthetic */ D2 b;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    C2(D2 d2, CLASSNAMEt5 t5Var) {
        super(t5Var);
        this.b = d2;
    }

    public void accept(long j) {
        this.a.accept(this.b.l.apply(j));
    }
}
