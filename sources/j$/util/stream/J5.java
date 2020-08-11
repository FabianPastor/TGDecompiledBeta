package j$.util.stream;

class J5 extends CLASSNAMEx5 {
    long b;
    long c;
    final /* synthetic */ K5 d;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    J5(K5 this$0, G5 downstream) {
        super(downstream);
        this.d = this$0;
        K5 k5 = this.d;
        this.b = k5.m;
        long j = k5.n;
        this.c = j < 0 ? Long.MAX_VALUE : j;
    }

    public void s(long size) {
        this.a.s(Q5.e(size, this.d.m, this.c));
    }

    public void accept(int t) {
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
