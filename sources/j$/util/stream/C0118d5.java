package j$.util.stream;

/* renamed from: j$.util.stream.d5  reason: case insensitive filesystem */
class CLASSNAMEd5 extends CLASSNAMEz5 {
    final /* synthetic */ CLASSNAMEe5 b;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    CLASSNAMEd5(CLASSNAMEe5 this$1, G5 downstream) {
        super(downstream);
        this.b = this$1;
    }

    public void accept(Object u) {
        this.a.accept(this.b.m.apply(u));
    }
}
