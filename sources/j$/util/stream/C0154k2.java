package j$.util.stream;

/* renamed from: j$.util.stream.k2  reason: case insensitive filesystem */
class CLASSNAMEk2 extends CLASSNAMEn5 {
    final /* synthetic */ CLASSNAMEl2 b;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    CLASSNAMEk2(CLASSNAMEl2 l2Var, CLASSNAMEt5 t5Var) {
        super(t5Var);
        this.b = l2Var;
    }

    public void accept(int i) {
        this.a.accept(this.b.l.applyAsLong(i));
    }
}
