package j$.util.stream;

/* renamed from: j$.util.stream.v3  reason: case insensitive filesystem */
class CLASSNAMEv3 extends CLASSNAMEi3 {
    long b;
    long c;
    final /* synthetic */ CLASSNAMEw3 d;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    CLASSNAMEv3(CLASSNAMEw3 w3Var, CLASSNAMEn3 n3Var) {
        super(n3Var);
        this.d = w3Var;
        this.b = w3Var.l;
        long j = w3Var.m;
        this.c = j < 0 ? Long.MAX_VALUE : j;
    }

    public void accept(long j) {
        long j2 = this.b;
        if (j2 == 0) {
            long j3 = this.c;
            if (j3 > 0) {
                this.c = j3 - 1;
                this.a.accept(j);
                return;
            }
            return;
        }
        this.b = j2 - 1;
    }

    public void n(long j) {
        this.a.n(C3.c(j, this.d.l, this.c));
    }

    public boolean o() {
        return this.c == 0 || this.a.o();
    }
}
