package j$.util.stream;

/* renamed from: j$.util.stream.p3  reason: case insensitive filesystem */
class CLASSNAMEp3 extends CLASSNAMEj3 {
    long b;
    long c;
    final /* synthetic */ CLASSNAMEq3 d;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    CLASSNAMEp3(CLASSNAMEq3 q3Var, CLASSNAMEn3 n3Var) {
        super(n3Var);
        this.d = q3Var;
        this.b = q3Var.l;
        long j = q3Var.m;
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
        this.a.n(C3.c(j, this.d.l, this.c));
    }

    public boolean o() {
        return this.c == 0 || this.a.o();
    }
}
