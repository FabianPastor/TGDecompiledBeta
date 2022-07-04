package j$.util.stream;

/* renamed from: j$.util.stream.r3  reason: case insensitive filesystem */
class CLASSNAMEr3 extends CLASSNAMEg3 {
    long b;
    long c;
    final /* synthetic */ CLASSNAMEs3 d;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    CLASSNAMEr3(CLASSNAMEs3 s3Var, CLASSNAMEm3 m3Var) {
        super(m3Var);
        this.d = s3Var;
        this.b = s3Var.l;
        long j = s3Var.m;
        this.c = j < 0 ? Long.MAX_VALUE : j;
    }

    public void accept(int i) {
        long j = this.b;
        if (j == 0) {
            long j2 = this.c;
            if (j2 > 0) {
                this.c = j2 - 1;
                this.a.accept(i);
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
