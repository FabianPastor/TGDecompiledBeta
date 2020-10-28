package j$.util.stream;

/* renamed from: j$.util.stream.a5  reason: case insensitive filesystem */
class CLASSNAMEa5 extends CLASSNAMEp5 {
    final /* synthetic */ CLASSNAMEb5 b;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    CLASSNAMEa5(CLASSNAMEb5 b5Var, CLASSNAMEt5 t5Var) {
        super(t5Var);
        this.b = b5Var;
    }

    public void accept(Object obj) {
        this.a.accept(this.b.l.applyAsDouble(obj));
    }
}
