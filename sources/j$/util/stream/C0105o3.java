package j$.util.stream;

/* renamed from: j$.util.stream.o3  reason: case insensitive filesystem */
class CLASSNAMEo3 extends CLASSNAMEi3 {
    long b;
    long c;
    final /* synthetic */ CLASSNAMEp3 d;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    CLASSNAMEo3(CLASSNAMEp3 p3Var, CLASSNAMEm3 m3Var) {
        super(m3Var);
        this.d = p3Var;
        this.b = p3Var.l;
        long j = p3Var.m;
        this.c = j < 0 ? Long.MAX_VALUE : j;
    }

    public void accept(Object obj) {
        long j = this.b;
        if (j == 0) {
            long j2 = this.c;
            if (j2 > 0) {
                this.c = j2 - 1;
                this.a.accept(obj);
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
