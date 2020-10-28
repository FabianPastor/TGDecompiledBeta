package j$.util.stream;

/* renamed from: j$.util.stream.i2  reason: case insensitive filesystem */
class CLASSNAMEi2 extends CLASSNAMEn5 {
    final /* synthetic */ CLASSNAMEj2 b;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    CLASSNAMEi2(CLASSNAMEj2 j2Var, CLASSNAMEt5 t5Var) {
        super(t5Var);
        this.b = j2Var;
    }

    public void accept(int i) {
        this.a.accept(this.b.l.apply(i));
    }
}
