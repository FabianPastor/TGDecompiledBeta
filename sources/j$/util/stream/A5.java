package j$.util.stream;

class A5 extends CLASSNAMEm5 {
    long b;
    long c;
    final /* synthetic */ B5 d;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    A5(B5 b5, CLASSNAMEt5 t5Var) {
        super(t5Var);
        this.d = b5;
        this.b = b5.l;
        long j = b5.m;
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
        this.a.n(D5.c(j, this.d.l, this.c));
    }

    public boolean p() {
        return this.c == 0 || this.a.p();
    }
}
