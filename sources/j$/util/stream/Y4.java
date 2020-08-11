package j$.util.stream;

class Y4 extends CLASSNAMEz5 {
    final /* synthetic */ Z4 b;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    Y4(Z4 this$1, G5 downstream) {
        super(downstream);
        this.b = this$1;
    }

    public void accept(Object u) {
        this.b.m.accept(u);
        this.a.accept(u);
    }
}
