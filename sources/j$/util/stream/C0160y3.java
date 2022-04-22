package j$.util.stream;

/* renamed from: j$.util.stream.y3  reason: case insensitive filesystem */
class CLASSNAMEy3 extends CLASSNAMEg3 {
    long b;
    long c;
    final /* synthetic */ CLASSNAMEz3 d;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    CLASSNAMEy3(CLASSNAMEz3 z3Var, CLASSNAMEn3 n3Var) {
        super(n3Var);
        this.d = z3Var;
        this.b = z3Var.l;
        long j = z3Var.m;
        this.c = j < 0 ? Long.MAX_VALUE : j;
    }

    public void accept(double d2) {
        long j = this.b;
        if (j == 0) {
            long j2 = this.c;
            if (j2 > 0) {
                this.c = j2 - 1;
                this.a.accept(d2);
                return;
            }
            return;
        }
        this.b = j - 1;
    }

    public void n(long j) {
        this.a.n(C3.c(j, this.d.l, this.c));
    }

    public boolean o() {
        return this.c == 0 || this.a.o();
    }
}
