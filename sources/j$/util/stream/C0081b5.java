package j$.util.stream;

/* renamed from: j$.util.stream.b5  reason: case insensitive filesystem */
class CLASSNAMEb5 extends CLASSNAMEz5 {
    final /* synthetic */ CLASSNAMEc5 b;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    CLASSNAMEb5(CLASSNAMEc5 this$1, G5 downstream) {
        super(downstream);
        this.b = this$1;
    }

    public void s(long size) {
        this.a.s(-1);
    }

    public void accept(Object u) {
        if (this.b.m.test(u)) {
            this.a.accept(u);
        }
    }
}
