package j$.util.stream;

class A2 extends CLASSNAMEo5 {
    final /* synthetic */ B2 b;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    A2(B2 b2, CLASSNAMEt5 t5Var) {
        super(t5Var);
        this.b = b2;
    }

    public void accept(long j) {
        this.a.accept(this.b.l.applyAsLong(j));
    }
}
