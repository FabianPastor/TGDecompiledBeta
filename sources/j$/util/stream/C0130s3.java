package j$.util.stream;

/* renamed from: j$.util.stream.s3  reason: case insensitive filesystem */
class CLASSNAMEs3 extends CLASSNAMEh3 {
    long b;
    long c;
    final /* synthetic */ CLASSNAMEt3 d;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    CLASSNAMEs3(CLASSNAMEt3 t3Var, CLASSNAMEn3 n3Var) {
        super(n3Var);
        this.d = t3Var;
        this.b = t3Var.l;
        long j = t3Var.m;
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
        this.a.n(C3.c(j, this.d.l, this.c));
    }

    public boolean o() {
        return this.c == 0 || this.a.o();
    }
}
