package j$.util.stream;

import j$.K;

/* renamed from: j$.util.stream.x1  reason: case insensitive filesystem */
class CLASSNAMEx1 extends CLASSNAMEw5 {
    final /* synthetic */ CLASSNAMEy1 b;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    CLASSNAMEx1(CLASSNAMEy1 this$1, G5 downstream) {
        super(downstream);
        this.b = this$1;
    }

    public void accept(double t) {
        this.a.accept(((K) this.b.m).a(t));
    }
}
