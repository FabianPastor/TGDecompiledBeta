package j$.util.stream;

/* renamed from: j$.util.stream.f4  reason: case insensitive filesystem */
final class CLASSNAMEf4 extends CLASSNAMEg4 {
    private final Object[] c;

    private CLASSNAMEf4(CLASSNAMEf4 f4Var, CLASSNAMEl3 l3Var, int i) {
        super(f4Var, l3Var, i);
        this.c = f4Var.c;
    }

    CLASSNAMEf4(CLASSNAMEl3 l3Var, Object[] objArr, int i, CLASSNAMEm3 m3Var) {
        super(l3Var, i);
        this.c = objArr;
    }

    /* access modifiers changed from: package-private */
    public void a() {
        this.a.j(this.c, this.b);
    }

    /* access modifiers changed from: package-private */
    public CLASSNAMEg4 b(int i, int i2) {
        return new CLASSNAMEf4(this, this.a.b(i), i2);
    }
}
