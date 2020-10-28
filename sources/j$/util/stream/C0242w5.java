package j$.util.stream;

/* renamed from: j$.util.stream.w5  reason: case insensitive filesystem */
class CLASSNAMEw5 extends CLASSNAMEn5 {
    long b;
    long c;
    final /* synthetic */ CLASSNAMEx5 d;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    CLASSNAMEw5(CLASSNAMEx5 x5Var, CLASSNAMEt5 t5Var) {
        super(t5Var);
        this.d = x5Var;
        this.b = x5Var.l;
        long j = x5Var.m;
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
        this.a.n(D5.c(j, this.d.l, this.c));
    }

    public boolean p() {
        return this.c == 0 || this.a.p();
    }
}
