package j$.util.stream;

class D2 extends CLASSNAMEy5 {
    final /* synthetic */ E2 b;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    D2(E2 this$1, G5 downstream) {
        super(downstream);
        this.b = this$1;
    }

    public void accept(long t) {
        this.a.accept(this.b.m.applyAsLong(t));
    }
}
