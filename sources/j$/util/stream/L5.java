package j$.util.stream;

class L5 extends CLASSNAMEy5 {
    long b;
    long c;
    final /* synthetic */ M5 d;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    L5(M5 this$0, G5 downstream) {
        super(downstream);
        this.d = this$0;
        M5 m5 = this.d;
        this.b = m5.m;
        long j = m5.n;
        this.c = j < 0 ? Long.MAX_VALUE : j;
    }

    public void s(long size) {
        this.a.s(Q5.e(size, this.d.m, this.c));
    }

    public void accept(long t) {
        long j = this.b;
        if (j == 0) {
            long j2 = this.c;
            if (j2 > 0) {
                this.c = j2 - 1;
                this.a.accept(t);
                return;
            }
            return;
        }
        this.b = j - 1;
    }

    public boolean u() {
        return this.c == 0 || this.a.u();
    }
}
