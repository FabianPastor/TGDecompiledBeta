package j$.util.stream;

/* renamed from: j$.util.stream.r2  reason: case insensitive filesystem */
class CLASSNAMEr2 extends CLASSNAMEn5 {
    final /* synthetic */ CLASSNAMEs2 b;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    CLASSNAMEr2(CLASSNAMEs2 s2Var, CLASSNAMEt5 t5Var) {
        super(t5Var);
        this.b = s2Var;
    }

    public void accept(int i) {
        if (this.b.l.b(i)) {
            this.a.accept(i);
        }
    }

    public void n(long j) {
        this.a.n(-1);
    }
}
