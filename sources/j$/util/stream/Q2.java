package j$.util.stream;

class Q2 extends CLASSNAMEy5 {
    final /* synthetic */ R2 b;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    Q2(R2 this$1, G5 downstream) {
        super(downstream);
        this.b = this$1;
    }

    public void accept(long t) {
        this.b.m.accept(t);
        this.a.accept(t);
    }
}
