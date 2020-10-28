package j$.util.stream;

/* renamed from: j$.util.stream.y5  reason: case insensitive filesystem */
class CLASSNAMEy5 extends CLASSNAMEo5 {
    long b;
    long c;
    final /* synthetic */ CLASSNAMEz5 d;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    CLASSNAMEy5(CLASSNAMEz5 z5Var, CLASSNAMEt5 t5Var) {
        super(t5Var);
        this.d = z5Var;
        this.b = z5Var.l;
        long j = z5Var.m;
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
        this.a.n(D5.c(j, this.d.l, this.c));
    }

    public boolean p() {
        return this.c == 0 || this.a.p();
    }
}
