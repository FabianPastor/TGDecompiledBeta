package j$.util.stream;

class G1 extends CLASSNAMEw5 {
    final /* synthetic */ H1 b;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    G1(H1 this$1, G5 downstream) {
        super(downstream);
        this.b = this$1;
    }

    public void accept(double t) {
        this.b.m.accept(t);
        this.a.accept(t);
    }
}
