package j$.util.stream;

/* renamed from: j$.util.stream.z1  reason: case insensitive filesystem */
class CLASSNAMEz1 extends CLASSNAMEw5 {
    final /* synthetic */ A1 b;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    CLASSNAMEz1(A1 this$1, G5 downstream) {
        super(downstream);
        this.b = this$1;
    }

    public void accept(double t) {
        this.a.accept(this.b.m.a(t));
    }
}