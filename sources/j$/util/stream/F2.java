package j$.util.stream;

class F2 extends CLASSNAMEy5 {
    final /* synthetic */ G2 b;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    F2(G2 this$1, G5 downstream) {
        super(downstream);
        this.b = this$1;
    }

    public void accept(long t) {
        this.a.accept(this.b.m.a(t));
    }
}
