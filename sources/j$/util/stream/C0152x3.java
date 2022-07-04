package j$.util.stream;

/* renamed from: j$.util.stream.x3  reason: case insensitive filesystem */
class CLASSNAMEx3 extends CLASSNAMEf3 {
    long b;
    long c;
    final /* synthetic */ CLASSNAMEy3 d;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    CLASSNAMEx3(CLASSNAMEy3 y3Var, CLASSNAMEm3 m3Var) {
        super(m3Var);
        this.d = y3Var;
        this.b = y3Var.l;
        long j = y3Var.m;
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
        this.a.n(B3.c(j, this.d.l, this.c));
    }

    public boolean o() {
        return this.c == 0 || this.a.o();
    }
}
