package j$.util.stream;

/* renamed from: j$.util.stream.u5  reason: case insensitive filesystem */
class CLASSNAMEu5 extends CLASSNAMEp5 {
    long b;
    long c;
    final /* synthetic */ CLASSNAMEv5 d;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    CLASSNAMEu5(CLASSNAMEv5 v5Var, CLASSNAMEt5 t5Var) {
        super(t5Var);
        this.d = v5Var;
        this.b = v5Var.l;
        long j = v5Var.m;
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
        this.a.n(D5.c(j, this.d.l, this.c));
    }

    public boolean p() {
        return this.c == 0 || this.a.p();
    }
}
