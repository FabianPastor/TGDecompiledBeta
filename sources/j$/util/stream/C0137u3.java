package j$.util.stream;

/* renamed from: j$.util.stream.u3  reason: case insensitive filesystem */
class CLASSNAMEu3 extends CLASSNAMEh3 {
    long b;
    long c;
    final /* synthetic */ CLASSNAMEv3 d;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    CLASSNAMEu3(CLASSNAMEv3 v3Var, CLASSNAMEm3 m3Var) {
        super(m3Var);
        this.d = v3Var;
        this.b = v3Var.l;
        long j = v3Var.m;
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
        this.a.n(B3.c(j, this.d.l, this.c));
    }

    public boolean o() {
        return this.c == 0 || this.a.o();
    }
}
