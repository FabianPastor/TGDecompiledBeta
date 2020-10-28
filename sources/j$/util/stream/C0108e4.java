package j$.util.stream;

/* renamed from: j$.util.stream.e4  reason: case insensitive filesystem */
class CLASSNAMEe4 extends CLASSNAMEg4 {
    private final Object c;

    private CLASSNAMEe4(CLASSNAMEe4 e4Var, CLASSNAMEk3 k3Var, int i) {
        super(e4Var, k3Var, i);
        this.c = e4Var.c;
    }

    CLASSNAMEe4(CLASSNAMEk3 k3Var, Object obj, int i, CLASSNAMEm3 m3Var) {
        super(k3Var, i);
        this.c = obj;
    }

    /* access modifiers changed from: package-private */
    public void a() {
        ((CLASSNAMEk3) this.a).d(this.c, this.b);
    }

    /* access modifiers changed from: package-private */
    public CLASSNAMEg4 b(int i, int i2) {
        return new CLASSNAMEe4(this, ((CLASSNAMEk3) this.a).b(i), i2);
    }
}
